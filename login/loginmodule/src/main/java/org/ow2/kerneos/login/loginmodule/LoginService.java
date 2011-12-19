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

package org.ow2.kerneos.login.loginmodule;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;

import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.kerneos.login.KerneosLogin;
import org.ow2.kerneos.login.KerneosSession;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;

@Component
@Provides
public class LoginService implements KerneosLogin {
    /**
     * The logger.
     */
    private static final Log LOGGER = LogFactory.getLog(LoginService.class);

    @Property(name = KerneosLogin.ID, mandatory = true)
    @ServiceProperty(name = KerneosLogin.ID)
    private String id;

    @Property(name = "module", mandatory = true)
    private String module;

    /**
     * CallbackHandler.
     */
    private CallbackHandler handler = null;

    @Validate
    private void start() throws IOException {
        LOGGER.debug("Start LoginService(" + id + ")");
    }

    @Invalidate
    private void stop() throws IOException {
        LOGGER.debug("Stop LoginService(" + id + ")");
    }

    public void login(final String application, final String user, final String password) {
        this.handler = new NoInputCallbackHandler(user, password);
        try {
            // Obtain a LoginContext
            LoginContext lc = new LoginContext(module, this.handler);

            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            lc.login();

            Collection<String> roles = new LinkedList<String>();
            for (Principal principal : lc.getSubject().getPrincipals()) {
                roles.add(principal.getName());
            }

            KerneosSession.getCurrent().setUsername(user);
            KerneosSession.getCurrent().setRoles(roles);
        } catch (Exception e) {
            LOGGER.warn("Unexpected error during login", e);
        }
    }

    public void logout() {
        KerneosSession.getCurrent().reset();
    }

    public KerneosSession newSession() {
        return new KerneosSession();
    }
}
