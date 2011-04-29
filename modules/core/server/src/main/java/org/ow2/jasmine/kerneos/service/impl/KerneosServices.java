/**
 * JASMINe
 * Copyright (C) 2009 Bull S.A.S.
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

import org.apache.felix.ipojo.api.Dependency;
import org.apache.felix.ipojo.api.PrimitiveComponentType;
import org.apache.felix.ipojo.api.SingletonComponentType;
import org.granite.osgi.GraniteClassRegistry;

import org.granite.osgi.service.GraniteDestination;
import org.granite.osgi.service.GraniteFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.ow2.jasmine.kerneos.service.KerneosFactory;
import org.ow2.jasmine.kerneos.service.KerneosFactoryProperties;
import org.ow2.jasmine.kerneos.service.KerneosService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

@Component
@Instantiate
public class KerneosServices {

    private static Log logger = LogFactory.getLog(KerneosServices.class);

    private class KerneosInstance {
        private ComponentInstance destinationConfiguration;
        private ComponentInstance factoryConfiguration;
        private ServiceRegistration instance;

        public KerneosInstance(ServiceRegistration instance,
                               ComponentInstance destinationConfiguration,
                               ComponentInstance factoryConfiguration) {
            this.destinationConfiguration = destinationConfiguration;
            this.factoryConfiguration = factoryConfiguration;
            this.instance = instance;
        }

        public void dispose() {
            destinationConfiguration.dispose();
            factoryConfiguration.dispose();
            instance.unregister();
        }
    }

    Map<String, KerneosInstance> serviceMap = new Hashtable<String, KerneosInstance>();
    Map<String, KerneosInstance> factoryMap = new Hashtable<String, KerneosInstance>();

    @Requires(from = "org.granite.config.flex.Destination")
    Factory destinationService;

    @Requires(from = "org.granite.config.flex.Factory")
    Factory factoryService;


    @Requires
    GraniteClassRegistry gcr;

    BundleContext bundleContext;

    private KerneosServices(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Validate
    private void start() {
        logger.info("Start KereosServices");
    }

    @Invalidate
    private void stop() {
        logger.info("Stop KereosServices");
    }

    @Bind(aggregate = true, optional = true)
    private final void bindService(final KerneosService service) {
        addService(service);
    }

    @Unbind
    private final void unbindService(final KerneosService service) {
        removeService(service.getId());
    }

    @Bind(aggregate = true, optional = true)
    private final void bindFactory(final KerneosFactory factory) {
     //   gcr.registerClass(factory.getId(), factory.getClass(), true);
        addFactory(factory);
    }

    @Unbind
    private final void unbindFactory(final KerneosFactory factory) {
        removeFactory(factory.getId());
    //    gcr.unregisterClass(factory.getId(), factory.getClass(), true);
    }

    private final void addService(KerneosService service) {
        String serviceId = service.getId();
        logger.info("New Service: " + serviceId);
        try {
            ServiceRegistration instance = null;
            {
                instance = bundleContext.registerService(GraniteFactory.class.getName(), new GraniteKerneosService(service), null);

            }

            ComponentInstance factoryConfiguration = null;
            {
                Dictionary properties = new Hashtable();
                properties.put("ID", serviceId + KerneosConstants.FACTORY_SUFFIX);
                factoryConfiguration = factoryService.createComponentInstance(properties);
            }

            ComponentInstance destinationConfiguration = null;
            {
                Collection<String> channels = new LinkedList<String>();
                channels.add(KerneosConstants.GRANITE_CHANNEL);
                Dictionary properties = new Hashtable();
                properties.put("ID", serviceId);
                properties.put("SERVICE", KerneosConstants.GRANITE_SERVICE);
                properties.put("CHANNELS", channels);
                properties.put("FACTORY", serviceId + KerneosConstants.FACTORY_SUFFIX);
                properties.put("SCOPE", GraniteDestination.SCOPE.APPLICATION);
                destinationConfiguration = destinationService.createComponentInstance(properties);
            }

            synchronized (factoryService) {
                factoryMap.put(serviceId, new KerneosInstance(instance, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private final void removeService(String serviceId) {
        logger.info("Remove service: " + serviceId);

        KerneosInstance ksi = null;
        synchronized (serviceMap) {
            ksi = serviceMap.get(serviceId);
        }
        if (ksi == null) {
            logger.warn("Try to remove an invalid KerneosService: " + serviceId);
            return;
        }

        ksi.dispose();
    }

    private final void addFactory(KerneosFactory factory) {
        String serviceId = factory.getId();
        logger.info("New factory: " + serviceId);
        try {
            ServiceRegistration instance = null;
            {
                instance = bundleContext.registerService(GraniteFactory.class.getName(), new GraniteKerneosFactory(factory), null);
            }

            ComponentInstance factoryConfiguration = null;
            {
                Dictionary properties = new Hashtable();
                properties.put("ID", serviceId + KerneosConstants.FACTORY_SUFFIX);
                factoryConfiguration = factoryService.createComponentInstance(properties);
            }

            ComponentInstance destinationConfiguration = null;
            {
                Collection<String> channels = new LinkedList<String>();
                channels.add(KerneosConstants.GRANITE_CHANNEL);
                Dictionary properties = new Hashtable();
                properties.put("ID", serviceId);
                properties.put("SERVICE", KerneosConstants.GRANITE_SERVICE);
                properties.put("CHANNELS", channels);
                properties.put("FACTORY", serviceId + KerneosConstants.FACTORY_SUFFIX);

                // Get Properties
                KerneosFactoryProperties anno_props = factory.getClass().getAnnotation(KerneosFactoryProperties.class);
                if (anno_props != null) {
                    switch (anno_props.scope()) {
                        case APPLICATION:
                            properties.put("SCOPE", GraniteDestination.SCOPE.APPLICATION);
                            break;
                        case SESSION:
                            properties.put("SCOPE", GraniteDestination.SCOPE.SESSION);
                        default:
                            properties.put("SCOPE", GraniteDestination.SCOPE.REQUEST);

                    }
                } else {
                    properties.put("SCOPE", GraniteDestination.SCOPE.REQUEST);
                }

                destinationConfiguration = destinationService.createComponentInstance(properties);
            }

            synchronized (factoryService) {
                factoryMap.put(serviceId, new KerneosInstance(instance, destinationConfiguration, factoryConfiguration));
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private final void removeFactory(String factoryId) {
        logger.info("Remove factory: " + factoryId);

        KerneosInstance kfi = null;
        synchronized (factoryMap) {
            kfi = factoryMap.get(factoryId);
        }
        if (kfi == null) {
            logger.warn("Try to remove an invalid KerneosFactory: " + factoryId);
            return;
        }

        kfi.dispose();
    }
}
