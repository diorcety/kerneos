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
import org.granite.config.flex.Destination;
import org.ow2.kerneos.core.ApplicationInstance;
import org.ow2.kerneos.core.ModuleInstance;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * Interface of the Kerneos Security Service.
 */
public interface IKerneosSecurityService {

    /**
     * The different status associated to the session.
     */
    enum SecurityError {
        NO_ERROR,
        SESSION_EXPIRED,
        INVALID_CREDENTIALS
    }

    /**
     * Have to be called for updating the KerneosContext according to the request.
     */
    public void updateContext(HttpServletRequest request, String destination);

    /**
     * Login to Kerneos.
     *
     * @param username The username used for login.
     * @param password The password used for login.
     * @return True if the login is successful.
     */
    public boolean logIn(String username, String password);

    /**
     * Check the authorisation associated to the request.
     *
     * @return The status associated to the authorisation.
     */
    public SecurityError authorize();

    /**
     * Logout of Kerneos.
     *
     * @return True if the logout is successful.
     */
    public boolean logOut();
}
