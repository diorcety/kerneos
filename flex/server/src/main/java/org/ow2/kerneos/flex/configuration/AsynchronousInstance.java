package org.ow2.kerneos.flex.configuration;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.granite.gravity.osgi.adapters.jms.JMSConstants;

import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.GraniteConstants;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.common.config.generated.Mapping;
import org.ow2.kerneos.common.config.generated.Service;
import org.ow2.kerneos.common.config.generated.SwfModule;
import org.ow2.kerneos.common.service.KerneosModule;
import org.ow2.kerneos.core.service.KerneosAsynchronousService;
import org.ow2.kerneos.flex.FlexConstants;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

@Component
public class AsynchronousInstance {
    private static Log LOGGER = LogFactory.getLog(AsynchronousInstance.class);

    @Requires(id = "kerneosModule")
    private KerneosModule kerneosModule;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private GraniteClassRegistry gcr;

    @Property(name = "serviceReference")
    private ServiceReference serviceReference;

    private Service service;
    private Configuration destinationConfiguration;
    private Configuration factoryConfiguration;

    private BundleContext bundleContext;

    AsynchronousInstance(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private Service getService(String id) {
        if (kerneosModule.getConfiguration() instanceof SwfModule) {
            SwfModule configuration = (SwfModule) kerneosModule.getConfiguration();
            for (Service service : configuration.getServices()) {
                if (service.getId().equals(id)) {
                    return service;
                }
            }
        } else {
            LOGGER.error("This module is not a SwfModule");
        }

        return null;
    }

    @Validate
    private void start() {
        try {
            KerneosAsynchronousService kerneosService = (KerneosAsynchronousService) bundleContext.getService(serviceReference);
            String id = (String) serviceReference.getProperty(KerneosAsynchronousService.ID);
            String at = (String) serviceReference.getProperty(KerneosAsynchronousService.TYPE);

            if (id == null || at == null) {
                LOGGER.error("Invalid Kerneos Asynchronous Service: " + kerneosService);
                return;
            }

            LOGGER.debug("New Kerneos Asynchronous Service: " + id);

            service = getService(id);
            if (service == null)
                throw new Exception("Can't find the service \"" + id + "\"");
            String destination = service.getDestination();

            registerClasses(service, kerneosService);

            {
                Dictionary properties = new Hashtable();
                for (String key : serviceReference.getPropertyKeys()) {
                    properties.put(key, serviceReference.getProperty(key));
                }

                if (at.equals(KerneosAsynchronousService.Type.JMS)) {
                    properties.put("destination", destination);
                    factoryConfiguration = configurationAdmin.createFactoryConfiguration(JMSConstants.CONFIGURATION_ID, null);
                    factoryConfiguration.update(properties);
                } else if (at.equals(KerneosAsynchronousService.Type.EVENTADMIN)) {
                    properties.put("destination", destination);
                    factoryConfiguration = configurationAdmin.createFactoryConfiguration(EAConstants.CONFIGURATION_ID, null);
                    factoryConfiguration.update(properties);
                }
            }

            String adapter = null;
            if (at.equals(KerneosAsynchronousService.Type.JMS)) {
                adapter = JMSConstants.ADAPTER_ID;
            } else if (at.equals(KerneosAsynchronousService.Type.EVENTADMIN)) {
                adapter = EAConstants.ADAPTER_ID;
            }

            {
                Dictionary properties = new Hashtable();
                properties.put("id", destination);
                properties.put("service", FlexConstants.GRAVITY_SERVICE);
                properties.put("aobjectdapter", adapter);
                destinationConfiguration = configurationAdmin.createFactoryConfiguration(GraniteConstants.DESTINATION, null);
                destinationConfiguration.update(properties);
            }

        } catch (Exception e) {
            LOGGER.error("Can't register a Asynchronous Service: " + e);
        }
    }

    @Invalidate
    private void stop() {
        try {
            KerneosAsynchronousService kerneosService = (KerneosAsynchronousService) bundleContext.getService(serviceReference);
            String id = (String) serviceReference.getProperty(KerneosAsynchronousService.ID);
            if (id == null) {
                LOGGER.warn("Invalid Kerneos Asynchronous Service: " + kerneosService);
            }
            LOGGER.debug("Remove Kerneos Asynchronous Service: " + id);

            unregisterClasses(service);

            destinationConfiguration.delete();
            factoryConfiguration.delete();

        } catch (Exception e) {
            LOGGER.error("Can't unregister a Asynchronous Service: " + e);
        }
    }


    /**
     * Register the classes associated to a service
     *
     * @param service the service
     * @param service the instance of the service
     */
    private void registerClasses(final Service service, final Object object) throws ClassNotFoundException {
        if (service.getModule() instanceof SwfModule) {
            List<Mapping> mappings = ((SwfModule) service.getModule()).getMappings();
            Class[] classes = new Class[mappings.size()];
            int i = 0;
            for (Mapping mapping : mappings) {
                classes[i] = object.getClass().getClassLoader().loadClass(mapping.getJava());
                i++;
            }

            gcr.registerClasses(service.getDestination(), classes);
        }
    }

    /**
     * Unregister the classes associated to a service
     *
     * @param service the service
     */
    private void unregisterClasses(final Service service) {
        if (service.getModule() instanceof SwfModule) {
            gcr.unregisterClasses(service.getDestination());
        }
    }
}