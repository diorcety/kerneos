/**
 * Kerneos
 * Copyright (C) 2008 Bull S.A.S.
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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.granite.messaging.service.annotations.RemoteDestination;
import org.ow2.jonas.security.auth.callback.NoInputCallbackHandler;

@RemoteDestination(id = "logIn")
public class LoginService implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7807669487844076133L;

    /**
     * CallbackHandler.
     */
    private CallbackHandler handler = null;

    /**
     * LoginContext.
     */
    private LoginContext lc = null;

    /**
     * Login method. Use JOnAS JAAS configuration.
     *
     * @param user     user name
     * @param password password
     * @return true if the user successfully logged in, false otherwise.
     */

    @SuppressWarnings("unchecked")
    public boolean login(final String user, final String password) {

        try {
            // try for Jonas 4.X first
            Class noInputCallbackHandler =
                    Class.forName("org.objectweb.jonas.security.auth.callback.NoInputCallbackHandler");
            Constructor constructor =
                    noInputCallbackHandler.getDeclaredConstructor(new Class[]{String.class, String.class});
            this.handler = (CallbackHandler) constructor.newInstance(new Object[]{user, password});

        } catch (Exception e) {
            // try for Jonas 5.X then
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Login on JOnAS 5");
            this.handler = new NoInputCallbackHandler(user, password);
        }

        try {
            // Obtain a LoginContext
            lc = new LoginContext("eos", this.handler);

            lc.login();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }


    /**
     * Log out from the console.
     *
     * @return true if the user successfully logged out, false otherwise.
     */
    public boolean logOut() {
        try {
            lc.logout();
        } catch (LoginException e) {
            return false;
        }
        return true;
    }
}
