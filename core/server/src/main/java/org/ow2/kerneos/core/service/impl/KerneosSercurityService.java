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

@Component
@Instantiate
@Provides
public class KerneosSercurityService implements IKerneosSecurityService {

    private static final String KERNEOS_SECURITY_KEY = "KERNEOS-SECURITY-CONTEXT";

    @Requires(optional = true, defaultimplementation = DefaultKerneosLogin.class)
    KerneosLogin kerneosLogin;

    @Requires(optional = true, specification = "org.ow2.kerneos.core.IApplicationInstance")
    Collection<IApplicationInstance> applicationInstances;

    private static KerneosContext getKerneosContext() {
        HttpServletRequest request = KerneosHttpService.getCurrentHttpRequest();
        Object obj = request.getSession().getAttribute(KERNEOS_SECURITY_KEY);
        if (obj == null || !(obj instanceof KerneosContext))
            return null;
        return (KerneosContext) obj;
    }

    private static void setKerneosContext(KerneosContext kerneosContext) {
        HttpServletRequest request = KerneosHttpService.getCurrentHttpRequest();
        request.getSession().setAttribute(KERNEOS_SECURITY_KEY, kerneosContext);
    }

    private IApplicationInstance getApplicationInstance() {
        HttpServletRequest request = KerneosHttpService.getCurrentHttpRequest();
        for (IApplicationInstance applicationInstance : applicationInstances) {
            if (request.getRequestURI().startsWith(applicationInstance.getConfiguration().getApplicationUrl()))
                return applicationInstance;
        }

        return null;
    }

    public boolean isLogged() {
        KerneosContext kerneosContext = getKerneosContext();
        return kerneosContext != null;
    }

    public boolean login(String user, String password) {
        IApplicationInstance applicationInstance = getApplicationInstance();
        Collection<String> roles = kerneosLogin.login(applicationInstance.getId(), user, password);
        if (roles == null)
            return false;

        KerneosContext kerneosContext = new KerneosContext(user, roles);
        setKerneosContext(kerneosContext);
        return true;
    }

    public SecurityError authorize(Destination destination, Message message) {
        if (!isLogged()) {
            return SecurityError.SESSION_EXPIRED;
        }
        return SecurityError.NO_ERROR;
    }

    public boolean logout() {
        boolean logged_out = kerneosLogin.logout();
        if (!logged_out)
            return false;

        setKerneosContext(null);
        return true;
    }

    public Collection<String> getRoles() {
        return null;
    }

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
