package org.ow2.kerneos.core.service;

import java.util.Collection;
import java.util.LinkedList;

public class DefaultKerneosLogin implements KerneosLogin {

    public Collection<String> login(String user, String password) {
        return new LinkedList<String>();
    }

    public boolean logout() {
        return true;
    }
}
