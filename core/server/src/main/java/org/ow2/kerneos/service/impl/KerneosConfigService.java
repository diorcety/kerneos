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

package org.ow2.kerneos.service.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.handler.extender.BundleTracker;
import org.apache.felix.ipojo.handlers.event.publisher.Publisher;

import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.service.GraniteDestination;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.ow2.kerneos.config.generated.IframeModule;
import org.ow2.kerneos.config.generated.Application;
import org.ow2.kerneos.config.generated.Module;
import org.ow2.kerneos.config.generated.ObjectFactory;
import org.ow2.kerneos.config.generated.PromptBeforeClose;
import org.ow2.kerneos.config.generated.Service;
import org.ow2.kerneos.config.generated.SwfModule;
import org.ow2.kerneos.service.ApplicationInstance;
import org.ow2.kerneos.service.ModuleEvent;
import org.ow2.kerneos.service.ModuleInstance;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * This is the component which handles the arrival/departure of the Kerneos' modules.
 */
@Component
@Instantiate
@Provides
public final class KerneosConfigService implements GraniteDestination {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosConfigService.class);

    private static final String GRAVITY_DESTINATION = "kerneos-async-configuration";

    /**
     * The JAXB context for rules packages serialization/deserialization. Must
     * be declared with all the potentially involved classes.
     */
    private JAXBContext jaxbContext;

    /**
     * Async Event Publisher.
     */
    @org.apache.felix.ipojo.handlers.event.Publisher(
            name = "KerneosConfigService",
            topics = KerneosConstants.KERNEOS_CONFIG_TOPIC
    )

    private Publisher publisher;

    /**
     * Granite Class Registry.
     */
    @Requires
    private GraniteClassRegistry gcr;

    @Requires
    private IKerneosCore kerneosCore;

    @Requires(from = "org.granite.gravity.osgi.adapters.ea.configuration")
    private Factory eaFactory;

    @Requires(from = "org.granite.config.flex.Destination")
    private Factory destinationFactory;

    private ComponentInstance eaConfig, graniteDestination, gravityDestination;
    private BundleTracker bundleTracker;

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
    private KerneosConfigService(final BundleContext bundleContext) throws Exception {
        bundleTracker = new KerneosBundleTracker(bundleContext);
        jaxbContext = JAXBContext.newInstance(
                ObjectFactory.class.getPackage().getName(),
                ObjectFactory.class.getClassLoader());
    }

    /**
     * Called when all the component dependencies are met.
     *
     * @throws MissingHandlerException   issue during GraniteDS configuration.
     * @throws ConfigurationException    issue during GraniteDS configuration.
     * @throws UnacceptableConfiguration issue during GraniteDS configuration.
     */
    @Validate
    private void start() throws MissingHandlerException, ConfigurationException,
            UnacceptableConfiguration {
        logger.debug("Start KerneosConfigService");

        // Register the classes used with event admin
        gcr.registerClasses(GRAVITY_DESTINATION, new Class[]{
                ModuleEvent.class,
                Service.class,
                Application.class,
                ApplicationInstance.class,
                Module.class,
                ModuleInstance.class,
                IframeModule.class,
                SwfModule.class,
                PromptBeforeClose.class
        });

        // Register the classes used with "kerneosConfig" service
        gcr.registerClasses(getId(), new Class[]{
                Service.class,
                Application.class,
                ApplicationInstance.class,
                Module.class,
                ModuleInstance.class,
                IframeModule.class,
                SwfModule.class,
                PromptBeforeClose.class
        });

        // Register the few configurations used with KerneosConfigService
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", GRAVITY_DESTINATION);
            properties.put("SERVICE", KerneosConstants.GRAVITY_SERVICE);
            gravityDestination = destinationFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("destination", GRAVITY_DESTINATION);
            eaConfig = eaFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", getId());
            properties.put("SERVICE", KerneosConstants.GRANITE_SERVICE);
            graniteDestination = destinationFactory.createComponentInstance(properties);
        }

        // Start to track bundles
        bundleTracker.open();
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() {
        logger.debug("Stop KerneosConfigService");

        // Stop the tracking of bundles
        bundleTracker.close();

        // Remove the configurations
        eaConfig.dispose();
        gravityDestination.dispose();
        graniteDestination.dispose();

        // Unregister the different used classes
        gcr.unregisterClasses(getId());
        gcr.unregisterClasses(GRAVITY_DESTINATION);
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
            Application module = loadApplicationConfig(bundle);

            kerneosCore.registerApplication(name, module, bundle);

        } catch (Exception e) {
            logger.error(e, "Can't add the Kerneos Application: " + name);
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
            kerneosCore.unregisterApplication(name);

        } catch (Exception e) {
            logger.error(e, "Can't remove the Kerneos Application: " + name);
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
            Module module = loadModuleConfig(bundle);

            ModuleInstance moduleInstance = kerneosCore.registerModule(name, module, bundle);

            // Post an event
            ModuleEvent me = new ModuleEvent(new ModuleInstance(moduleInstance), ModuleEvent.LOAD);
            publisher.sendData(me);
        } catch (Exception e) {
            logger.error(e, "Can't add the Kerneos Module: " + name);
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
            ModuleInstance moduleInstance = kerneosCore.unregisterModule(name);

            // Post an event
            ModuleEvent me = new ModuleEvent(new ModuleInstance(moduleInstance), ModuleEvent.UNLOAD);
            publisher.sendData(me);
        } catch (Exception e) {
            logger.error(e, "Can't remove the Kerneos Module: " + name);
        }
    }


    /**
     * Load the module information.
     *
     * @param bundle the bundle corresponding to the module.
     * @return the information corresponding to the module.
     * @throws Exception the kerneos module configuration file is not found are is invalid.
     */
    private Module loadModuleConfig(final Bundle bundle) throws Exception {

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
            throw new Exception("No configuration file available at " + KerneosConstants.KERNEOS_MODULE_FILE);
        }
    }

    /**
     * Load the Kerneos config file and build the configuration object.
     *
     * @param bundle the bundle corresponding to the module.
     * @throws Exception the Kerneos application configuration file is not found are is invalid.
     */
    private Application loadApplicationConfig(final Bundle bundle) throws Exception {

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

                // Deserialize the configuration file
                //JAXBElement element = (JAXBElement) unmarshaller.unmarshal(resource);
                //return (Application) element.getValue();
                return (Application) unmarshaller.unmarshal(resource);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                resource.close();
            }
        } else {
            throw new Exception("No configuration file available at " + KerneosConstants.KERNEOS_APPLICATION_FILE);
        }
    }

    /**
     * Get the an application.
     *
     * @return the Kerneos configuration.
     */
    public ApplicationInstance getApplication(String application) {
        for (ApplicationInstance applicationInstance : kerneosCore.getApplications()) {
            if (applicationInstance.getId().equals(application))
                return new ApplicationInstance(applicationInstance);
        }
        return null;
    }

    /**
     * Get the module list.
     *
     * @return the module list.
     */
    public Collection<ModuleInstance> getModules() {
        List<ModuleInstance> moduleInstanceList = new LinkedList<ModuleInstance>();

        for (ModuleInstance moduleInstance : kerneosCore.getModules()) {
            moduleInstanceList.add(new ModuleInstance(moduleInstance));
        }

        return moduleInstanceList;
    }

    /**
     * Get the service id.
     *
     * @return the service id.
     */
    public String getId() {
        return "kerneos-configuration";
    }
}
