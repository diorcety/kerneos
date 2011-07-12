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

import org.ow2.kerneos.core.KerneosConstants;
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
import java.util.LinkedList;
import java.util.List;
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
        private String destination;

        /**
         * Constructor
         *
         * @param instance is an OSGi service instance.
         * @param conf1    is an iPojo component instance.
         * @param conf2    is an iPojo component instance.
         */
        public ServiceInstance(final String destination,
                               final ServiceRegistration instance,
                               final Configuration conf1,
                               final Configuration conf2) {
            this.destination = destination;
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

        public String getDestination() {
            return destination;
        }
    }

    private Map<String, ServiceInstance> instancesMap = new Hashtable<String, ServiceInstance>();
    private List<Object> services = new LinkedList<Object>();

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private GraniteClassRegistry gcr;

    @Requires
    private IKerneosCore kerneosCore;

    private BundleContext bundleContext;

    private boolean started = false;


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
    private synchronized void start() {
        logger.debug("Start KerneosServiceFactory");
        started = true;

        // Add already bound services
        for (Object obj : services) {
            if (obj instanceof KerneosSimpleService) {
                addSimple((KerneosSimpleService) obj);
            } else if (obj instanceof KerneosFactoryService) {
                addFactory((KerneosFactoryService) obj);
            } else if (obj instanceof KerneosAsynchronousService) {
                addAsynchronous((KerneosAsynchronousService) obj);
            }
        }
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private synchronized void stop() {
        logger.debug("Stop KerneosServiceFactory");
        started = false;

        // Remove all the services
        for (Object obj : services) {
            if (obj instanceof KerneosSimpleService) {
                removeSimple((KerneosSimpleService) obj);
            } else if (obj instanceof KerneosFactoryService) {
                removeFactory((KerneosFactoryService) obj);
            } else if (obj instanceof KerneosAsynchronousService) {
                removeAsynchronous((KerneosAsynchronousService) obj);
            }
        }
    }

    /**
     * Called when a new Kerneos Simple Service is registered.
     *
     * @param service the instance of the service
     */
    @Bind(aggregate = true, optional = true)
    private synchronized void bindSimple(final KerneosSimpleService service) {
        services.add(service);
        if (started) {
            addSimple(service);
        }
    }

    private synchronized void addSimple(final KerneosSimpleService service) {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            if (ks == null) {
                logger.error("Invalid Kerneos Simple Service: " + service);
                return;
            }

            logger.debug("New Kerneos Simple Service: " + ks.id());

            String destination = kerneosCore.getService(ks.id()).getDestination();
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

            instancesMap.put(ks.id(), new ServiceInstance(destination, instance, destinationConfiguration, factoryConfiguration));

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
    private synchronized void unbindSimple(final KerneosSimpleService service) {
        services.remove(service);
        if (started) {
            removeSimple(service);
        }
    }

    private synchronized void removeSimple(final KerneosSimpleService service) {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            if (ks == null) {
                logger.error("Invalid Kerneos Simple Service: " + service);
                return;
            }


            logger.debug("Remove Kerneos Simple Service: " + ks.id());

            ServiceInstance ksi = instancesMap.get(ks.id());
            if (ksi == null) {
                logger.warn("Try to remove an invalid Kerneos Simple Service: " + ks.id());
                return;
            }

            unregisterClasses(ksi.getDestination());

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
    private synchronized void bindFactory(final KerneosFactoryService service) {
        services.add(service);
        if (started) {
            addFactory(service);
        }
    }

    private synchronized void addFactory(final KerneosFactoryService service) {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            if (ks == null) {
                logger.error("Invalid Kerneos Factory Service: " + service);
                return;
            }

            logger.debug("New Kerneos Factory Service: " + ks.id());

            String destination = kerneosCore.getService(ks.id()).getDestination();
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

            instancesMap.put(ks.id(), new ServiceInstance(destination, instance, destinationConfiguration, factoryConfiguration));

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
    private synchronized void unbindFactory(final KerneosFactoryService service) {
        services.remove(service);
        if (started) {
            removeFactory(service);
        }
    }

    private synchronized void removeFactory(final KerneosFactoryService service) {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            if (ks == null) {
                logger.error("Invalid Kerneos Factory Service: " + service);
                return;
            }

            logger.debug("Remove Kerneos Factory Service: " + ks.id());

            ServiceInstance kfi = instancesMap.get(ks.id());
            if (kfi == null) {
                logger.warn("Try to remove an invalid Kerneos Factory Service: " + ks.id());
                return;
            }

            unregisterClasses(kfi.getDestination());

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
    private synchronized void bindAsynchronous(final KerneosAsynchronousService service) {
        services.add(service);
        if (started) {
            addAsynchronous(service);
        }
    }

    private synchronized void addAsynchronous(final KerneosAsynchronousService service) {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            KerneosAsynchronous ka = service.getClass().getAnnotation(KerneosAsynchronous.class);
            if (ks == null || ka == null) {
                logger.error("Invalid Kerneos Asynchronous Service: " + service);
                return;
            }

            logger.debug("New Kerneos Asynchronous Service: " + ks.id());

            String destination = kerneosCore.getService(ks.id()).getDestination();
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

            instancesMap.put(ks.id(), new ServiceInstance(destination, null, destinationConfiguration, factoryConfiguration));
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
    private synchronized void unbindAsynchronous(final KerneosAsynchronousService service) {
        services.remove(service);
        if (started) {
            removeAsynchronous(service);
        }
    }

    private synchronized void removeAsynchronous(final KerneosAsynchronousService service) {
        try {
            KerneosService ks = service.getClass().getAnnotation(KerneosService.class);
            if (ks == null) {
                logger.warn("Invalid Kerneos Asynchronous Service: " + service);
            }

            logger.debug("Remove Kerneos Asynchronous Service: " + ks.id());

            ServiceInstance ksi = instancesMap.get(ks.id());
            if (ksi == null) {
                logger.warn("Try to remove an invalid Kerneos Asynchronous Service: " + ks.id());
                return;
            }

            unregisterClasses(ksi.getDestination());

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
