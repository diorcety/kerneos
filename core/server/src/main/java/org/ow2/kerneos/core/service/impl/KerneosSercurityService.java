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
import org.ow2.kerneos.core.service.DefaultKerneosLogin;
import org.ow2.kerneos.core.service.KerneosLogin;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * The security service used by Kerneos.
 */
@Component
@Instantiate
@Provides
public class KerneosSercurityService implements IKerneosSecurityService {

    private static final String KERNEOS_SECURITY_KEY = "KERNEOS-SECURITY-CONTEXT";

    @Requires(optional = true, defaultimplementation = DefaultKerneosLogin.class)
    KerneosLogin kerneosLogin;

    @Requires(optional = true, specification = "org.ow2.kerneos.core.IApplicationInstance")
    Collection<IApplicationInstance> applicationInstances;

    /**
     * Get the Kerneos context of the session.
     *
     * @return The KerneosContext if it is present in the session.
     */
    private KerneosContext getKerneosContext() {
        HttpServletRequest request = KerneosHttpService.getCurrentHttpRequest();
        Object obj = request.getSession().getAttribute(KERNEOS_SECURITY_KEY);
        if (obj == null || !(obj instanceof KerneosContext))
            return null;
        return (KerneosContext) obj;
    }

    /**
     * Set the Kerneos context of the session.
     *
     * @param kerneosContext The KerneosContext.
     */
    private void setKerneosContext(KerneosContext kerneosContext) {
        HttpServletRequest request = KerneosHttpService.getCurrentHttpRequest();
        request.getSession().setAttribute(KERNEOS_SECURITY_KEY, kerneosContext);
    }

    /**
     * Get the application associated to the current handled request.
     *
     * @return The application instance associated to the request.
     */
    private IApplicationInstance getApplicationInstance() {
        HttpServletRequest request = KerneosHttpService.getCurrentHttpRequest();
        for (IApplicationInstance applicationInstance : applicationInstances) {
            if (request.getRequestURI().startsWith(applicationInstance.getConfiguration().getApplicationUrl()))
                return applicationInstance;
        }

        return null;
    }

    /**
     * Check if there is the user is logged.
     *
     * @return True if the user is logged.
     */
    public boolean isLogged() {
        KerneosContext kerneosContext = getKerneosContext();
        return kerneosContext != null;
    }

    /**
     * Login to Kerneos.
     *
     * @param username The username used for login.
     * @param password The password used for login.
     * @return True if the login is successful.
     */
    public boolean login(String username, String password) {
        IApplicationInstance applicationInstance = getApplicationInstance();
        Collection<String> roles = kerneosLogin.login(applicationInstance.getId(), username, password);
        if (roles == null)
            return false;

        KerneosContext kerneosContext = new KerneosContext(username, roles);
        setKerneosContext(kerneosContext);
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
        if (!isLogged()) {
            return SecurityError.SESSION_EXPIRED;
        }
        return SecurityError.NO_ERROR;
    }

    /**
     * Logout of Kerneos.
     *
     * @return True if the logout is successful.
     */
    public boolean logout() {
        boolean logged_out = kerneosLogin.logout();
        if (!logged_out)
            return false;

        setKerneosContext(null);
        return true;
    }

    /**
     * Get the roles of the logged user.
     *
     * @return An array containing the roles associated to the logged user.
     */
    public Collection<String> getRoles() {
        return null;
    }

    /**
     * The Object used to store the session information.
     */
    class KerneosContext {
        String user;
        Collection<String> roles;

        KerneosContext(String user, Collection<String> roles) {
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
