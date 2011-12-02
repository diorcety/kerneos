package org.ow2.kerneos.flex.configuration;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

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
import org.ow2.kerneos.core.service.KerneosSimpleService;
import org.ow2.kerneos.flex.FlexConstants;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

@Component
public class SimpleInstance {
    private static Log LOGGER = LogFactory.getLog(SimpleInstance.class);

    @Requires(id = "kerneosModule")
    private KerneosModule kerneosModule;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires(filter = "(factory.name=org.ow2.kerneos.flex.granite.GraniteSimpleWrapper)")
    private Factory factory;

    @Requires
    private GraniteClassRegistry gcr;

    @Property(name = "serviceReference")
    private ServiceReference serviceReference;

    private Service service;
    private Configuration destinationConfiguration;
    private Configuration factoryConfiguration;
    private ComponentInstance instance;

    private BundleContext bundleContext;

    SimpleInstance(BundleContext bundleContext) {
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
            KerneosSimpleService kerneosService = (KerneosSimpleService) bundleContext.getService(serviceReference);
            String id = (String) serviceReference.getProperty(KerneosSimpleService.ID);
            if (id == null) {
                LOGGER.error("Invalid Kerneos Simple Service: " + service);
                return;
            }

            LOGGER.debug("New Kerneos Simple Service: " + id);

            service = getService(id);
            if (service == null)
                throw new Exception("Can't find the service \"" + id + "\"");
            String destination = service.getDestination();

            registerClasses(service, kerneosService);

            {
                Dictionary properties = new Hashtable();
                properties.put("id", destination + FlexConstants.FACTORY_SUFFIX);
                properties.put("service", kerneosService);
                instance = factory.createComponentInstance(properties);
                instance.start();
            }

            {
                Dictionary properties = new Hashtable();
                properties.put("id", destination + FlexConstants.FACTORY_SUFFIX);
                factoryConfiguration = configurationAdmin.createFactoryConfiguration(GraniteConstants.FACTORY, null);
                factoryConfiguration.update(properties);
            }

            {
                Dictionary properties = new Hashtable();
                properties.put("id", destination);
                properties.put("service", FlexConstants.GRANITE_SERVICE);
                properties.put("factory", destination + FlexConstants.FACTORY_SUFFIX);
                properties.put("scope", "application");
                destinationConfiguration = configurationAdmin.createFactoryConfiguration(GraniteConstants.DESTINATION, null);
                destinationConfiguration.update(properties);
            }

        } catch (Exception e) {
            LOGGER.error("Can't register a Simple Service: " + e);
        }
    }

    @Invalidate
    private void stop() {
        try {
            KerneosSimpleService kerneosService = (KerneosSimpleService) bundleContext.getService(serviceReference);
            String id = (String) serviceReference.getProperty(KerneosSimpleService.ID);
            if (id == null) {
                LOGGER.error("Invalid Kerneos Simple Service: " + service);
                return;
            }
            LOGGER.debug("Remove Kerneos Simple Service: " + id);

            unregisterClasses(service);

            destinationConfiguration.delete();
            factoryConfiguration.delete();
            instance.dispose();
        } catch (Exception e) {
            LOGGER.error("Can't unregister a Simple Service: " + e);
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
