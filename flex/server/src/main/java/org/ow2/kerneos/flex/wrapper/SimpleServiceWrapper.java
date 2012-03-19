package org.ow2.kerneos.flex.wrapper;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.GraniteConstants;
import org.granite.osgi.service.GraniteFactory;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.common.config.generated.Mapping;
import org.ow2.kerneos.common.config.generated.Service;
import org.ow2.kerneos.common.config.generated.SwfModule;
import org.ow2.kerneos.core.service.KerneosSimpleService;
import org.ow2.kerneos.flex.FlexConstants;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

@Component
@Provides
public class SimpleServiceWrapper implements GraniteFactory {
    public static final String SERVICE = "SERVICE";
    public static final String CONFIGURATION = "CONFIGURATION";

    private static final Log LOGGER = LogFactory.getLog(SimpleServiceWrapper.class);

    @Property(name = CONFIGURATION)
    private Service serviceConfiguration;

    private KerneosSimpleService service;
    private ServiceReference serviceReference;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private GraniteClassRegistry gcr;

    private Configuration destinationConfiguration;
    private Configuration factoryConfiguration;

    private SimpleServiceWrapper() {

    }

    @Bind(id = SERVICE)
    private void bindService(KerneosSimpleService service, ServiceReference serviceReference) {
        this.service = service;
        this.serviceReference = serviceReference;
    }

    @Unbind
    private void unbindService(KerneosSimpleService service, ServiceReference serviceReference) {
        this.service = null;
        this.serviceReference = null;
    }

    @Validate
    private void start() {
        try {
            LOGGER.debug("Start KerneosSimpleService Wrapper: " + serviceConfiguration.getId());
            String destination = serviceConfiguration.getDestination();

            registerClasses(serviceConfiguration, service);
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
                destinationConfiguration = configurationAdmin.createFactoryConfiguration(GraniteConstants.DESTINATION,
                        null);
                destinationConfiguration.update(properties);
            }

        } catch (Exception e) {
            LOGGER.error("Can't start KerneosSimpleService Wrapper: " + e);
        }
    }

    @Invalidate
    private void stop() {
        try {
            LOGGER.debug("Stop KerneosSimpleService Wrapper: " + serviceConfiguration.getId());

            unregisterClasses(serviceConfiguration);

            destinationConfiguration.delete();
            factoryConfiguration.delete();
        } catch (Exception e) {
            LOGGER.error("Can't stop KerneosSimpleService: " + e);
        }
    }


    /**
     * Register the classes associated to a service
     *
     * @param service the service
     * @param object  the instance of the service
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


    /**
     * Get the GraniteFactory's id.
     *
     * @return the GraniteFactory's id.
     */
    public String getId() {
        return serviceConfiguration.getDestination() + FlexConstants.FACTORY_SUFFIX;
    }

    /**
     * Called when GraniteDS ask for a new instance of the service provided by this factory.
     *
     * @return the new instance of the service.
     */
    public Object newInstance() {
        return service;
    }
}
