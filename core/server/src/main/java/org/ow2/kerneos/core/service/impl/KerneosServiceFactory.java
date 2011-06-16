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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.core.IModuleInstance;
import org.ow2.kerneos.core.config.generated.Service;
import org.ow2.kerneos.core.config.generated.SwfModule;
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
import java.util.HashMap;
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
    private Map<Bundle, IModuleInstance> moduleMap = new HashMap<Bundle, IModuleInstance>();

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

    @Bind(aggregate = true, optional = true)
    private void bindModule(final IModuleInstance moduleInstance) {
        synchronized (moduleMap) {
            moduleMap.put(moduleInstance.getBundle(), moduleInstance);
        }
    }

    @Unbind
    private void unbindModule(final IModuleInstance moduleInstance) {
        synchronized (moduleMap) {
            moduleMap.remove(moduleInstance.getBundle());
        }
    }

    /**
     * Get the destination associated to a service.
     *
     * @param serviceRef the ServiceReference of the Kerneos service
     * @param serviceId  the id of service
     * @return the name of the destination
     * @throws Exception Invalid kerneos service
     */
    private String getDestination(final ServiceReference serviceRef, final String serviceId) throws Exception {
        IModuleInstance moduleInstance = moduleMap.get(serviceRef.getBundle());
        if (moduleInstance == null)
            throw new Exception("Can't find the Module associated to the service: " + serviceId);
        if (!(moduleInstance.getConfiguration() instanceof SwfModule))
            throw new Exception("Invalid type of Module(not SwfModule) for the Kerneos service: " + serviceId);
        SwfModule swfModule = (SwfModule) moduleInstance.getConfiguration();
        for (Service service : swfModule.getServices()) {
            if (service.getId().equals(serviceId))
                return service.getDestination();
        }
        throw new Exception("Service \"" + serviceId + "\" not found in: " + moduleInstance.getId());
    }

    /**
     * Called when a new Kerneos Simple Service is registered.
     *
     * @param service the instance of the service
     */
    @Bind(aggregate = true, optional = true)
    private void bindSimple(final KerneosSimpleService service, final ServiceReference serviceRef) {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            if (ks == null) {
                logger.error("Invalid Kerneos Simple Service: " + service);
                return;
            }

            logger.debug("New Kerneos Simple Service: " + ks.id());

            String destination = getDestination(serviceRef, ks.id());
            registerClasses(destination, service);

            ServiceRegistration instance = bundleContext.registerService(
                    GraniteFactory.class.getName(),
                    new GraniteSimpleWrapper(service, destination + KerneosConstants.FACTORY_SUFFIX),
                    null);

            Configuration factoryConfiguration;
            {
                Dictionary properties = new Hashtable();
                properties.put("id", destination + KerneosConstants.FACTORY_SUFFIX);
                factoryConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Factory", null);
                factoryConfiguration.update(properties);
            }

            Configuration destinationConfiguration;
            {
                Dictionary properties = new Hashtable();
                properties.put("id", destination);
                properties.put("service", KerneosConstants.GRANITE_SERVICE);
                properties.put("factory", destination + KerneosConstants.FACTORY_SUFFIX);
                properties.put("scope", "application");
                destinationConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
                destinationConfiguration.update(properties);
            }

            synchronized (servicesMap) {
                servicesMap.put(ks.id(), new ServiceInstance(instance, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e, "Can't register a Simple Service");
        }
    }

    /**
     * Called when a Kerneos Simple Service isn't registered anymore.
     *
     * @param service the instance of the service
     */
    @Unbind
    private void unbindSimple(final KerneosSimpleService service, final ServiceReference serviceRef) throws IOException {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            if (ks == null) {
                logger.error("Invalid Kerneos Simple Service: " + service);
                return;
            }


            logger.debug("Remove Kerneos Simple Service: " + ks.id());

            String destination = getDestination(serviceRef, ks.id());
            ServiceInstance ksi = null;
            synchronized (servicesMap) {
                ksi = servicesMap.get(ks.id());
            }
            if (ksi == null) {
                logger.warn("Try to remove an invalid Kerneos Simple Service: " + ks.id());
                return;
            }

            unregisterClasses(destination);

            ksi.dispose();
        } catch (Exception e) {
            logger.error(e, "Can't unregister a Simple Service");
        }
    }

    /**
     * Called when a Kerneos Factory Service is registered.
     *
     * @param service the instance of the service
     */
    @Bind(aggregate = true, optional = true)
    private void bindFactory(final KerneosFactoryService service, final ServiceReference serviceRef) {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            if (ks == null) {
                logger.error("Invalid Kerneos Factory Service: " + service);
                return;
            }

            logger.debug("New Kerneos Factory Service: " + ks.id());

            String destination = getDestination(serviceRef, ks.id());
            registerClasses(destination, service);

            ServiceRegistration instance = bundleContext.registerService(
                    GraniteFactory.class.getName(),
                    new GraniteFactoryWrapper(service, destination + KerneosConstants.FACTORY_SUFFIX),
                    null);
            Configuration factoryConfiguration;
            {
                Dictionary properties = new Hashtable();
                properties.put("id", destination + KerneosConstants.FACTORY_SUFFIX);
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
                properties.put("id", destination);
                properties.put("service", KerneosConstants.GRANITE_SERVICE);
                properties.put("factory", destination + KerneosConstants.FACTORY_SUFFIX);
                properties.put("scope", scope);
                destinationConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
                destinationConfiguration.update(properties);
            }

            synchronized (servicesMap) {
                servicesMap.put(ks.id(), new ServiceInstance(instance, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e, "Can't register a Factory Service");
        }
    }

    /**
     * Called when a Kerneos Factory Service isn't registered anymore.
     *
     * @param service the instance of the service
     */
    @Unbind
    private void unbindFactory(final KerneosFactoryService service, final ServiceReference serviceRef) throws IOException {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            if (ks == null) {
                logger.error("Invalid Kerneos Factory Service: " + service);
                return;
            }

            logger.debug("Remove Kerneos Factory Service: " + ks.id());

            String destination = getDestination(serviceRef, ks.id());
            ServiceInstance kfi = null;
            synchronized (servicesMap) {
                kfi = servicesMap.get(ks.id());
            }
            if (kfi == null) {
                logger.warn("Try to remove an invalid Kerneos Factory Service: " + ks.id());
                return;
            }

            unregisterClasses(destination);

            kfi.dispose();
        } catch (Exception e) {
            logger.error(e, "Can't unregister a Factory Service");
        }
    }


    /**
     * Called when a Kerneos Asynchronous Service is registered.
     *
     * @param service the instance of the service
     */
    @Bind(aggregate = true, optional = true)
    private void bindAsynchronous(final KerneosAsynchronousService service, final ServiceReference serviceRef) {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            KerneosAsynchronous ka = service.getClass().getAnnotation(KerneosAsynchronous.class);
            if (ks == null || ka == null) {
                logger.error("Invalid Kerneos Asynchronous Service: " + service);
                return;
            }

            logger.debug("New Kerneos Asynchronous Service: " + ks.id());

            String destination = getDestination(serviceRef, ks.id());
            registerClasses(destination, service);

            Configuration factoryConfiguration = null;
            {
                Dictionary properties = new Hashtable();
                for (KerneosAsynchronous.Property property : ka.properties()) {
                    properties.put(property.name(), property.value());
                }

                switch (ka.type()) {
                    case JMS:
                        properties.put("destination", destination);
                        factoryConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.gravity.osgi.adapters.jms.configuration", null);
                        factoryConfiguration.update(properties);
                        break;
                    case EVENTADMIN:
                        properties.put("destination", destination);
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
                properties.put("id", destination);
                properties.put("service", KerneosConstants.GRAVITY_SERVICE);
                properties.put("adapter", adapter);
                destinationConfiguration = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
                destinationConfiguration.update(properties);
            }

            synchronized (servicesMap) {
                servicesMap.put(ks.id(), new ServiceInstance(null, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e, "Can't register a Asynchronous Service");
        }
    }

    /**
     * Called when a Kerneos Asynchronous Service isn't registered anymore.
     *
     * @param service the instance of the service
     */
    @Unbind
    private void unbindAsynchronous(final KerneosAsynchronousService service, final ServiceReference serviceRef) throws IOException {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            if (ks == null) {
                logger.warn("Invalid Kerneos Asynchronous Service: " + service);
            }

            logger.debug("Remove Kerneos Asynchronous Service: " + ks.id());

            String destination = getDestination(serviceRef, ks.id());
            ServiceInstance ksi = null;
            synchronized (servicesMap) {
                ksi = servicesMap.get(ks.id());
            }
            if (ksi == null) {
                logger.warn("Try to remove an invalid Kerneos Asynchronous Service: " + ks.id());
                return;
            }

            unregisterClasses(destination);

            ksi.dispose();
        } catch (Exception e) {
            logger.error(e, "Can't unregister a Asynchronous Service");
        }
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
