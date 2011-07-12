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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.handler.extender.BundleTracker;

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.service.GraniteDestination;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import org.ow2.kerneos.core.ApplicationBundle;
import org.ow2.kerneos.core.ModuleBundle;
import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.ModuleEvent;
import org.ow2.kerneos.core.config.generated.Application;
import org.ow2.kerneos.core.config.generated.Folder;
import org.ow2.kerneos.core.config.generated.Module;
import org.ow2.kerneos.core.config.generated.ObjectFactory;
import org.ow2.kerneos.core.config.generated.Service;
import org.ow2.kerneos.core.config.generated.SwfModule;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * This is the component which handles the arrival/departure of the Kerneos' modules.
 */
@Component
@Instantiate
@Provides
public final class KerneosConfigurationService implements GraniteDestination {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosConfigurationService.class);

    /**
     * The JAXB context for rules packages serialization/deserialization. Must
     * be declared with all the potentially involved classes.
     */
    private JAXBContext jaxbContext;

    @Requires
    private EventAdmin eventAdmin;

    @Requires
    private GraniteClassRegistry gcr;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private IKerneosCore kerneosCore;

    private Configuration eaConfig, graniteDestination, gravityDestination;

    private BundleTracker bundleTracker;
    private BundleContext bundleContext;

    /**
     * Class used for tracking Kerneos' modules.
     */
    private class KerneosBundleTracker extends BundleTracker {

        /**
         * Constructor of the Kerneos bundle tracker.
         *
         * @param bundleContext the current bundle context.
         */
        KerneosBundleTracker(final BundleContext bundleContext) {
            super(bundleContext);
        }

        /**
         * Called by the OSGi Framework when a new bundle arrived.
         *
         * @param bundle is the new bundle.
         */
        @Override
        protected void addedBundle(final Bundle bundle) {
            String moduleHeader = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_MODULE_MANIFEST);
            String applicationHeader = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_APPLICATION_MANIFEST);

            if (moduleHeader != null) {
                try {
                    onModuleArrival(bundle, moduleHeader);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
            if (applicationHeader != null) {
                try {
                    onApplicationArrival(bundle, applicationHeader);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }

        /**
         * Called by the OSGi Framework on a bundle departure.
         *
         * @param bundle is the bundle which is gone.
         */
        @Override
        protected void removedBundle(final Bundle bundle) {
            String moduleHeader = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_MODULE_MANIFEST);
            String applicationHeader = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_APPLICATION_MANIFEST);

            if (moduleHeader != null) {
                try {
                    onModuleDeparture(bundle, moduleHeader);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
            if (applicationHeader != null) {
                try {
                    onApplicationDeparture(bundle, applicationHeader);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
    }

    /**
     * Constructor used by iPojo.
     *
     * @param bundleContext is the current bundle context.
     * @throws Exception can't load the JAXBContext.
     */
    private KerneosConfigurationService(final BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
        bundleTracker = new KerneosBundleTracker(bundleContext);
        jaxbContext = JAXBContext.newInstance(
                ObjectFactory.class.getPackage().getName(),
                ObjectFactory.class.getClassLoader());
    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        logger.debug("Start KerneosConfigurationService");

        // Register the classes used with "kerneosConfig" service
        gcr.registerClasses(KerneosConstants.KERNEOS_SERVICE_CONFIGURATION, ConfigObjects.list());

        // Register the classes used with event admin
        gcr.registerClasses(KerneosConstants.KERNEOS_SERVICE_ASYNC_CONFIGURATION, ConfigObjects.list());

        // Register the asynchronous service used with KerneosConfigurationService
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.KERNEOS_SERVICE_ASYNC_CONFIGURATION);
            properties.put("service", KerneosConstants.GRAVITY_SERVICE);

            gravityDestination = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
            gravityDestination.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("destination", KerneosConstants.KERNEOS_SERVICE_ASYNC_CONFIGURATION);

            eaConfig = configurationAdmin.createFactoryConfiguration("org.granite.gravity.osgi.adapters.ea.configuration", null);
            eaConfig.update(properties);
        }

        // Register the synchronous service used with KerneosConfigurationService
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.KERNEOS_SERVICE_CONFIGURATION);
            properties.put("service", KerneosConstants.GRANITE_SERVICE);

            graniteDestination = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
            graniteDestination.update(properties);
        }

        // Start to track bundles
        bundleTracker.open();
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        logger.debug("Stop KerneosConfigurationService");

        // Stop the tracking of bundles
        bundleTracker.close();

        // Remove the configurations
        eaConfig.delete();
        gravityDestination.delete();
        graniteDestination.delete();

        // Unregister the different used classes
        gcr.unregisterClasses(KerneosConstants.KERNEOS_SERVICE_ASYNC_CONFIGURATION);
        gcr.unregisterClasses(KerneosConstants.KERNEOS_SERVICE_CONFIGURATION);
    }

    /**
     * Call when a new Kerneos Module is arrived.
     *
     * @param bundle The Bundle corresponding to the module.
     * @param name   The name of the module.
     */
    private void onApplicationArrival(final Bundle bundle, final String name) {
        logger.debug("Add Kerneos Application: " + name);

        try {
            // Get the url used for resources of the bundle
            Application application = loadKerneosApplicationConfig(bundle);
            ApplicationBundle applicationBundle = new ApplicationBundle(name, application, bundle);

            kerneosCore.addApplicationBundle(applicationBundle);
        } catch (Exception e) {
            logger.error("Can't create the application \"" + name + "\": " + e);
            return;
        }
    }

    /**
     * Call when a new Kerneos Module is gone.
     *
     * @param bundle The Bundle corresponding to the module.
     * @param name   The name of the module.
     */
    private void onApplicationDeparture(final Bundle bundle, final String name) {
        logger.debug("Remove Application Module: " + name);
        try {
            ApplicationBundle applicationBundle = kerneosCore.removeApplicationBundle(name);
        } catch (Exception e) {
            logger.error("Can't remove the application \"" + name + "\": " + e);
        }

    }

    private void transformModule(Module module, String name) {
        // Fix Paths with module name
        module.setBundle(name);
        module.setBigIcon(KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + name + "/" + module.getBigIcon());
        module.setSmallIcon(KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + name + "/" + module.getSmallIcon());
        if (module instanceof SwfModule) {
            SwfModule swfModule = (SwfModule) module;
            swfModule.setFile(KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + name + "/" + swfModule.getFile());

            // Completing the destination of the module's services
            for (Service service : swfModule.getServices()) {
                if (service.getDestination() == null) {
                    service.setDestination(name + "~" + service.getId());
                }
            }
        } else if (module instanceof Folder) {
            for (Module subModule : ((Folder) module).getModules()) {
                transformModule(subModule, name);
            }
        }
    }


    /**
     * Call when a new Kerneos Module is arrived.
     *
     * @param bundle The Bundle corresponding to the module.
     * @param name   The name of the module.
     */
    private void onModuleArrival(final Bundle bundle, final String name) {
        logger.debug("Add Kerneos Module: " + name);

        try {
            // Get the url used for resources of the bundle
            Module module = loadKerneosModuleConfig(bundle);

            transformModule(module, name);

            ModuleBundle moduleBundle = new ModuleBundle(name, module, bundle);

            kerneosCore.addModuleBundle(moduleBundle);

            // Post an event
            ModuleEvent me = new ModuleEvent(moduleBundle.getModule(), ModuleEvent.LOAD);
            Dictionary<String, Object> properties = new Hashtable<String, Object>();
            properties.put(EAConstants.DATA, me);
            Event event = new Event(KerneosConstants.KERNEOS_CONFIG_TOPIC, properties);
            eventAdmin.sendEvent(event);
        } catch (Exception e) {
            logger.error("Can't create the module \"" + name + "\": " + e);
            return;
        }
    }

    /**
     * Call when a new Kerneos Module is gone.
     *
     * @param bundle The Bundle corresponding to the module.
     * @param name   The name of the module.
     */
    private void onModuleDeparture(final Bundle bundle, final String name) {
        logger.debug("Remove Kerneos Module: " + name);
        try {
            ModuleBundle moduleBundle = kerneosCore.removeModuleBundle(name);

            if (moduleBundle != null) {
                // Post an event
                ModuleEvent me = new ModuleEvent(moduleBundle.getModule(), ModuleEvent.UNLOAD);
                Dictionary<String, Object> properties = new Hashtable<String, Object>();
                properties.put(EAConstants.DATA, me);
                Event event = new Event(KerneosConstants.KERNEOS_CONFIG_TOPIC, properties);
                eventAdmin.sendEvent(event);
            }
        } catch (Exception e) {
            logger.error("Can't remove the module \"" + name + "\": " + e);
        }
    }


    /**
     * Load the module information.
     *
     * @param bundle the bundle corresponding to the module.
     * @return the information corresponding to the module.
     * @throws Exception the kerneos module application file is not found are is invalid.
     */
    private Module loadKerneosModuleConfig(final Bundle bundle) throws Exception {

        // Retrieve the Kerneos module file
        URL url = bundle.getResource(KerneosConstants.KERNEOS_MODULE_FILE);
        if (url != null) {
            InputStream resource = url.openStream();
            // Unmarshall it
            try {
                // Create an unmarshaller
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                // Deserialize the configuration file
                JAXBElement element = (JAXBElement) unmarshaller.unmarshal(resource);
                return (Module) element.getValue();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                resource.close();
            }
        } else {
            throw new Exception("No application file available at " + KerneosConstants.KERNEOS_MODULE_FILE);
        }
    }

    /**
     * Load the Kerneos config file and build the application object.
     *
     * @param bundle the bundle corresponding to the module.
     * @throws Exception the Kerneos application application file is not found are is invalid.
     */
    private Application loadKerneosApplicationConfig(final Bundle bundle) throws Exception {

        // Retrieve the Kerneos application file
        URL url = bundle.getResource(KerneosConstants.KERNEOS_APPLICATION_FILE);

        if (url != null) {

            // Load the file
            logger.info("Loading file : {0}", url.getFile());
            InputStream resource = url.openStream();

            // Unmarshall it
            try {
                // Create an unmarshaller
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                // Deserialize the application file
                return (Application) unmarshaller.unmarshal(resource);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                resource.close();
            }
        } else {
            throw new Exception("No application file available at " + KerneosConstants.KERNEOS_APPLICATION_FILE);
        }
    }

    /**
     * Get the an application.
     *
     * @return the Kerneos application.
     */
    public Application getApplication(String application) {
        return kerneosCore.getApplicationBundle(application).getApplication();
    }

    /**
     * Get the module list.
     *
     * @return the module list.
     */
    public Collection<Module> getModules() {
        List<Module> modules = new LinkedList<Module>();
        for (ModuleBundle moduleBundle : kerneosCore.getModuleBundles().values()) {
            modules.add(moduleBundle.getModule());
        }
        return modules;
    }

    /**
     * Get the service id.
     *
     * @return the service id.
     */
    public String getId() {
        return KerneosConstants.KERNEOS_SERVICE_CONFIGURATION;
    }
}
