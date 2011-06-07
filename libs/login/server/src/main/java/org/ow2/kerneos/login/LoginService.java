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

package org.ow2.kerneos.login;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import org.ow2.kerneos.core.service.KerneosContext;
import org.ow2.kerneos.core.service.KerneosLogin;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;

@Component
@Instantiate
@Provides
public class LoginService implements KerneosLogin {

    private static final String KERNEOS_SECURITY_KEY = "KERNEOS-SECURITY";

    /**
     * CallbackHandler.
     */
    private CallbackHandler handler = null;

    public boolean login(final String application, final String user, final String password) {
        this.handler = new NoInputCallbackHandler(user, password);
        try {
            // Obtain a LoginContext
            LoginContext lc = new LoginContext("kerneos-" + application, this.handler);

            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            lc.login();


            Collection<String> roles = new LinkedList<String>();
            for (Principal principal : lc.getSubject().getPrincipals()) {
                roles.add(principal.getName());
            }
            KerneosContext.get().getCurrentHttpRequest().getSession().setAttribute(KERNEOS_SECURITY_KEY, roles);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Collection<String> getRoles() {
        return (Collection<String>) KerneosContext.get().getCurrentHttpRequest().getSession().getAttribute(KERNEOS_SECURITY_KEY);
    }

    public boolean logout() {
        KerneosContext.get().getCurrentHttpRequest().getSession().removeAttribute(KERNEOS_SECURITY_KEY);
        return true;
    }

    public boolean isLogged() {
        return (KerneosContext.get().getCurrentHttpRequest().getSession().getAttribute(KERNEOS_SECURITY_KEY) == null) ? false : true;
    }
}
