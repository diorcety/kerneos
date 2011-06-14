/**
 * Kerneos
 * Copyright (C) 2011 Bull S.A.S.
 * Contact: jasmine@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */

package org.ow2.kerneos.core.service.impl;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.granite.gravity.osgi.adapters.jms.JMSConstants;


import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.service.GraniteFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ow2.kerneos.core.service.KerneosAsynchronous;
import org.ow2.kerneos.core.service.KerneosAsynchronousService;
import org.ow2.kerneos.core.service.KerneosFactory;
import org.ow2.kerneos.core.service.KerneosFactoryService;
import org.ow2.kerneos.core.service.KerneosService;
import org.ow2.kerneos.core.service.KerneosSimpleService;

import org.ow2.kerneos.core.service.impl.granite.GraniteFactoryWrapper;
import org.ow2.kerneos.core.service.impl.granite.GraniteSimpleWrapper;
import org.ow2.kerneos.core.service.util.ClassAnalyzer;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

/**
 * This is the component which handles the arrival/departure of the Kerneos services.
 */
@Component
@Instantiate
public final class KerneosServiceFactory {

    private static Log logger = LogFactory.getLog(KerneosServiceFactory.class);

    /**
     * Used for holding the different configuration/service associated with a Kerneos service.
     */
    private class ServiceInstance {
        private Configuration conf1;
        private Configuration conf2;
        private ServiceRegistration instance;

        /**
         * Constructor
         *
         * @param instance is an OSGi service instance.
         * @param conf1    is an iPojo component instance.
         * @param conf2    is an iPojo component instance.
         */
        public ServiceInstance(final ServiceRegistration instance,
                               final Configuration conf1,
                               final Configuration conf2) {
            this.conf1 = conf1;
            this.conf2 = conf2;
            this.instance = instance;
        }

        /**
         * Dispose all the service and the configuration associated with this instance.
         */
        public void dispose() throws IOException {
            if (conf1 != null) {
                conf1.delete();
            }
            if (conf2 != null) {
                conf2.delete();
            }
            if (instance != null) {
                instance.unregister();
            }
        }
    }

    private Map<String, ServiceInstance> servicesMap = new Hashtable<String, ServiceInstance>();

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private GraniteClassRegistry gcr;

    private BundleContext bundleContext;


    /**
     * Constructor used by iPojo.
     *
     * @param bundleContext is the current bundle context.
     */
    private KerneosServiceFactory(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() {
        logger.debug("Start KerneosServiceFactory");
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() {
        logger.debug("Stop KerneosServiceFactory");
    }

    /**
     * Called when a new Kerneos Simple Service is registered.
     *
     * @param service the instance of the service
     */
    @Bind(aggregate = true, optional = true)
    private void bindSimple(final KerneosSimpleService service) {
        KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
        if (ks == null) {
            logger.error("Invalid Kerneos Simple Service: " + service);
            return;
        }

        String serviceId = ks.destination();
        logger.debug("New Kerneos Simple Service: " + serviceId);

        registerClasses(serviceId, service);

        try {
            ServiceRegistration instance = bundleContext.registerService(
                    GraniteFactory.class.getName(),
                    new GraniteSimpleWrapper(service, serviceId + KerneosConstants.FACTORY_SUFFIX),
                    null);

            Configuration factoryConfiguration;
            {
                Dictionary properties = new Hashtable();
                properties.put("id", serviceId + KerneosConstants.FACTORY_SUFFIX);
                factoryConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Factory", null);
                factoryConfiguration.update(properties);
            }

            Configuration destinationConfiguration;
            {
                Dictionary properties = new Hashtable();
                properties.put("id", serviceId);
                properties.put("service", KerneosConstants.GRANITE_SERVICE);
                properties.put("factory", serviceId + KerneosConstants.FACTORY_SUFFIX);
                properties.put("scope", "application");
                destinationConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
                destinationConfiguration.update(properties);
            }

            synchronized (servicesMap) {
                servicesMap.put(serviceId,
                        new ServiceInstance(instance, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * Called when a Kerneos Simple Service isn't registered anymore.
     *
     * @param service the instance of the service
     */
    @Unbind
    private void unbindSimple(final KerneosSimpleService service) throws IOException {
        KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
        if (ks == null) {
            logger.error("Invalid Kerneos Simple Service: " + service);
            return;
        }

        String serviceId = ks.destination();

        logger.debug("Remove Kerneos Simple Service: " + serviceId);

        ServiceInstance ksi = null;
        synchronized (servicesMap) {
            ksi = servicesMap.get(serviceId);
        }
        if (ksi == null) {
            logger.warn("Try to remove an invalid Kerneos Simple Service: " + serviceId);
            return;
        }

        unregisterClasses(serviceId);

        ksi.dispose();
    }

    /**
     * Called when a Kerneos Factory Service is registered.
     *
     * @param service the instance of the service
     */
    @Bind(aggregate = true, optional = true)
    private void bindFactory(final KerneosFactoryService service) {
        KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
        if (ks == null) {
            logger.error("Invalid Kerneos Factory Service: " + service);
            return;
        }

        String serviceId = ks.destination();
        logger.debug("New Kerneos Factory Service: " + serviceId);

        registerClasses(serviceId, service);

        try {
            ServiceRegistration instance = bundleContext.registerService(
                    GraniteFactory.class.getName(),
                    new GraniteFactoryWrapper(service, serviceId + KerneosConstants.FACTORY_SUFFIX),
                    null);
            Configuration factoryConfiguration;
            {
                Dictionary properties = new Hashtable();
                properties.put("id", serviceId + KerneosConstants.FACTORY_SUFFIX);
                factoryConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Factory", null);
                factoryConfiguration.update(properties);
            }

            // Get Scope property
            String scope = "request";
            KerneosFactory annoProps = service.getClass().getAnnotation(KerneosFactory.class);
            if (annoProps != null) {
                switch (annoProps.scope()) {
                    case APPLICATION:
                        scope = "application";
                        break;
                    case SESSION:
                        scope = "session";
                        break;
                    default:
                        scope = "request";

                }
            }

            Configuration destinationConfiguration;
            {
                Dictionary properties = new Hashtable();
                properties.put("id", serviceId);
                properties.put("service", KerneosConstants.GRANITE_SERVICE);
                properties.put("factory", serviceId + KerneosConstants.FACTORY_SUFFIX);
                properties.put("scope", scope);
                destinationConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
                destinationConfiguration.update(properties);
            }

            synchronized (servicesMap) {
                servicesMap.put(serviceId,
                        new ServiceInstance(instance, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e);
        }

    }

    /**
     * Called when a Kerneos Factory Service isn't registered anymore.
     *
     * @param service the instance of the service
     */
    @Unbind
    private void unbindFactory(final KerneosFactoryService service) throws IOException {
        KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
        if (ks == null) {
            logger.error("Invalid Kerneos Factory Service: " + service);
            return;
        }

        String serviceId = ks.destination();

        logger.debug("Remove Kerneos Factory Service: " + serviceId);

        ServiceInstance kfi = null;
        synchronized (servicesMap) {
            kfi = servicesMap.get(serviceId);
        }
        if (kfi == null) {
            logger.warn("Try to remove an invalid Kerneos Factory Service: " + serviceId);
            return;
        }

        unregisterClasses(serviceId);

        kfi.dispose();
    }


    /**
     * Called when a Kerneos Asynchronous Service is registered.
     *
     * @param service the instance of the service
     */
    @Bind(aggregate = true, optional = true)
    private void bindAsynchronous(final KerneosAsynchronousService service) {
        KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
        KerneosAsynchronous ka = service.getClass().getAnnotation(KerneosAsynchronous.class);
        if (ks == null || ka == null) {
            logger.error("Invalid Kerneos Asynchronous Service: " + service);
            return;
        }

        String serviceId = ks.destination();
        logger.debug("New Kerneos Asynchronous Service: " + serviceId);

        registerClasses(serviceId, service);

        try {

            Configuration factoryConfiguration = null;
            {
                Dictionary properties = new Hashtable();
                for (KerneosAsynchronous.Property property : ka.properties()) {
                    properties.put(property.name(), property.value());
                }

                switch (ka.type()) {
                    case JMS:
                        properties.put("destination", serviceId);
                        factoryConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.gravity.osgi.adapters.jms.configuration", null);
                        factoryConfiguration.update(properties);
                        break;
                    case EVENTADMIN:
                        properties.put("destination", serviceId);
                        factoryConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.gravity.osgi.adapters.ea.configuration", null);
                        factoryConfiguration.update(properties);
                        break;
                    default:
                        break;
                }
            }

            String adapter = null;
            switch (ka.type()) {
                case JMS:
                    adapter = JMSConstants.ADAPTER_ID;
                    break;
                case EVENTADMIN:
                    adapter = EAConstants.ADAPTER_ID;
                    break;
                default:
                    break;
            }

            Configuration destinationConfiguration;
            {
                Dictionary properties = new Hashtable();
                properties.put("id", serviceId);
                properties.put("service", KerneosConstants.GRAVITY_SERVICE);
                properties.put("adapter", adapter);
                destinationConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
                destinationConfiguration.update(properties);
            }

            synchronized (servicesMap) {
                servicesMap.put(serviceId, new ServiceInstance(null, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * Called when a Kerneos Asynchronous Service isn't registered anymore.
     *
     * @param service the instance of the service
     */
    @Unbind
    private void unbindAsynchronous(final KerneosAsynchronousService service) throws IOException {
        KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
        if (ks == null) {
            logger.warn("Invalid Kerneos Asynchronous Service: " + service);
        }

        String serviceId = ks.destination();

        logger.debug("Remove Kerneos Asynchronous Service: " + serviceId);

        ServiceInstance ksi = null;
        synchronized (servicesMap) {
            ksi = servicesMap.get(serviceId);
        }
        if (ksi == null) {
            logger.warn("Try to remove an invalid Kerneos Asynchronous Service: " + serviceId);
            return;
        }

        unregisterClasses(serviceId);

        ksi.dispose();
    }


    /**
     * Register the classes associated to a service
     *
     * @param serviceId the service id
     * @param service   the instance of the service
     */
    private void registerClasses(final String serviceId, final Object service) {
        KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
        if (ks != null) {
            ClassAnalyzer ca = new ClassAnalyzer();
            if (ks.analyze()) {
                ca.analyze(service.getClass());
            }
            for (Class cls : ks.classes()) {
                ca.add(cls);
            }
            gcr.registerClasses(serviceId, ca.compile());
        }
    }

    /**
     * Unregister the classes associated to a service
     *
     * @param serviceId the service id
     */
    private void unregisterClasses(final String serviceId) {
        gcr.unregisterClasses(serviceId);
    }
}
