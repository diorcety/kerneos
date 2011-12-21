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

package org.ow2.kerneos.core;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.kerneos.common.service.KerneosApplication;
import org.ow2.kerneos.common.service.KerneosModule;
import org.ow2.kerneos.common.service.KerneosSecurityService;
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
import java.util.List;

/**
 * The security service used by Kerneos.
 */
@Component
@Instantiate
@Provides
public class SecurityService implements KerneosSecurityService {
    /**
     * The logger.
     */
    private static final Log LOGGER = LogFactory.getLog(SecurityService.class);

    SecurityService() {

    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        LOGGER.debug("Start Kerneos SecurityService");
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        LOGGER.debug("Stop Kerneos SecurityService");
    }

    /**
     * Login to Kerneos.
     *
     * @param username The username used for login.
     * @param password The password used for login.
     * @return True if the login is successful.
     */
    public boolean logIn(KerneosApplication application, String username, String password) {
        switch (application.getConfiguration().getAuthentication()) {
            case NONE:
                break;

            default:
                application.getLoginManager().login(application.getId(), username, password);
                KerneosSession session = KerneosSession.getCurrent();
                if (session.getRoles() != null) {
                    session.setRoles(application.getRolesManager().resolve(session.getRoles()));
                }
                return KerneosSession.getCurrent().isLogged();
        }
        return true;
    }

    /**
     * Check the authorisation associated to the request.
     *
     * @return The status associated to the authorisation.
     */
    public SecurityError isAuthorized(KerneosApplication application, KerneosModule module, String service,
                                      String method) {
        KerneosSession session = KerneosSession.getCurrent();
        switch (application.getConfiguration().getAuthentication()) {
            case NONE:
                break;

            case FLEX:
                if (!session.isLogged()) {
                    if (module == null) {
                        return SecurityError.NO_ERROR;
                    }
                }

            default:
                if (!session.isLogged()) {
                    return SecurityError.SESSION_EXPIRED;
                }
                break;
        }

        Collection<String> roles = session.getRoles();
        Profile profile = application.getProfileManager().getProfile();

        if (module != null) {
            if (profile == null) {
                return SecurityError.ACCESS_DENIED;
            }

            ProfilePolicy policy = getPolicy(profile.getDefaultRules(), roles, profile.getDefaultPolicy());

            ProfileBundle profileBundle = getBundle(profile.getBundles(), module.getId());
            if (profileBundle != null) {
                policy = getPolicy(profileBundle.getRules(), roles, policy);

                if (service != null) {
                    ProfileService profileService = getService(profileBundle.getServices(), service);
                    if (profileService != null) {
                        policy = getPolicy(profileService.getRules(), roles, policy);

                        if (method != null) {
                            ProfileMethod profileMethod = getMethod(profileService.getMethods(), method);
                            if (profileMethod != null) {
                                policy = getPolicy(profileMethod.getRules(), roles, policy);
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
            if (bundle.getId().equals(id)) {
                return bundle;
            }
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
            if (service.getId().equals(id)) {
                return service;
            }
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
            if (method.getId().equals(id)) {
                return method;
            }
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
                if (roles.contains(rule.getRole())) {
                    if (policy == null || rule.getPolicy() == ProfilePolicy.ALLOW) {
                        policy = rule.getPolicy();
                    }
                }
            }
        }
        return (policy != null) ? policy : defaultPolicy;
    }

    /**
     * Logout from Kerneos.
     *
     * @return True if the logout is successful.
     */
    public boolean logOut(KerneosApplication application) {
        switch (application.getConfiguration().getAuthentication()) {
            case NONE:
                break;

            default:
                application.getLoginManager().logout();
                return !KerneosSession.getCurrent().isLogged();
        }
        return true;
    }
}
