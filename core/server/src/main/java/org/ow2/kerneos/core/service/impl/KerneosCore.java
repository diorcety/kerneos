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

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.gravity.osgi.adapters.ea.EAConstants;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.core.ApplicationBundle;
import org.ow2.kerneos.core.ModuleBundle;
import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.KerneosContext;
import org.ow2.kerneos.core.config.generated.ManagerProperty;
import org.ow2.kerneos.core.config.generated.Service;
import org.ow2.kerneos.core.config.generated.SwfModule;
import org.ow2.kerneos.login.Session;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
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

    private Map<String, ApplicationBundle> applicationMap = new HashMap<String, ApplicationBundle>();

    private Map<String, ModuleBundle> moduleMap = new HashMap<String, ModuleBundle>();

    private Map<String, Service> serviceMap = new HashMap<String, Service>();

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
    private synchronized void start() throws IOException {
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
    private synchronized void stop() throws IOException {
        logger.debug("Stop KerneosCore");

        // Dispose configurations
        gravity.delete();
        gravitySecurity.delete();
        granite.delete();
        graniteSecurity.delete();

        // Dispose applications
        while (applicationMap.size() != 0) {
            try {
                String applicationId = applicationMap.keySet().iterator().next();
                removeApplicationBundle(applicationId);
            } catch (Exception e) {
            }
        }

        // Dispose modules
        while (moduleMap.size() != 0) {
            try {
                String moduleId = moduleMap.keySet().iterator().next();
                removeModuleBundle(moduleId);
            } catch (Exception e) {
            }
        }
    }

    public synchronized void addApplicationBundle(final ApplicationBundle applicationBundle) throws Exception {
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

                    applicationBundle.addConfiguration(instance);
                }
                if (applicationBundle.getApplication().getManagers().getProfile() != null) {
                    Configuration instance = configurationAdmin.createFactoryConfiguration(applicationBundle.getApplication().getManagers().getProfile().getId(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put("ID", applicationBundle.getId());
                    for (ManagerProperty prop : applicationBundle.getApplication().getManagers().getProfile().getProperty()) {
                        dictionary.put(prop.getId(), prop.getValue());
                    }
                    instance.update(dictionary);

                    applicationBundle.addConfiguration(instance);
                }
                if (applicationBundle.getApplication().getManagers().getRoles() != null) {
                    Configuration instance = configurationAdmin.createFactoryConfiguration(applicationBundle.getApplication().getManagers().getRoles().getId(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put("ID", applicationBundle.getId());
                    for (ManagerProperty prop : applicationBundle.getApplication().getManagers().getRoles().getProperty()) {
                        dictionary.put(prop.getId(), prop.getValue());
                    }
                    instance.update(dictionary);

                    applicationBundle.addConfiguration(instance);
                }
            }

            // Remove managers (security issue)
            applicationBundle.getApplication().setManagers(null);

            // HttpService
            Configuration httpInstance = configurationAdmin.createFactoryConfiguration(KerneosHttpService.class.getName());

            Dictionary dictionary = new Hashtable();
            dictionary.put("requires.filters", new String[]{
                    "login", "(ID=" + applicationBundle.getId() + ")",
                    "profile", "(ID=" + applicationBundle.getId() + ")",
                    "roles", "(ID=" + applicationBundle.getId() + ")",
            });
            dictionary.put("ID", applicationBundle.getId());
            httpInstance.update(dictionary);

            applicationBundle.addConfiguration(httpInstance);

            applicationMap.put(applicationBundle.getId(), applicationBundle);

        } catch (Exception e) {
            applicationBundle.dispose();
            throw e;
        }
    }

    public synchronized ApplicationBundle removeApplicationBundle(final String applicationId) throws Exception {
        ApplicationBundle applicationBundle = null;
        applicationBundle = applicationMap.remove(applicationId);
        applicationBundle.dispose();
        return applicationBundle;
    }

    public synchronized Map<String, ApplicationBundle> getApplicationBundles() {
        return new HashMap<String, ApplicationBundle>(applicationMap);
    }

    public synchronized ApplicationBundle getApplicationBundle(Bundle bundle) {
        for (ApplicationBundle applicationBundle : applicationMap.values()) {
            if (applicationBundle.getBundle() == bundle) {
                return applicationBundle;
            }
        }
        return null;
    }

    public synchronized ApplicationBundle getApplicationBundle(String applicationId) {
        return applicationMap.get(applicationId);
    }

    public synchronized void addModuleBundle(final ModuleBundle moduleBundle) throws Exception {
        moduleMap.put(moduleBundle.getId(), moduleBundle);
        if (moduleBundle.getModule() instanceof SwfModule) {
            SwfModule swfModule = (SwfModule) moduleBundle.getModule();
            for (Service service : swfModule.getServices()) {
                destinationMap.put(service.getDestination(), new ModuleService(moduleBundle, service));
            }
            for (Service service : swfModule.getServices()) {
                serviceMap.put(service.getId(), service);
            }
        }
    }

    public synchronized ModuleBundle removeModuleBundle(final String moduleId) throws Exception {
        ModuleBundle moduleBundle = moduleMap.remove(moduleId);
        if (moduleBundle.getModule() instanceof SwfModule) {
            SwfModule swfModule = (SwfModule) moduleBundle.getModule();
            for (Service service : swfModule.getServices()) {
                destinationMap.remove(service.getDestination());
            }
            for (Service service : swfModule.getServices()) {
                serviceMap.remove(service.getId());
            }
        }

        return moduleBundle;
    }

    public synchronized Map<String, ModuleBundle> getModuleBundles() {
        return new HashMap<String, ModuleBundle>(moduleMap);
    }

    public synchronized ModuleBundle getModuleBundle(Bundle bundle) {
        for (ModuleBundle moduleBundle : moduleMap.values()) {
            if (moduleBundle.getBundle() == bundle) {
                return moduleBundle;
            }
        }
        return null;
    }

    public synchronized ModuleBundle getModuleBundle(String moduleId) {
        return moduleMap.get(moduleId);
    }

    public synchronized Service getService(String serviceId) {
        Service service = serviceMap.get(serviceId);
        return service;
    }

    public void updateContext(HttpServletRequest request) {
        KerneosContext kerneosContext = KerneosContext.getCurrentContext();

        // Get the module and the associated path
        String path = request.getPathInfo();
        ModuleBundle currentModuleBundle = null;
        if (path != null && path.startsWith(KerneosConstants.KERNEOS_MODULE_URL)) {
            path = path.substring(KerneosConstants.KERNEOS_MODULE_URL.length());
            int sep = path.indexOf("/");
            if (sep != -1) {
                synchronized (this) {
                    currentModuleBundle = moduleMap.get(path.substring(0, sep));
                }
                path = path.substring(sep);
            }
        }

        kerneosContext.setModuleBundle(currentModuleBundle);
        kerneosContext.setPath(path);
        kerneosContext.setService(null);
        kerneosContext.setMethod(null);

        // Get or create a session
        Session session = null;
        Object obj = request.getSession().getAttribute(KERNEOS_SESSION_KEY);
        if (obj == null || !(obj instanceof Session)) {
            session = kerneosContext.getLoginManager().newSession();
            if (session.getRoles() != null) {
                session.setRoles(kerneosContext.getRolesManager().resolve(session.getRoles()));
            }
            request.getSession().setAttribute(KERNEOS_SESSION_KEY, session);
        } else {
            session = (Session) obj;
        }
        kerneosContext.setSession(session);
    }

    public void updateContext(String destination, String method) {
        KerneosContext kerneosContext = KerneosContext.getCurrentContext();
        kerneosContext.setPath(null);
        kerneosContext.setMethod(method);
        ModuleService moduleService;
        synchronized (this) {
            moduleService = destinationMap.get(destination);
        }
        if (moduleService != null) {
            kerneosContext.setModuleBundle(moduleService.getModuleBundle());
            kerneosContext.setService(moduleService.getService());
        }
    }

    class ModuleService {
        private Service service;
        private ModuleBundle moduleBundle;

        ModuleService(ModuleBundle moduleBundle, Service service) {
            this.service = service;
            this.moduleBundle = moduleBundle;
        }

        public ModuleBundle getModuleBundle() {
            return moduleBundle;
        }

        public Service getService() {
            return service;
        }
    }
}
