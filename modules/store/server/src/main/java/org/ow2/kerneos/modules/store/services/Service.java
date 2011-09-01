/**
 * Kerneos
 * Copyright (C) 2009-2011 Bull S.A.S.
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

package org.ow2.kerneos.modules.store.services;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.kerneos.core.service.KerneosService;
import org.ow2.kerneos.core.service.KerneosSimpleService;
import org.ow2.kerneos.modules.store.IStoreRS;
import org.ow2.kerneos.modules.store.impl.StoreImpl;
import org.ow2.kerneos.modules.store.impl.StoreRS;
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
@KerneosService(id = "hello_service", classes = StoreImpl.class)
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

        IStoreRS store = new StoreRS();
        StoreImpl result = (StoreImpl) store.getStore();

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
        IStoreRS store = new StoreRS();
        org.ow2.kerneosstore.api.Store result = store.getStore();

        return result.getName();
    }
}
