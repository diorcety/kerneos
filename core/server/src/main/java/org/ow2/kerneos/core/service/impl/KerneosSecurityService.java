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

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.service.GraniteDestination;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.core.IApplicationBundle;
import org.ow2.kerneos.core.IModuleBundle;
import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.KerneosContext;
import org.ow2.kerneos.login.KerneosSession;
import org.ow2.kerneos.core.config.generated.Service;
import org.ow2.kerneos.core.config.generated.SwfModule;
import org.ow2.kerneos.core.manager.DefaultKerneosLogin;
import org.ow2.kerneos.core.manager.DefaultKerneosProfile;
import org.ow2.kerneos.core.manager.DefaultKerneosRoles;
import org.ow2.kerneos.core.manager.KerneosLogin;
import org.ow2.kerneos.core.manager.KerneosProfile;
import org.ow2.kerneos.core.manager.KerneosRoles;
import org.ow2.kerneos.profile.config.generated.Profile;
import org.ow2.kerneos.profile.config.generated.ProfileBundle;
import org.ow2.kerneos.profile.config.generated.ProfileMethod;
import org.ow2.kerneos.profile.config.generated.ProfilePolicy;
import org.ow2.kerneos.profile.config.generated.ProfileRule;
import org.ow2.kerneos.profile.config.generated.ProfileService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * The security service used by Kerneos.
 */
@Component
@Instantiate
@Provides
public class KerneosSecurityService implements IKerneosSecurityService, GraniteDestination {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosSecurityService.class);

    private static final String KERNEOS_SESSION_KEY = "KERNEOS-SESSION";

    @Requires(optional = true, defaultimplementation = DefaultKerneosLogin.class)
    private KerneosLogin kerneosLogin;

    @Requires(optional = true, defaultimplementation = DefaultKerneosRoles.class)
    private KerneosRoles kerneosRoles;

    @Requires(optional = true, defaultimplementation = DefaultKerneosProfile.class)
    private KerneosProfile kerneosProfile;


    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private GraniteClassRegistry gcr;

    private Map<String, IApplicationBundle> applicationMap = new HashMap<String, IApplicationBundle>();

    private Map<String, IModuleBundle> moduleMap = new HashMap<String, IModuleBundle>();

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

    private Map<String, ModuleService> destinationMap = new HashMap<String, ModuleService>();

    private Configuration graniteDestination, gravityDestination, eaConfig;

    @Validate
    private void start() throws IOException {
        logger.debug("Start KerneosSecurityService");

        gcr.registerClasses(KerneosConstants.KERNEOS_SERVICE_SECURITY, SecurityObjects.list());
        gcr.registerClasses(KerneosConstants.KERNEOS_SERVICE_ASYNC_SECURITY, SecurityObjects.list());

        // Register the synchronous service used with KerneosSecurityService
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.KERNEOS_SERVICE_SECURITY);
            properties.put("service", KerneosConstants.GRANITE_SERVICE);
            graniteDestination = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
            graniteDestination.update(properties);
        }

        // Register the asynchronous service used with KerneosSecurityService
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.KERNEOS_SERVICE_ASYNC_SECURITY);
            properties.put("service", KerneosConstants.GRAVITY_SERVICE);

            gravityDestination = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
            gravityDestination.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("destination", KerneosConstants.KERNEOS_SERVICE_ASYNC_SECURITY);

            eaConfig = configurationAdmin.createFactoryConfiguration("org.granite.gravity.osgi.adapters.ea.application", null);
            eaConfig.update(properties);
        }
    }

    @Invalidate
    private void stop() throws IOException {
        logger.debug("Stop KerneosSecurityService");

        graniteDestination.delete();
        gravityDestination.delete();
        eaConfig.delete();

        gcr.unregisterClasses(KerneosConstants.KERNEOS_SERVICE_SECURITY);
        gcr.unregisterClasses(KerneosConstants.KERNEOS_SERVICE_ASYNC_SECURITY);
    }


    @Bind(aggregate = true, optional = true)
    private void bindApplicationBundle(final IApplicationBundle applicationBundle) {
        synchronized (applicationMap) {
            applicationMap.put(applicationBundle.getId(), applicationBundle);
        }
    }

    @Unbind
    private void unbindApplicationBundle(final IApplicationBundle applicationBundle) {
        synchronized (applicationMap) {
            applicationMap.remove(applicationBundle.getId());
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

    /**
     * Update context following the request
     */
    public void updateContext(HttpServletRequest request) {
        updateContext(request, null, null);
    }

    /**
     * Update context following the request & destination & method
     */
    public void updateContext(HttpServletRequest request, String destination, String method) {
        KerneosSession kerneosSession = null;
        IApplicationBundle currentApplicationBundle = null;
        IModuleBundle currentModuleBundle = null;
        Service currentService = null;
        String currentMethod = method;

        for (IApplicationBundle applicationBundle : applicationMap.values()) {
            if (request.getRequestURI().startsWith(applicationBundle.getApplication().getApplicationUrl())) {
                currentApplicationBundle = applicationBundle;
                break;
            }
        }

        if (currentApplicationBundle == null)
            throw new RuntimeException("Invalid Kerneos Application");

        if (destination == null) {
            for (IModuleBundle moduleBundle : moduleMap.values()) {
                if (request.getRequestURI().startsWith(currentApplicationBundle.getApplication().getApplicationUrl() + "/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleBundle.getId())) {
                    currentModuleBundle = moduleBundle;
                    break;
                }
            }
        } else {
            ModuleService moduleService = destinationMap.get(destination);
            if (moduleService != null) {
                currentModuleBundle = moduleService.getModuleBundle();
                currentService = moduleService.getService();
            }
        }

        // Get or create a session
        Object obj = request.getSession().getAttribute(KERNEOS_SESSION_KEY);
        if (obj == null || !(obj instanceof KerneosSession)) {
            kerneosSession = kerneosLogin.newSession();
            if (kerneosSession.getRoles() != null) {
                kerneosSession.setRoles(kerneosRoles.resolve(kerneosSession.getRoles()));
            }
            request.getSession().setAttribute(KERNEOS_SESSION_KEY, kerneosSession);
        } else {
            kerneosSession = (KerneosSession) obj;
        }

        KerneosContext.setCurrentContext(new KerneosContext(kerneosSession, currentApplicationBundle, currentModuleBundle, currentService, currentMethod));
    }

    /**
     * Get the user' session.
     *
     * @return KerneosSession.
     */
    public KerneosSession getSession() {
        return KerneosContext.getCurrentContext().getSession();
    }

    public Profile getProfile() {
        return kerneosProfile.getProfile();
    }

    /**
     * Login to Kerneos.
     *
     * @param username The username used for login.
     * @param password The password used for login.
     * @return True if the login is successful.
     */
    public boolean logIn(String username, String password) {
        IApplicationBundle applicationBundle = KerneosContext.getCurrentContext().getApplicationBundle();
        switch (applicationBundle.getApplication().getAuthentication()) {
            case NONE:
                break;

            default:
                kerneosLogin.login(applicationBundle.getId(), username, password);
                KerneosSession kerneosSession = KerneosContext.getCurrentContext().getSession();
                if (kerneosSession.getRoles() != null) {
                    kerneosSession.setRoles(kerneosRoles.resolve(kerneosSession.getRoles()));
                }
                return KerneosContext.getCurrentContext().getSession().isLogged();
        }
        return true;
    }

    /**
     * Check the authorisation associated to the request.
     *
     * @return The status associated to the authorisation.
     */
    public SecurityError authorize() {
        IApplicationBundle applicationBundle = KerneosContext.getCurrentContext().getApplicationBundle();
        switch (applicationBundle.getApplication().getAuthentication()) {
            case NONE:
                break;

            case FLEX:
                if (!KerneosContext.getCurrentContext().getSession().isLogged()) {
                    if (KerneosContext.getCurrentContext().getModuleBundle() == null)
                        return SecurityError.NO_ERROR;
                }

            default:
                if (!KerneosContext.getCurrentContext().getSession().isLogged()) {
                    return SecurityError.SESSION_EXPIRED;
                }
                break;
        }

        KerneosContext kerneosContext = KerneosContext.getCurrentContext();
        Collection<String> roles = kerneosContext.getSession().getRoles();
        Profile profile = kerneosProfile.getProfile();

        if (kerneosContext.getModuleBundle() != null) {
            if (profile == null)
                return SecurityError.INVALID_CREDENTIALS;

            ProfilePolicy policy = getPolicy(profile.getDefaultRules(), roles, profile.getDefaultPolicy());

            ProfileBundle bundle = getBundle(profile.getBundles(), kerneosContext.getModuleBundle().getId());
            if (bundle != null) {
                policy = getPolicy(bundle.getRules(), roles, policy);
                if (policy == ProfilePolicy.DENY)
                    return SecurityError.INVALID_CREDENTIALS;

                if (kerneosContext.getService() != null) {
                    ProfileService service = getService(bundle.getServices(), kerneosContext.getService().getId());
                    if (service != null) {
                        policy = getPolicy(service.getRules(), roles, policy);
                        if (policy == ProfilePolicy.DENY)
                            return SecurityError.INVALID_CREDENTIALS;

                        if (kerneosContext.getMethod() != null) {
                            ProfileMethod method = getMethod(service.getMethods(), kerneosContext.getMethod());
                            if (method != null) {
                                policy = getPolicy(method.getRules(), roles, policy);
                                if (policy == ProfilePolicy.DENY)
                                    return SecurityError.INVALID_CREDENTIALS;
                            }
                        }
                    }
                }
            }

            return policy == ProfilePolicy.ALLOW ? SecurityError.NO_ERROR : SecurityError.INVALID_CREDENTIALS;
        }

        // Allow access to application only in other cases
        return SecurityError.NO_ERROR;
    }

    private ProfileBundle getBundle(List<ProfileBundle> bundles, String id) {
        for (ProfileBundle bundle : bundles) {
            if (bundle.getId().equals(id))
                return bundle;
        }
        return null;
    }

    private ProfileService getService(List<ProfileService> services, String id) {
        for (ProfileService service : services) {
            if (service.getId().equals(id))
                return service;
        }
        return null;
    }

    private ProfileMethod getMethod(List<ProfileMethod> methods, String id) {
        for (ProfileMethod method : methods) {
            if (method.getId().equals(id))
                return method;
        }
        return null;
    }

    private ProfilePolicy getPolicy(List<ProfileRule> rules, Collection<String> roles, ProfilePolicy defaultPolicy) {
        if (roles != null) {
            for (ProfileRule rule : rules) {
                if (roles.contains(rule.getRole()))
                    return rule.getPolicy();
            }
        }
        return defaultPolicy;
    }

    /**
     * Logout from Kerneos.
     *
     * @return True if the logout is successful.
     */
    public boolean logOut() {
        IApplicationBundle applicationBundle = KerneosContext.getCurrentContext().getApplicationBundle();
        switch (applicationBundle.getApplication().getAuthentication()) {
            case NONE:
                break;

            default:
                kerneosLogin.logout();
                return !KerneosContext.getCurrentContext().getSession().isLogged();
        }
        return true;
    }

    public String getId() {
        return KerneosConstants.KERNEOS_SERVICE_SECURITY;
    }

}
