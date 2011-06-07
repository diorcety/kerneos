package org.ow2.kerneos.core.service;

import java.util.Collection;
import java.util.LinkedList;

public class DefaultKerneosLogin implements KerneosLogin {

    public boolean login(String application, String user, String password) {
        return true;
    }

    public Collection<String> getRoles() {
        return new LinkedList<String>();
    }

    public boolean logout() {
        return true;
    }

    public boolean isLogged() {
        return true;
    }
}
