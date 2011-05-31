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

import flex.messaging.messages.Message;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.granite.config.flex.Destination;
import org.ow2.kerneos.core.IApplicationInstance;
import org.ow2.kerneos.core.IModuleInstance;
import org.ow2.kerneos.core.config.generated.Authentication;
import org.ow2.kerneos.core.service.DefaultKerneosLogin;
import org.ow2.kerneos.core.service.KerneosLogin;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * The security service used by Kerneos.
 */
@Component
@Instantiate
@Provides
public class KerneosSercurityService implements IKerneosSecurityService {

    private static final String KERNEOS_SESSION_KEY = "KERNEOS-SESSION";

    @Requires(optional = true, defaultimplementation = DefaultKerneosLogin.class)
    KerneosLogin kerneosLogin;

    @Requires(optional = true, specification = "org.ow2.kerneos.core.IApplicationInstance")
    Collection<IApplicationInstance> applicationInstances;

    @Requires(optional = true, specification = "org.ow2.kerneos.core.IModuleInstance")
    Collection<IModuleInstance> moduleInstances;

    /**
     * Get the information about Kerneos session.
     *
     * @return The KerneosSession if it is present in the session.
     */
    private KerneosSession getKerneosSession() {
        HttpServletRequest request = KerneosHttpService.getCurrentHttpRequest();
        Object obj = request.getSession().getAttribute(KERNEOS_SESSION_KEY);
        if (obj == null || !(obj instanceof KerneosSession))
            return null;
        return (KerneosSession) obj;
    }

    /**
     * Set the information about Kerneos session.
     *
     * @param kerneosSession The KerneosSession.
     */
    private void setKerneosSession(KerneosSession kerneosSession) {
        HttpServletRequest request = KerneosHttpService.getCurrentHttpRequest();
        request.getSession().setAttribute(KERNEOS_SESSION_KEY, kerneosSession);
    }

    public void updateContext() {
        HttpServletRequest request = KerneosHttpService.getCurrentHttpRequest();
        IApplicationInstance currentApplicationInstance = null;
        IModuleInstance currentModuleInstance = null;
        for (IApplicationInstance applicationInstance : applicationInstances) {
            if (request.getRequestURI().startsWith(applicationInstance.getConfiguration().getApplicationUrl())) {
                currentApplicationInstance = applicationInstance;
                break;
            }
        }

        if (currentApplicationInstance == null)
            throw new RuntimeException("Invalid Kerneos Application");


        for (IModuleInstance moduleInstance : moduleInstances) {
            if (request.getRequestURI().startsWith(currentApplicationInstance.getConfiguration().getApplicationUrl() + "/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleInstance.getId())) {
                currentModuleInstance = moduleInstance;
                break;
            }
        }

        KerneosContext.set(new KerneosContext(currentApplicationInstance, currentModuleInstance));
    }

    /**
     * Check if there is the user is logged.
     *
     * @return True if the user is logged.
     */
    public boolean isLogged() {
        IApplicationInstance applicationInstance = KerneosContext.get().getApplicationInstance();
        switch (applicationInstance.getConfiguration().getAuthentication()) {
            case NONE:
                break;

            default:
                KerneosSession kerneosSession = getKerneosSession();

                return kerneosSession != null;
        }
        return true;
    }

    /**
     * Login to Kerneos.
     *
     * @param username The username used for login.
     * @param password The password used for login.
     * @return True if the login is successful.
     */
    public boolean login(String username, String password) {
        IApplicationInstance applicationInstance = KerneosContext.get().getApplicationInstance();
        switch (applicationInstance.getConfiguration().getAuthentication()) {
            case NONE:
                break;

            default:
                Collection<String> roles = kerneosLogin.login(applicationInstance.getId(), username, password);
                if (roles == null)
                    return false;

                KerneosSession kerneosSession = new KerneosSession(username, roles);
                setKerneosSession(kerneosSession);
        }
        return true;
    }

    /**
     * Check the authorisation associated to the request.
     *
     * @param destination The destination of the request.
     * @param message     The message of the request.
     * @return The status associated to the authorisation.
     */
    public SecurityError authorize(Destination destination, Message message) {
        IApplicationInstance applicationInstance = KerneosContext.get().getApplicationInstance();
        switch (applicationInstance.getConfiguration().getAuthentication()) {
            case NONE:
                break;

            default:
                if (!isLogged()) {
                    return SecurityError.SESSION_EXPIRED;
                }
                break;
        }
        return SecurityError.NO_ERROR;
    }

    /**
     * Logout of Kerneos.
     *
     * @return True if the logout is successful.
     */
    public boolean logout() {
        IApplicationInstance applicationInstance = KerneosContext.get().getApplicationInstance();
        switch (applicationInstance.getConfiguration().getAuthentication()) {
            case NONE:
                break;

            default:
                boolean logged_out = kerneosLogin.logout();
                if (!logged_out)
                    return false;
                setKerneosSession(null);
        }
        return true;
    }

    /**
     * Get the roles of the logged user.
     *
     * @return An array containing the roles associated to the logged user.
     */
    public Collection<String> getRoles() {
        KerneosSession kerneosSession = getKerneosSession();
        return (kerneosSession != null) ? kerneosSession.getRoles() : null;
    }

    /**
     * The Object used to store the session information.
     */
    class KerneosSession {
        private String user;
        private Collection<String> roles;

        KerneosSession(String user, Collection<String> roles) {
            this.user = user;
            this.roles = roles;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public Collection<String> getRoles() {
            return roles;
        }

        public void setRoles(Collection<String> roles) {
            this.roles = roles;
        }
    }
}
