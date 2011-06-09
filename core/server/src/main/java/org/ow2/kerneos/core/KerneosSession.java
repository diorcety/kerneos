package org.ow2.kerneos.core;

import java.util.Collection;

/**
 * The Object used to store the session information.
 */
public class KerneosSession {
    private String username = null;
    private Collection<String> roles = null;

    public KerneosSession() {
    }

    public KerneosSession(KerneosSession session) {
        this.username = session.username;
        this.roles = session.roles;
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
}
