package org.ow2.jasmine.kerneos.service.impl;

import org.granite.osgi.service.GraniteFactory;

import org.ow2.jasmine.kerneos.service.KerneosService;


public class GraniteKerneosService implements GraniteFactory {

    KerneosService service;

    GraniteKerneosService(KerneosService service) {
        this.service = service;
    }

    public String getId() {
        return service.getId() + KerneosConstants.FACTORY_SUFFIX;
    }

    public Object newInstance() {
        return service;
    }
}
