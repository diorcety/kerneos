package org.ow2.jasmine.kerneos.service.impl;

import org.granite.osgi.service.GraniteFactory;

import org.ow2.jasmine.kerneos.service.KerneosFactory;


public class GraniteKerneosFactory implements GraniteFactory {

    KerneosFactory service;

    GraniteKerneosFactory(KerneosFactory service) {
        this.service = service;
    }

    public String getId() {
        return service.getId();
    }

    public Object newInstance() {
        return service.newInstance();
    }
}
