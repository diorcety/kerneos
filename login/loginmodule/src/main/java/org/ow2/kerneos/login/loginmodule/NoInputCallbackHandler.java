/**
 * JOnAS: Java(TM) Open Application Server
 * Copyright (C) 1999-2007 Bull S.A.S.
 * Contact: jonas-team@ow2.org
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
 * Initial developer: Florent Benoit
 * --------------------------------------------------------------------------
 * $Id: NoInputCallbackHandler.java 15428 2008-10-07 11:20:29Z sauthieg $
 * --------------------------------------------------------------------------
 */

package org.ow2.kerneos.login.loginmodule;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.security.cert.Certificate;

/**
 * The username and password are given by the constructor No JNDI, datasource or
 * file checks.
 *
 * @author Florent Benoit (initial developer)
 * @author Alexandre Thaveau (add the use of certificates)
 * @author Marc-Antoine Bourgeot (add the use of certificates)
 */
public class NoInputCallbackHandler implements CallbackHandler {

    /**
     * Username to use.
     */
    private String username = null;

    /**
     * Password to use.
     */
    private String password = null;

    /**
     * Certificate to use, optional.
     */
    private Certificate cert = null;

    /**
     * No default Constructor : must use the one with username and password.
     *
     * @throws Exception if someone try to use it
     */
    public NoInputCallbackHandler() throws Exception {
        throw new Exception("This class could only be used with the constructor "
                + "NoInputCallbackHandler(String username, String password)");
    }

    /**
     * Constructor.
     *
     * @param username username to store for the authentication
     * @param password password to store for the authentication
     */
    public NoInputCallbackHandler(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor.
     *
     * @param username username to store for the authentication
     * @param password password to store for the authentication
     * @param cert     the certificate for the authentication
     */
    public NoInputCallbackHandler(final String username,
                                  final String password,
                                  final Certificate cert) {
        this(username, password);
        this.cert = cert;
    }

    /**
     * Invoke an array of Callbacks.
     *
     * @param callbacks an array of <code>Callback</code> objects which
     *                  contain the information requested by an underlying security
     *                  service to be retrieved or displayed.
     * @throws java.io.IOException if an input or output error occurs. <p>
     * @throws javax.security.auth.callback.UnsupportedCallbackException
     *                             if the implementation of this method
     *                             does not support one or more of the Callbacks specified in the
     *                             <code>callbacks</code> parameter.
     */
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                // set the username to the username given in the constructor
                NameCallback nc = (NameCallback) callbacks[i];
                nc.setName(username);
            } else if (callbacks[i] instanceof PasswordCallback) {
                // set the password to the password given in the constructor
                PasswordCallback pc = (PasswordCallback) callbacks[i];
                pc.setPassword(password.toCharArray());
            } else if (callbacks[i] instanceof CertificateCallback) {
                CertificateCallback cc = (CertificateCallback) callbacks[i];
                cc.setUserCertificate(cert);
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
    }
}
