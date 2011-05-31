package org.ow2.kerneos.service;

import org.ow2.kerneos.service.KerneosLogin;

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
