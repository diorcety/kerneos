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

import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.service.GraniteDestination;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.core.IApplicationBundle;
import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.KerneosContext;
import org.ow2.kerneos.login.KerneosSession;
import org.ow2.kerneos.profile.config.generated.Profile;
import org.ow2.kerneos.profile.config.generated.ProfileBundle;
import org.ow2.kerneos.profile.config.generated.ProfileMethod;
import org.ow2.kerneos.profile.config.generated.ProfilePolicy;
import org.ow2.kerneos.profile.config.generated.ProfileRule;
import org.ow2.kerneos.profile.config.generated.ProfileService;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

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

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private GraniteClassRegistry gcr;

    private Map<String, IApplicationBundle> applicationMap = new HashMap<String, IApplicationBundle>();



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

    /**
     * Get the user' session.
     *
     * @return KerneosSession.
     */
    public KerneosSession getSession() {
        return KerneosContext.getCurrentContext().getSession();
    }

    public Profile getProfile() {
        return KerneosContext.getCurrentContext().getProfileManager().getProfile();
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
                KerneosContext.getCurrentContext().getLoginManager().login(applicationBundle.getId(), username, password);
                KerneosSession kerneosSession = KerneosContext.getCurrentContext().getSession();
                if (kerneosSession.getRoles() != null) {
                    kerneosSession.setRoles(KerneosContext.getCurrentContext().getRolesManager().resolve(kerneosSession.getRoles()));
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
        Profile profile = KerneosContext.getCurrentContext().getProfileManager().getProfile();

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
                KerneosContext.getCurrentContext().getLoginManager().logout();
                return !KerneosContext.getCurrentContext().getSession().isLogged();
        }
        return true;
    }

    public String getId() {
        return KerneosConstants.KERNEOS_SERVICE_SECURITY;
    }

}
