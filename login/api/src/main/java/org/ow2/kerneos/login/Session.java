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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The Object used to store the session information.
 */
public final class Session {
    private String username = null;
    private Collection<String> roles = null;
    private Map<String, Object> properties = new HashMap<String, Object>();

    public Session() {
    }

    public void reset() {
        username = null;
        roles = null;
        properties.clear();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLogged() {
        return username != null && roles != null;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

    public Object getProperty(String property) {
        return properties.get(property);
    }

    public void setProperty(String property, Object value) {
        properties.put(property, value);
    }

    public void removeProperty(String property) {
        properties.remove(property);
    }

}
