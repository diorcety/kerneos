
package org.ow2.kerneos.modules;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.kerneos.core.service.KerneosService;
import org.ow2.kerneos.core.service.KerneosSimpleService;
import org.ow2.kerneos.modules.impl.Store;
import org.ow2.kerneosstore.api.StoreInfo;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * HelloService
 */
//ipojo annotations Component, Instantiate and Provides
@Component
@Instantiate
@Provides
/*
 * Annotation that defines a KerneosService with the
 * "HelloService" destination.
 * It is the same destination defined in kerneos-module.xml
 */
@KerneosService(id = "hello_service")
public class Service implements KerneosSimpleService{

     /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(Service.class);

    /**
     * Start
     */
    @Validate
    private void start() {
        logger.info("Start HelloService");

        logger.info("Calling Store REST API");

        IStore store = new Store();
        StoreInfo result = store.getInfo();

        logger.info("Store Name : " + result.getName());
        logger.info("Store Description : " + result.getDescription());
        logger.info("Store Url : " + result.getUrl());
    }

    /**
     * Stop
     */
    @Invalidate
    private void stop() {
        logger.info("Stop HelloService");
    }

    /**
     * @param name the name of the user
     * @return a welcome message
     */
    public String sayHello(final String name) {
        IStore store = new Store();
        StoreInfo result = store.getInfo();

        return result.getName();
    }

    /**
     *
     */
    public StoreInfo getStoreInfo() {
        IStore store = new Store();
        StoreInfo result = store.getInfo();
        return result;
    }
}
