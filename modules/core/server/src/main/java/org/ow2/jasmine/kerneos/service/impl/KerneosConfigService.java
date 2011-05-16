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

import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

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
import org.osgi.service.http.NamespaceException;

import org.ow2.jasmine.kerneos.config.generated.IframeModule;
import org.ow2.jasmine.kerneos.config.generated.KerneosConfig;
import org.ow2.jasmine.kerneos.config.generated.KerneosModule;
import org.ow2.jasmine.kerneos.config.generated.Module;
import org.ow2.jasmine.kerneos.config.generated.Modules;
import org.ow2.jasmine.kerneos.config.generated.ObjectFactory;
import org.ow2.jasmine.kerneos.config.generated.PromptBeforeClose;
import org.ow2.jasmine.kerneos.config.generated.Service;
import org.ow2.jasmine.kerneos.config.generated.Services;
import org.ow2.jasmine.kerneos.config.generated.SwfModule;
import org.ow2.jasmine.kerneos.service.ModuleEvent;
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

    private static final String GRAVITY_DESTINATION = "kerneos-gravity";

    /**
     * The JAXB context for rules packages serialization/deserialization. Must
     * be declared with all the potentially involved classes.
     */
    private JAXBContext jaxbContext;

    /**
     * List of registered modules.
     */
    private Map<String, Module> moduleMap = new Hashtable<String, Module>();

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
            String header = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_MODULE_MANIFEST);
            if (header != null) {
                try {
                    onModuleArrival(bundle, header);
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
            String header = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_MODULE_MANIFEST);
            if (header != null) {
                try {
                    onModuleDeparture(bundle, header);
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
                Services.class,
                Service.class,
                KerneosConfig.class,
                Module.class,
                Modules.class,
                IframeModule.class,
                SwfModule.class,
                PromptBeforeClose.class,
                JAXBElement.class});

        // Register the classes used with "kerneosConfig" service
        gcr.registerClasses(getId(), new Class[]{
                Services.class,
                Service.class,
                KerneosConfig.class,
                Module.class,
                Modules.class,
                IframeModule.class,
                SwfModule.class,
                PromptBeforeClose.class,
                JAXBElement.class
        });

        // Register the few configurations used with KerneosConfigService
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", GRAVITY_DESTINATION);
            properties.put("SERVICE", KerneosConstants.GRAVITY_SERVICE);
            properties.put("CHANNELS", new String[]{KerneosConstants.GRAVITY_CHANNEL});
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
            properties.put("CHANNELS", new String[]{KerneosConstants.GRANITE_CHANNEL});
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

        synchronized (moduleMap) {
            // Unregister all handled modules
            for (String moduleName : moduleMap.keySet()) {
                kerneosCore.unregister(moduleName);
            }
            // Remove all handled modules
            moduleMap.clear();
        }

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
    private void onModuleArrival(final Bundle bundle, final String name) {
        logger.debug("Add Kerneos Module: " + name);

        try {
            Module module = loadModuleConfig(bundle);

            // Fix Paths
            module.setBigIcon(name + "/" + module.getBigIcon());
            module.setSmallIcon(name + "/" + module.getSmallIcon());
            if (module instanceof SwfModule) {
                SwfModule swfModule = (SwfModule) module;
                swfModule.setFile(name + "/" + swfModule.getFile());
            }

            // Get the url used for resources of the bundle
            String url = bundle.getResource(KerneosConstants.KERNEOS_PATH).toString();
            kerneosCore.register(name, url);
            logger.info("Register \"" + name + "\" resources");

            // Add to the list
            synchronized (moduleMap) {
                moduleMap.put(name, module);
            }

            // Post an event
            ModuleEvent me = new ModuleEvent(module, ModuleEvent.LOAD);
            publisher.sendData(me);
        } catch (Exception e) {
            logger.warn(e, "Invalid module: " + name);
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

        Module module = null;
        synchronized (moduleMap) {
            module = moduleMap.remove(name);
        }

        if (module == null) {
            logger.warn("Try to remove an invalid bundle: " + name);
            return;
        }

        kerneosCore.unregister(name);
        logger.info("Unregister \"" + name + "\" resources");

        // Post an event
        ModuleEvent me = new ModuleEvent(module, ModuleEvent.UNLOAD);
        publisher.sendData(me);
    }


    /**
     * Load the module information.
     *
     * @param bundle the bundle corresponding to the module.
     * @return the information corresponding to the module.
     * @throws Exception the kerneos module configuration file is not found are is invalid.
     */
    private Module loadModuleConfig(final Bundle bundle) throws Exception {
        // Get bundle kerneos module xml file
        URL url = bundle.getResource(KerneosConstants.KERNEOS_MODULE_FILE);
        if (url != null) {
            InputStream resource = url.openStream();
            // Unmarshall it
            try {
                // Create an unmarshaller
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                // Deserialize the configuration file
                KerneosModule module = (KerneosModule) unmarshaller.unmarshal(resource);
                return module.getModulesList().getValue();

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
     * Get the Kerneos configuration.
     *
     * @return the Kerneos configuration.
     */
    public KerneosConfig getKerneosConfig() {
        return kerneosCore.getKerneosConfig();
    }

    /**
     * Get the module list.
     *
     * @return the module list.
     */
    public Modules getModules() {
        synchronized (moduleMap) {
            Modules modules = new Modules();
            for (Module module : moduleMap.values()) {
                JAXBElement<Module> jaxbElement = new JAXBElement<Module>(new QName("module"), Module.class, module);
                modules.getModulesList().add(jaxbElement);
            }
            return modules;
        }
    }

    /**
     * Get the service id.
     *
     * @return the service id.
     */
    public String getId() {
        return "kerneosConfig";
    }
}
