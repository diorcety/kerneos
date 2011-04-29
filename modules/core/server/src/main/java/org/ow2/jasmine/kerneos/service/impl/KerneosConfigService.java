/**
 * Kerneos
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

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
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
import org.ow2.jasmine.kerneos.config.generated.SharedLibraries;
import org.ow2.jasmine.kerneos.config.generated.SwfModule;
import org.ow2.jasmine.kerneos.service.ModuleEvent;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

@Component
@Instantiate
@Provides
public final class KerneosConfigService implements GraniteDestination {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosConfigService.class);

    /**
     * The JAXB context for rules packages serialization/deserialization. Must
     * be declared with all the potentially involved classes.
     */
    private JAXBContext jaxbContext;

    /**
     * List of registered modules
     */
    private Map<String, Module> moduleMap = new Hashtable<String, Module>();

    /**
     * Async Event Publisher
     */
    @org.apache.felix.ipojo.handlers.event.Publisher(
            name = "KerneosConfigService",
            topics = KerneosConstants.KERNEOS_CONFIG_TOPIC
    )

    private Publisher publisher;

    /**
     * Granite Class Registry
     */
    @Requires
    private GraniteClassRegistry gcr;

    @Requires
    private IKerneosCore kerneosCore;

    @Requires(from = "org.granite.gravity.osgi.OSGiEventAdminAdapter")
    private Factory osgiAdapterFactory;

    @Requires(from = "org.granite.config.flex.Destination")
    private Factory destinationFactory;

    private ComponentInstance eaAdapter, destination;
    private BundleTracker bundleTracker;

    private class KerneosBundleTracker extends BundleTracker {

        KerneosBundleTracker(final BundleContext bundleContext) {
            super(bundleContext);
        }

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
     * Constructor
     *
     * @param bundleContext
     * @throws Exception
     */
    private KerneosConfigService(final BundleContext bundleContext) throws Exception {
        bundleTracker = new KerneosBundleTracker(bundleContext);
        jaxbContext = JAXBContext.newInstance(
                ObjectFactory.class.getPackage().getName(),
                ObjectFactory.class.getClassLoader());
    }

    @Validate
    private void start() throws MissingHandlerException, ConfigurationException,
            UnacceptableConfiguration, NamespaceException {
        gcr.registerClass(getId(), new Class[]{
                Services.class,
                Service.class,
                KerneosConfig.class,
                Module.class,
                Modules.class,
                IframeModule.class,
                SwfModule.class,
                PromptBeforeClose.class,
                JAXBElement.class,
                SharedLibraries.class
        });

        logger.info("Start KerneosConfigService");
        {
            Dictionary properties = new Hashtable();
            Dictionary prop = new Hashtable();
            prop.put("GravitySubscriber", KerneosConstants.KERNEOS_CONFIG_TOPIC);
            prop.put("GravityPublisher", KerneosConstants.KERNEOS_CONFIG_TOPIC);
            properties.put("ID", KerneosConstants.GRAVITY_ADAPTER);
            properties.put("event.topics", prop);
            eaAdapter = osgiAdapterFactory.createComponentInstance(properties);
        }
        {
            Collection<String> channels = new LinkedList<String>();
            channels.add(KerneosConstants.GRANITE_CHANNEL);
            Dictionary properties = new Hashtable();
            properties.put("ID", getId());
            properties.put("SERVICE", KerneosConstants.GRANITE_SERVICE);
            properties.put("CHANNELS", channels);
            destination = destinationFactory.createComponentInstance(properties);
        }
        bundleTracker.open();
    }

    @Invalidate
    private void stop() {
        logger.info("Stop KerneosConfigService");

        bundleTracker.close();

        moduleMap.clear();

        eaAdapter.dispose();

        gcr.unregisterClass(getId());
    }

    /**
     * Call when a new Kerneos Module is arrived
     *
     * @param bundle The Bundle corresponding to the module
     * @param name   The name of the module
     */
    private void onModuleArrival(final Bundle bundle, final String name) {
        logger.info("Add Kerneos Module: " + name);

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

            ModuleEvent me = new ModuleEvent(module, ModuleEvent.LOAD);

            // Post event
            Dictionary<String, Object> prop = new Hashtable<String, Object>();
            prop.put("message.topic", KerneosConstants.KERNEOS_CONFIG_TOPIC);
            prop.put("message.destination", KerneosConstants.GRAVITY_DESTINATION);
            prop.put("message.data", me);
            publisher.send(prop);
        } catch (Exception e) {
            logger.warn(e, "Invalid module: " + name);
            return;
        }
    }

    /**
     * Call when a new Kerneos Module is gone
     *
     * @param bundle The Bundle corresponding to the module
     * @param name   The name of the module
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

        ModuleEvent me = new ModuleEvent(module, ModuleEvent.UNLOAD);

        // Post event
        Dictionary<String, Object> prop = new Hashtable<String, Object>();
        prop.put("message.topic", KerneosConstants.KERNEOS_CONFIG_TOPIC);
        prop.put("message.destination", KerneosConstants.GRAVITY_DESTINATION);
        prop.put("message.data", me);
        publisher.send(prop);
    }


    /**
     * Load the module information
     *
     * @param bundle the bundle corresponding to the module
     * @return the information corresponding to the module
     * @throws Exception
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
     * Get the Kerneos configuration
     *
     * @return the Kerneos configuration
     */
    public KerneosConfig getKerneosConfig() {
        return kerneosCore.getKerneosConfig();
    }

    /**
     * Get the module list
     *
     * @return the module list
     */
    public Modules getModules() {
        Modules modules = new Modules();
        for (Module module : moduleMap.values()) {
            JAXBElement<Module> jaxbElement = new JAXBElement<Module>(new QName("module"), Module.class, module);
            modules.getModulesList().add(jaxbElement);
        }
        return modules;
    }

    /**
     * Get the service id
     *
     * @return the service id
     */
    public String getId() {
        return "kerneosConfig";
    }
}
