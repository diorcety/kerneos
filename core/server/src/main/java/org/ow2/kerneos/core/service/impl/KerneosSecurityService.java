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

import org.ow2.kerneos.core.ApplicationBundle;
import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.KerneosContext;
import org.ow2.kerneos.login.Session;
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

    private Map<String, ApplicationBundle> applicationMap = new HashMap<String, ApplicationBundle>();


    private Configuration graniteDestination, gravityDestination, eaConfig;

    /**
     * Called when all the component dependencies are met.
     */
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

            eaConfig = configurationAdmin.createFactoryConfiguration("org.granite.gravity.osgi.adapters.ea.configuration", null);
            eaConfig.update(properties);
        }
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
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
    public Session getSession() {
        return KerneosContext.getCurrentContext().getSession();
    }

    /**
     * Get the application profile.
     *
     * @return Profile.
     */
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
        ApplicationBundle applicationBundle = KerneosContext.getCurrentContext().getApplicationBundle();
        switch (applicationBundle.getApplication().getAuthentication()) {
            case NONE:
                break;

            default:
                KerneosContext.getCurrentContext().getLoginManager().login(applicationBundle.getId(), username, password);
                Session session = KerneosContext.getCurrentContext().getSession();
                if (session.getRoles() != null) {
                    session.setRoles(KerneosContext.getCurrentContext().getRolesManager().resolve(session.getRoles()));
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
        ApplicationBundle applicationBundle = KerneosContext.getCurrentContext().getApplicationBundle();
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
                return SecurityError.ACCESS_DENIED;

            ProfilePolicy policy = getPolicy(profile.getDefaultRules(), roles, profile.getDefaultPolicy());

            ProfileBundle bundle = getBundle(profile.getBundles(), kerneosContext.getModuleBundle().getId());
            if (bundle != null) {
                policy = getPolicy(bundle.getRules(), roles, policy);

                if (kerneosContext.getService() != null) {
                    ProfileService service = getService(bundle.getServices(), kerneosContext.getService().getId());
                    if (service != null) {
                        policy = getPolicy(service.getRules(), roles, policy);

                        if (kerneosContext.getMethod() != null) {
                            ProfileMethod method = getMethod(service.getMethods(), kerneosContext.getMethod());
                            if (method != null) {
                                policy = getPolicy(method.getRules(), roles, policy);
                            }
                        }
                    }
                }
            }

            return policy == ProfilePolicy.ALLOW ? SecurityError.NO_ERROR : SecurityError.ACCESS_DENIED;
        }

        // Allow access to application only in other cases
        return SecurityError.NO_ERROR;
    }

    /**
     * Get a bundle using its id.
     *
     * @param bundles the list of bundles.
     * @param id      the id of the bundle to find.
     * @return the bundle with the matching id or null otherwise.
     */
    private ProfileBundle getBundle(List<ProfileBundle> bundles, String id) {
        for (ProfileBundle bundle : bundles) {
            if (bundle.getId().equals(id))
                return bundle;
        }
        return null;
    }

    /**
     * Get a service using its id.
     *
     * @param services the list of services.
     * @param id       the id of the service to find.
     * @return the service with the matching id or null otherwise.
     */
    private ProfileService getService(List<ProfileService> services, String id) {
        for (ProfileService service : services) {
            if (service.getId().equals(id))
                return service;
        }
        return null;
    }

    /**
     * Get a method using its id.
     *
     * @param methods the list of methods.
     * @param id      the id of the method to find.
     * @return the method with the matching id or null otherwise.
     */
    private ProfileMethod getMethod(List<ProfileMethod> methods, String id) {
        for (ProfileMethod method : methods) {
            if (method.getId().equals(id))
                return method;
        }
        return null;
    }

    /**
     * Get the policy associated corresponding to the rules and the roles
     *
     * @param rules         the list of rules.
     * @param roles         the list of user's roles.
     * @param defaultPolicy the default policy to use if no matching rules is found.
     * @return the policy.
     */
    private ProfilePolicy getPolicy(List<ProfileRule> rules, Collection<String> roles, ProfilePolicy defaultPolicy) {
        ProfilePolicy policy = null;
        if (roles != null) {
            for (ProfileRule rule : rules) {
                if (roles.contains(rule.getRole()))
                    if (policy == null || rule.getPolicy() == ProfilePolicy.ALLOW)
                        policy = rule.getPolicy();
            }
        }
        return (policy != null) ? policy : defaultPolicy;
    }

    /**
     * Logout from Kerneos.
     *
     * @return True if the logout is successful.
     */
    public boolean logOut() {
        ApplicationBundle applicationBundle = KerneosContext.getCurrentContext().getApplicationBundle();
        switch (applicationBundle.getApplication().getAuthentication()) {
            case NONE:
                break;

            default:
                KerneosContext.getCurrentContext().getLoginManager().logout();
                return !KerneosContext.getCurrentContext().getSession().isLogged();
        }
        return true;
    }

    /**
     * Security service id
     */
    public String getId() {
        return KerneosConstants.KERNEOS_SERVICE_SECURITY;
    }

}
