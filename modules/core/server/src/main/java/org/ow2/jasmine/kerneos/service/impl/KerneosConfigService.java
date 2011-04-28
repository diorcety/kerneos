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

import org.granite.osgi.service.GraniteDestination;

import org.osgi.framework.Bundle;

import org.osgi.framework.BundleContext;
import org.osgi.service.http.NamespaceException;
import org.ow2.jasmine.kerneos.config.generated.KerneosConfig;
import org.ow2.jasmine.kerneos.config.generated.KerneosModule;
import org.ow2.jasmine.kerneos.config.generated.Module;
import org.ow2.jasmine.kerneos.config.generated.Modules;
import org.ow2.jasmine.kerneos.config.generated.ObjectFactory;
import org.ow2.jasmine.kerneos.service.ModuleEvent;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

@Component
@Instantiate
@Provides
public class KerneosConfigService implements GraniteDestination {
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

    @Requires
    IKerneosCore kerneosCore;

    @Requires(from = "org.granite.gravity.osgi.OSGiEventAdminAdapter")
    private Factory osgiAdapterFactory;

    @Requires(from = "org.granite.config.flex.Destination")
    private Factory destinationFactory;

    private BundleContext bundleContext;

    private ComponentInstance gravity_service_adapter, destination;
    private BundleTracker bundleTracker;

    private class KerneosBundleTracker extends BundleTracker {

        KerneosBundleTracker(BundleContext bundleContext) {
            super(bundleContext);
        }

        @Override
        protected void addedBundle(Bundle bundle) {
            String header = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_MODULE_MANIFEST);
            if (header != null) {
                try {
                    onModuleArrival(bundle, header);
                } catch (Exception ex) {

                }
            }
        }

        @Override
        protected void removedBundle(Bundle bundle) {
            String header = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_MODULE_MANIFEST);
            if (header != null) {
                try {
                    onModuleDeparture(bundle, header);
                } catch (Exception ex) {

                }
            }
        }
    }

    private KerneosConfigService(BundleContext bundleContext) throws Exception {
        bundleTracker = new KerneosBundleTracker(bundleContext);
        jaxbContext = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName(), ObjectFactory.class.getClassLoader());
    }

    @Validate
    private void start() throws MissingHandlerException, ConfigurationException, UnacceptableConfiguration, NamespaceException {
        logger.info("Start KerneosConfigService");
        {
            Dictionary properties = new Hashtable();
            Dictionary prop = new Hashtable();
            prop.put("GravitySubscriber", KerneosConstants.KERNEOS_CONFIG_TOPIC);
            prop.put("GravityPublisher", KerneosConstants.KERNEOS_CONFIG_TOPIC);
            properties.put("ID", KerneosConstants.GRAVITY_ADAPTER);
            properties.put("event.topics", prop);
            gravity_service_adapter = osgiAdapterFactory.createComponentInstance(properties);
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

        gravity_service_adapter.dispose();
    }

    private void onModuleArrival(Bundle bundle, String header) {
        logger.info("Add Kerneos Module: " + header);

        try {
            Module module = loadModuleConfig(bundle);

            // Get the url used for resources of the bundle
            String url = bundle.getResource(KerneosConstants.KERNEOS_PATH).toString();
            kerneosCore.register(header, url);
            logger.info("Register \"" + header + "\" resources");

            // Add to the list
            synchronized (moduleMap) {
                moduleMap.put(header, module);
            }

            ModuleEvent me = new ModuleEvent(module, ModuleEvent.LOAD);

            // Post event
            Dictionary<String, Object> prop = new Hashtable<String, Object>();
            prop.put("message.topic", KerneosConstants.KERNEOS_CONFIG_TOPIC);
            prop.put("message.destination", KerneosConstants.GRAVITY_DESTINATION);
            prop.put("message.data", me);
            publisher.send(prop);
        } catch (Exception e) {
            logger.warn(e, "Invalid module: " + header);
            return;
        }
    }

    private void onModuleDeparture(Bundle bundle, String header) {

        Module module = null;
        synchronized (moduleMap) {
            module = moduleMap.remove(header);
        }

        if (module == null) {
            logger.warn("Try to remove an invalid bundle: " + header);
            return;
        }

        kerneosCore.unregister(header);
        logger.info("Unregister \"" + header + "\" resources");

        ModuleEvent me = new ModuleEvent(module, ModuleEvent.UNLOAD);

        // Post event
        Dictionary<String, Object> prop = new Hashtable<String, Object>();
        prop.put("message.topic", KerneosConstants.KERNEOS_CONFIG_TOPIC);
        prop.put("message.destination", KerneosConstants.GRAVITY_DESTINATION);
        prop.put("message.data", me);
        publisher.send(prop);
    }

    private Module loadModuleConfig(Bundle bundle) throws Exception {
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


    public KerneosConfig getKerneosConfig() {
        return kerneosCore.getKerneosConfig();
    }

    public Modules getModules() {
        Modules modules = new Modules();
        for (Module module : moduleMap.values()) {
            JAXBElement<Module> jaxbElement = new JAXBElement<Module>(new QName("module"), Module.class, module);
            modules.getModulesList().add(jaxbElement);
        }
        return modules;
    }

    public String getId() {
        return "kerneosConfig";
    }
}
