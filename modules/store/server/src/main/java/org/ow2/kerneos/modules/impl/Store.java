package org.ow2.kerneos.modules.impl;

import org.ow2.kerneos.modules.IStore;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Julian RIVERA
 */
public class Store implements IStore {
    private String url = "http://localhost:9000/store";

    public org.ow2.kerneosstore.api.Store getInfo() {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/info");
        return webResource.get(org.ow2.kerneosstore.api.Store.class);
    }
}
