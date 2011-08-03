package org.ow2.kerneos.modules.impl;

import org.ow2.kerneos.modules.IStore;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.ow2.kerneosstore.api.StoreInfo;

/**
 * @author Julian RIVERA
 */
public class Store implements IStore{

   public StoreInfo getInfo() {
       Client client = new Client();

       WebResource webResource = client.resource("http://localhost:9000/store/info");
       return webResource.get(StoreInfo.class);
   }
}
