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

package org.ow2.jasmine.kerneos.service.impl;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;

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

import org.granite.osgi.service.GraniteDestination;
import org.granite.osgi.service.GraniteFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.ow2.jasmine.kerneos.service.KerneosAsynchronous;
import org.ow2.jasmine.kerneos.service.KerneosAsynchronousService;
import org.ow2.jasmine.kerneos.service.KerneosFactory;
import org.ow2.jasmine.kerneos.service.KerneosFactoryService;
import org.ow2.jasmine.kerneos.service.KerneosService;
import org.ow2.jasmine.kerneos.service.KerneosSimpleService;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

@Component
@Instantiate
public final class KerneosServiceFactory {

    private static Log logger = LogFactory.getLog(KerneosServiceFactory.class);

    private class KerneosInstance {
        private ComponentInstance conf1;
        private ComponentInstance conf2;
        private ServiceRegistration instance;

        /**
         * @param instance
         * @param conf1
         * @param conf2
         */
        public KerneosInstance(final ServiceRegistration instance,
                               final ComponentInstance conf1,
                               final ComponentInstance conf2) {
            this.conf1 = conf1;
            this.conf2 = conf2;
            this.instance = instance;
        }

        /**
         *
         */
        public void dispose() {
            if (conf1 != null)
                conf1.dispose();
            if (conf2 != null)
                conf2.dispose();
            if (instance != null)
                instance.unregister();
        }
    }

    private Map<String, KerneosInstance> servicesMap = new Hashtable<String, KerneosInstance>();

    @Requires(from = "org.granite.config.flex.Destination")
    private Factory destinationService;

    @Requires(from = "org.granite.config.flex.Factory")
    private Factory factoryService;

    @Requires(from = "org.granite.gravity.osgi.adapters.jms.configuration")
    private Factory jmsFactory;

    @Requires(from = "org.granite.gravity.osgi.adapters.ea.configuration")
    private Factory eaFactory;

    @Requires
    private GraniteClassRegistry gcr;

    private BundleContext bundleContext;


    /**
     * @param bundleContext
     */
    private KerneosServiceFactory(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     *
     */
    @Validate
    private void start() {
        logger.debug("Start KerneosServiceFactory");
    }

    /**
     *
     */
    @Invalidate
    private void stop() {
        logger.debug("Stop KerneosServiceFactory");
    }

    /**
     * @param service
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
                    new GraniteKerneosSimple(service, serviceId + KerneosConstants.FACTORY_SUFFIX),
                    null);

            ComponentInstance factoryConfiguration = null;
            {
                Dictionary properties = new Hashtable();
                properties.put("ID", serviceId + KerneosConstants.FACTORY_SUFFIX);
                factoryConfiguration = factoryService.createComponentInstance(properties);
            }

            ComponentInstance destinationConfiguration = null;
            {
                Dictionary properties = new Hashtable();
                properties.put("ID", serviceId);
                properties.put("SERVICE", KerneosConstants.GRANITE_SERVICE);
                properties.put("CHANNELS", new String[]{KerneosConstants.GRANITE_CHANNEL});
                properties.put("FACTORY", serviceId + KerneosConstants.FACTORY_SUFFIX);
                properties.put("SCOPE", GraniteDestination.SCOPE.APPLICATION);
                destinationConfiguration = destinationService.createComponentInstance(properties);
            }

            synchronized (factoryService) {
                servicesMap.put(serviceId,
                                new KerneosInstance(instance, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * @param service
     */
    @Unbind
    private void unbindSimple(final KerneosSimpleService service) {

        KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
        if (ks == null) {
            logger.error("Invalid Kerneos Simple Service: " + service);
            return;
        }

        String serviceId = ks.destination();

        logger.debug("Remove Kerneos Simple Service: " + serviceId);

        KerneosInstance ksi = null;
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
     * @param service
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
                    new GraniteKerneosFactory(service, serviceId + KerneosConstants.FACTORY_SUFFIX),
                    null);

            ComponentInstance factoryConfiguration = null;
            {
                Dictionary properties = new Hashtable();
                properties.put("ID", serviceId + KerneosConstants.FACTORY_SUFFIX);
                factoryConfiguration = factoryService.createComponentInstance(properties);
            }

            ComponentInstance destinationConfiguration = null;
            {
                Dictionary properties = new Hashtable();
                properties.put("ID", serviceId);
                properties.put("SERVICE", KerneosConstants.GRANITE_SERVICE);
                properties.put("CHANNELS", new String[]{KerneosConstants.GRANITE_CHANNEL});
                properties.put("FACTORY", serviceId + KerneosConstants.FACTORY_SUFFIX);

                // Get Properties
                KerneosFactory annoProps = service.getClass().getAnnotation(KerneosFactory.class);
                if (annoProps != null) {
                    switch (annoProps.scope()) {
                        case APPLICATION:
                            properties.put("SCOPE", GraniteDestination.SCOPE.APPLICATION);
                            break;
                        case SESSION:
                            properties.put("SCOPE", GraniteDestination.SCOPE.SESSION);
                            break;
                        default:
                            properties.put("SCOPE", GraniteDestination.SCOPE.REQUEST);

                    }
                } else {
                    properties.put("SCOPE", GraniteDestination.SCOPE.REQUEST);
                }

                destinationConfiguration = destinationService.createComponentInstance(properties);
            }

            synchronized (factoryService) {
                servicesMap.put(serviceId,
                                new KerneosInstance(instance, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * @param service
     */
    @Unbind
    private void unbindFactory(final KerneosFactoryService service) {
        KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
        if (ks == null) {
            logger.error("Invalid Kerneos Factory Service: " + service);
            return;
        }

        String serviceId = ks.destination();

        logger.debug("Remove Kerneos Factory Service: " + serviceId);

        KerneosInstance kfi = null;
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
     * @param service
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

            ComponentInstance factoryConfiguration = null;
            {
                Dictionary properties = new Hashtable();
                for (KerneosAsynchronous.Property property : ka.properties()) {
                    properties.put(property.name(), property.value());
                }
                switch (ka.type()) {
                    case JMS:
                        properties.put("destination", serviceId);
                        factoryConfiguration = jmsFactory.createComponentInstance(properties);
                        break;
                    case EVENTADMIN:
                        properties.put("destination", serviceId);
                        factoryConfiguration = eaFactory.createComponentInstance(properties);
                        break;
                }
            }

            ComponentInstance destinationConfiguration = null;
            {
                Dictionary properties = new Hashtable();
                properties.put("ID", serviceId);
                properties.put("SERVICE", KerneosConstants.GRAVITY_SERVICE);
                properties.put("CHANNELS", new String[]{KerneosConstants.GRAVITY_CHANNEL});
                switch (ka.type()) {
                    case JMS:
                        properties.put("ADAPTER", JMSConstants.ADAPTER_ID);
                        break;
                    case EVENTADMIN:
                        properties.put("ADAPTER", EAConstants.ADAPTER_ID);
                        break;
                }
                destinationConfiguration = destinationService.createComponentInstance(properties);
            }

            synchronized (factoryService) {
                servicesMap.put(serviceId, new KerneosInstance(null, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * @param service
     */
    @Unbind
    private void unbindAsynchronous(final KerneosAsynchronousService service) {
        KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
        if (ks == null) {
            logger.warn("Invalid Kerneos Asynchronous Service: " + service);
        }

        String serviceId = ks.destination();

        logger.debug("Remove Kerneos Asynchronous Service: " + serviceId);

        KerneosInstance ksi = null;
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
     * @param serviceId
     * @param service
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
     * @param serviceId
     */
    private void unregisterClasses(final String serviceId) {
        gcr.unregisterClasses(serviceId);
    }
}