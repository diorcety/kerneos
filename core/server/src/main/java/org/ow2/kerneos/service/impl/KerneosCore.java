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

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import org.ow2.kerneos.config.generated.Application;
import org.ow2.kerneos.config.generated.Module;
import org.ow2.kerneos.config.generated.SwfModule;
import org.ow2.kerneos.service.ApplicationInstance;
import org.ow2.kerneos.service.ModuleInstance;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;


/**
 * The core of Kerneos.
 */
@Component
@Instantiate
@Provides
public final class KerneosCore implements IKerneosCore {

    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosConfigService.class);

    /**
     * OSGi HTTPService.
     */
    @Requires
    private HttpService httpService;

    /**
     * Kerneos HTTP Context.
     */
    @Requires(filter = "(id=kerneos)")
    private HttpContext httpContext;

    private Map<String, ModuleInstanceImpl> moduleInstanceMap = new HashMap<String, ModuleInstanceImpl>();

    private Map<String, ApplicationInstanceImpl> applicationInstanceMap = new HashMap<String, ApplicationInstanceImpl>();

    private ComponentInstance granite, gravity;

    private class ModuleInstanceImpl extends ModuleInstance {
        private Bundle bundle;

        /**
         * Constructor
         */
        ModuleInstanceImpl(String name, Module module, Bundle bundle) {
            super(name, module);
            this.bundle = bundle;

            // Fix Paths with module name
            module.setBigIcon(KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + name + "/" + module.getBigIcon());
            module.setSmallIcon(KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + name + "/" + module.getSmallIcon());
            if (module instanceof SwfModule) {
                SwfModule swfModule = (SwfModule) module;
                swfModule.setFile(KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + name + "/" + swfModule.getFile());
            }
        }

        /**
         * Dispose all the service and the configuration associated with this instance.
         */
        public void dispose() {

        }
    }

    /**
     * Used for holding the different configuration/service associated with a Kerneos service.
     */
    private class ApplicationInstanceImpl extends ApplicationInstance {
        private Bundle bundle;
        private ComponentInstance gavityChannel, graniteChannel;

        /**
         * Constructor
         */
        public ApplicationInstanceImpl(String name, Application application,
                                       Bundle bundle) throws MissingHandlerException, ConfigurationException, UnacceptableConfiguration, NamespaceException {
            super(name, application);
            this.bundle = bundle;

            String applicationURL = application.getApplicationUrl();

            // Register channels
            {
                Dictionary properties = new Hashtable();
                properties.put("ID", KerneosConstants.GRAVITY_CHANNEL + name);
                properties.put("CLASS", "org.granite.gravity.channels.GravityChannel");
                properties.put("ENDPOINT_URI", applicationURL + KerneosConstants.GRAVITY_CHANNEL_URI);
                gavityChannel = channelFactory.createComponentInstance(properties);
            }
            {
                Dictionary properties = new Hashtable();
                properties.put("ID", KerneosConstants.GRANITE_CHANNEL + name);
                properties.put("ENDPOINT_URI", applicationURL + KerneosConstants.GRANITE_CHANNEL_URI);
                graniteChannel = channelFactory.createComponentInstance(properties);
            }

            // Register Kerneos Application resources
            httpService.registerResources(applicationURL,
                    bundle.getResource(KerneosConstants.KERNEOS_PATH).toString(),
                    httpContext);
            httpService.registerResources(applicationURL + "/" + KerneosConstants.KERNEOS_SWF_NAME,
                    KerneosConstants.KERNEOS_SWF_NAME, httpContext);

            logger.info("Register \"" + name + "\" resources: " + applicationURL);
        }

        public void registerModule(ModuleInstanceImpl moduleInstance) throws NamespaceException {
            httpService.registerResources(
                    configuration.getApplicationUrl() + "/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleInstance.getId(),
                    moduleInstance.bundle.getResource(KerneosConstants.KERNEOS_PATH).toString(), httpContext);
        }

        public void unregisterModule(ModuleInstanceImpl moduleInstance) {
            httpService.unregister(
                    configuration.getApplicationUrl() + "/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleInstance.getId());
        }

        /**
         * Dispose all the service and the configuration associated with this instance.
         */
        public void dispose() {
            String applicationURL = configuration.getApplicationUrl();

            gavityChannel.dispose();
            granite.dispose();

            // Unregister Kerneos resources
            logger.info("Unregister \"" + id + "\" resources: " + applicationURL);

            httpService.unregister(applicationURL);
            httpService.unregister(applicationURL + "/" + KerneosConstants.KERNEOS_SWF_NAME);
        }
    }

    @Requires(from = "org.granite.config.flex.Service")
    private Factory serviceFactory;

    @Requires(from = "org.granite.config.flex.Channel")
    private Factory channelFactory;

    @Requires(from = "org.granite.config.flex.Destination")
    private Factory destinationFactory;


    /**
     * Constructor used by iPojo.
     */
    private KerneosCore() {
    }

    /**
     * Called when all the component dependencies are met.
     *
     * @throws MissingHandlerException   issue during GraniteDS configuration.
     * @throws ConfigurationException    issue during GraniteDS configuration.
     * @throws UnacceptableConfiguration issue during GraniteDS configuration.
     * @throws NamespaceException        an invalid application url.
     */
    @Validate
    private void start() throws MissingHandlerException, ConfigurationException,
            UnacceptableConfiguration, NamespaceException {
        logger.debug("Start KerneosCore");

        // Gravity Configuration Instances
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", KerneosConstants.GRAVITY_SERVICE);
            properties.put("MESSAGETYPES", "flex.messaging.messages.AsyncMessage");
            properties.put("DEFAULT_ADAPTER", EAConstants.ADAPTER_ID);
            gravity = serviceFactory.createComponentInstance(properties);
        }

        // Granite Configuration Instances
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", KerneosConstants.GRANITE_SERVICE);
            granite = serviceFactory.createComponentInstance(properties);
        }
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() {
        logger.debug("Stop KerneosCore");


        // Dispose configuration
        gravity.dispose();
        granite.dispose();

        for (String name : moduleInstanceMap.keySet())
            unregisterModule(name);


        for (String name : applicationInstanceMap.keySet())
            unregisterApplication(name);

    }

    /**
     * Register a module.
     */
    public synchronized ModuleInstance registerModule(String name, Module module, Bundle bundle) throws Exception {
        ModuleInstanceImpl moduleInstance = new ModuleInstanceImpl(name, module, bundle);

        // Register Kerneos Module resources for the applications
        for (ApplicationInstanceImpl applicationInstance : applicationInstanceMap.values()) {
            applicationInstance.registerModule(moduleInstance);
        }

        // Add the module to the handled module list
        moduleInstanceMap.put(name, moduleInstance);

        return moduleInstance;
    }

    /**
     * Unregister a module.
     */
    public synchronized ModuleInstance unregisterModule(String name) {
        ModuleInstanceImpl moduleInstance = moduleInstanceMap.remove(name);

        // Register Kerneos Module resources for the applications
        for (ApplicationInstanceImpl applicationInstance : applicationInstanceMap.values()) {
            applicationInstance.unregisterModule(moduleInstance);
        }

        //Dispose configurations
        moduleInstance.dispose();

        return moduleInstance;
    }

    /**
     * Get Module list.
     */
    public synchronized Collection<ModuleInstance> getModules() {
        return new LinkedList<ModuleInstance>(moduleInstanceMap.values());
    }

    /**
     * Register a module.
     */
    public synchronized ApplicationInstance registerApplication(String name, Application application,
                                                                Bundle bundle) throws Exception {
        ApplicationInstanceImpl applicationInstance = new ApplicationInstanceImpl(name, application, bundle);

        // Register Kerneos Module resources for this application
        for (ModuleInstanceImpl moduleInstance : moduleInstanceMap.values()) {
            applicationInstance.registerModule(moduleInstance);
        }

        // Add the application to the handled application list
        applicationInstanceMap.put(name, applicationInstance);

        return applicationInstance;
    }

    /**
     * Unregister a module.
     */
    public synchronized ApplicationInstance unregisterApplication(String name) {
        ApplicationInstanceImpl applicationInstance = applicationInstanceMap.remove(name);

        // UnRegister Kerneos Module resources for this application
        for (ModuleInstanceImpl moduleInstance : moduleInstanceMap.values()) {
            applicationInstance.unregisterModule(moduleInstance);
        }

        //Dispose configurations
        applicationInstance.dispose();

        return applicationInstance;
    }

    /**
     * Get Application list.
     */
    public synchronized Collection<ApplicationInstance> getApplications() {
        return new LinkedList<ApplicationInstance>(applicationInstanceMap.values());
    }
}
