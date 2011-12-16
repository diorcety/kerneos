package org.ow2.kerneos.flex.wrapper;

import org.apache.felix.ipojo.annotations.*;

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.granite.gravity.osgi.adapters.jms.JMSConstants;

import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.GraniteConstants;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.common.config.generated.Mapping;
import org.ow2.kerneos.common.config.generated.Service;
import org.ow2.kerneos.common.config.generated.SwfModule;
import org.ow2.kerneos.core.service.KerneosAsynchronousService;
import org.ow2.kerneos.flex.FlexConstants;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

@Component
public class AsynchronousServiceWrapper {
    public final static String SERVICE = "SERVICE";
    public final static String CONFIGURATION = "CONFIGURATION";

    private static Log LOGGER = LogFactory.getLog(AsynchronousServiceWrapper.class);

    @Property(name = CONFIGURATION)
    private Service serviceConfiguration;

    private KerneosAsynchronousService service;
    private ServiceReference serviceReference;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private GraniteClassRegistry gcr;

    private Configuration destinationConfiguration;
    private Configuration factoryConfiguration;

    @Bind(id = SERVICE)
    private void bindService(KerneosAsynchronousService service, ServiceReference serviceReference) {
        this.service = service;
        this.serviceReference = serviceReference;
    }

    @Unbind
    private void unbindService(KerneosAsynchronousService service, ServiceReference serviceReference) {
        this.service = null;
        this.serviceReference = null;
    }

    @Validate
    private void start() {
        try {
            LOGGER.debug("Start KerneosAsynchronousService Wrapper: " + serviceConfiguration.getId());

            String at = (String) serviceReference.getProperty(KerneosAsynchronousService.TYPE);

            if (at == null) {
                LOGGER.error("Invalid KerneosAsynchronousService: " + serviceConfiguration.getId());
                return;
            }

            String destination = serviceConfiguration.getDestination();

            registerClasses(serviceConfiguration, service);
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
            LOGGER.error("Can't start KerneosAsynchronousService Wrapper: " + e);
        }
    }

    @Invalidate
    private void stop() {
        try {
            LOGGER.debug("Stop KerneosAsynchronousService Wrapper: " + serviceConfiguration.getId());

            unregisterClasses(serviceConfiguration);

            destinationConfiguration.delete();
            factoryConfiguration.delete();

        } catch (Exception e) {
            LOGGER.error("Can't stop KerneosAsynchronousService: " + e);
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