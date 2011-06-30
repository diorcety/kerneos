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

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.apache.felix.ipojo.api.composite.Instance;

import org.granite.gravity.osgi.adapters.ea.EAConstants;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.core.IApplicationBundle;
import org.ow2.kerneos.core.IModuleBundle;
import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.KerneosContext;
import org.ow2.kerneos.core.config.generated.ManagerProperty;
import org.ow2.kerneos.core.config.generated.Service;
import org.ow2.kerneos.core.config.generated.SwfModule;
import org.ow2.kerneos.login.KerneosSession;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
public class KerneosCore implements IKerneosCore {

    private static final String KERNEOS_SESSION_KEY = "KERNEOS-SESSION";

    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosConfigurationService.class);

    private Map<String, KerneosApplication> applicationMap = new HashMap<String, KerneosApplication>();

    private Map<String, IModuleBundle> moduleMap = new HashMap<String, IModuleBundle>();

    private Map<String, ModuleService> destinationMap = new HashMap<String, ModuleService>();


    private Configuration granite, gravity, graniteSecurity, gravitySecurity;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    private BundleContext bundleContext;

    /**
     * Constructor used by iPojo.
     */
    private KerneosCore(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        logger.debug("Start KerneosCore");

        // Gravity Configurations
        {
            Dictionary properties = new Hashtable();
            properties.put("service", KerneosConstants.GRAVITY_SERVICE);
            gravitySecurity = configurationAdmin.createFactoryConfiguration(org.ow2.kerneos.core.service.impl.granite.GraniteSecurityWrapper.class.getName(), null);
            gravitySecurity.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.GRAVITY_SERVICE);
            properties.put("messageTypes", "flex.messaging.messages.AsyncMessage");
            properties.put("defaultAdapter", EAConstants.ADAPTER_ID);
            gravity = configurationAdmin.createFactoryConfiguration(org.granite.config.flex.Service.class.getName(), null);
            gravity.update(properties);
        }

        // Granite Configurations
        {
            Dictionary properties = new Hashtable();
            properties.put("service", KerneosConstants.GRANITE_SERVICE);
            graniteSecurity = configurationAdmin.createFactoryConfiguration(org.ow2.kerneos.core.service.impl.granite.GraniteSecurityWrapper.class.getName(), null);
            graniteSecurity.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.GRANITE_SERVICE);
            granite = configurationAdmin.createFactoryConfiguration(org.granite.config.flex.Service.class.getName(), null);
            granite.update(properties);
        }
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        logger.debug("Stop KerneosCore");

        // Dispose configurations
        gravity.delete();
        gravitySecurity.delete();
        granite.delete();
        graniteSecurity.delete();
    }

    /**
     * Called when an Application instance is registered.
     *
     * @param applicationBundle the instance of an application
     */
    @Bind(aggregate = true, optional = true)
    private void bindApplicationBundle(final IApplicationBundle applicationBundle) throws IOException {
        KerneosApplication kerneosApplication = new KerneosApplication();
        try {
            // Managers
            if (applicationBundle.getApplication().getManagers() != null) {
                if (applicationBundle.getApplication().getManagers().getLogin() != null) {
                    Configuration instance = configurationAdmin.createFactoryConfiguration(applicationBundle.getApplication().getManagers().getLogin().getId(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put("ID", applicationBundle.getId());
                    for (ManagerProperty prop : applicationBundle.getApplication().getManagers().getLogin().getProperty()) {
                        dictionary.put(prop.getId(), prop.getValue());
                    }
                    instance.update(dictionary);

                    kerneosApplication.addInstance(instance);
                }
                if (applicationBundle.getApplication().getManagers().getProfile() != null) {
                    Configuration instance = configurationAdmin.createFactoryConfiguration(applicationBundle.getApplication().getManagers().getProfile().getId(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put("ID", applicationBundle.getId());
                    for (ManagerProperty prop : applicationBundle.getApplication().getManagers().getProfile().getProperty()) {
                        dictionary.put(prop.getId(), prop.getValue());
                    }
                    instance.update(dictionary);

                    kerneosApplication.addInstance(instance);
                }
                if (applicationBundle.getApplication().getManagers().getRoles() != null) {
                    Configuration instance = configurationAdmin.createFactoryConfiguration(applicationBundle.getApplication().getManagers().getRoles().getId(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put("ID", applicationBundle.getId());
                    for (ManagerProperty prop : applicationBundle.getApplication().getManagers().getRoles().getProperty()) {
                        dictionary.put(prop.getId(), prop.getValue());
                    }
                    instance.update(dictionary);

                    kerneosApplication.addInstance(instance);
                }
            }

            // Remove managers (security issue)
            applicationBundle.getApplication().setManagers(null);

            // HttpService
            Configuration httpInstance = configurationAdmin.createFactoryConfiguration(KerneosHttpService.class.getName());

            Dictionary dictionary = new Hashtable();
            dictionary.put("requires.filters", new String[]{
                    "application", "(ID=" + applicationBundle.getId() + ")",
                    "login", "(ID=" + applicationBundle.getId() + ")",
                    "profile", "(ID=" + applicationBundle.getId() + ")",
                    "roles", "(ID=" + applicationBundle.getId() + ")",
            });
            httpInstance.update(dictionary);

            kerneosApplication.addInstance(httpInstance);

            synchronized (applicationMap) {
                applicationMap.put(applicationBundle.getId(), kerneosApplication);
            }

        } catch (Exception e) {
            kerneosApplication.dispose();
            logger.error("Can't create the application \"" + applicationBundle.getId() + "\": " + e);
        }
    }

    /**
     * Called when an Application instance is unregistered.
     *
     * @param applicationBundle the instance of an application
     */
    @Unbind
    private void unbindApplicationBundle(final IApplicationBundle applicationBundle) throws IOException {
        synchronized (applicationMap) {
            KerneosApplication ka = applicationMap.remove(applicationBundle.getId());
            ka.dispose();
        }

    }

    @Bind(aggregate = true, optional = true)
    private void bindModuleBundle(final IModuleBundle moduleBundle) {
        synchronized (moduleMap) {
            moduleMap.put(moduleBundle.getId(), moduleBundle);
        }
        if (moduleBundle.getModule() instanceof SwfModule) {
            SwfModule swfModule = (SwfModule) moduleBundle.getModule();
            synchronized (destinationMap) {
                for (Service service : swfModule.getServices()) {
                    destinationMap.put(service.getDestination(), new ModuleService(moduleBundle, service));
                }
            }
        }
    }

    @Unbind
    private void unbindModuleBundle(final IModuleBundle moduleBundle) {
        synchronized (moduleMap) {
            moduleMap.remove(moduleBundle.getId());
        }
        if (moduleBundle.getModule() instanceof SwfModule) {
            SwfModule swfModule = (SwfModule) moduleBundle.getModule();
            synchronized (destinationMap) {
                for (Service service : swfModule.getServices()) {
                    destinationMap.remove(service.getDestination());
                }
            }
        }
    }


    public void updateContext(HttpServletRequest request) {
        KerneosContext kerneosContext = KerneosContext.getCurrentContext();

        IModuleBundle currentModuleBundle = null;
        for (IModuleBundle moduleBundle : moduleMap.values()) {
            if (request.getRequestURI().contains("/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleBundle.getId())) {
                currentModuleBundle = moduleBundle;
                break;
            }
        }
        kerneosContext.setModuleBundle(currentModuleBundle);
        kerneosContext.setService(null);
        kerneosContext.setMethod(null);

        // Get or create a session
        KerneosSession kerneosSession = null;
        Object obj = request.getSession().getAttribute(KERNEOS_SESSION_KEY);
        if (obj == null || !(obj instanceof KerneosSession)) {
            kerneosSession = kerneosContext.getLoginManager().newSession();
            if (kerneosSession.getRoles() != null) {
                kerneosSession.setRoles(kerneosContext.getRolesManager().resolve(kerneosSession.getRoles()));
            }
            request.getSession().setAttribute(KERNEOS_SESSION_KEY, kerneosSession);
        } else {
            kerneosSession = (KerneosSession) obj;
        }
        kerneosContext.setSession(kerneosSession);
    }

    public void updateContext(String destination, String method) {
        KerneosContext kerneosContext = KerneosContext.getCurrentContext();

        kerneosContext.setMethod(method);
        ModuleService moduleService = destinationMap.get(destination);
        if (moduleService != null) {
            kerneosContext.setModuleBundle(moduleService.getModuleBundle());
            kerneosContext.setService(moduleService.getService());
        }
    }

    class ModuleService {
        private Service service;
        private IModuleBundle moduleBundle;

        ModuleService(IModuleBundle moduleBundle, Service service) {
            this.service = service;
            this.moduleBundle = moduleBundle;
        }

        public IModuleBundle getModuleBundle() {
            return moduleBundle;
        }

        public Service getService() {
            return service;
        }
    }

    class KerneosApplication {

        private Collection<Configuration> instances = new LinkedList<Configuration>();

        synchronized void addInstance(Configuration instance) {
            instances.add(instance);
        }

        synchronized void dispose() {
            for (Configuration instance : instances) {
                try {
                    instance.delete();
                } catch (Exception e) {

                }
            }
            instances.clear();
        }
    }
}
