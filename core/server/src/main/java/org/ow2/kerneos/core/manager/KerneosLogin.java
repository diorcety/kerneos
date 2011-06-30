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

package org.ow2.kerneos.core.manager;

import org.ow2.kerneos.login.Session;

/**
 * Interface for providing a authentication service to Kerneos.
 */
public interface KerneosLogin {
    /**
     * Login.
     *
     * @param application The name of the application.
     * @param username    The username.
     * @param password    The password.
     */
    public void login(final String application, final String username, final String password);

    /**
     * Logout.
     *
     * @return
     */
    public void logout();

    /**
     * Called when a new session is needed.
     *
     * @return The new session
     */
    public Session newSession();
}
