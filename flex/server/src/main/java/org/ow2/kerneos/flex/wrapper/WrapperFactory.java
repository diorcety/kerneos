/**
 * Kerneos
 * Copyright (C) 2011 Bull S.A.S.
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

package org.ow2.kerneos.flex.wrapper;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.kerneos.common.config.generated.Service;
import org.ow2.kerneos.common.config.generated.SwfModule;
import org.ow2.kerneos.common.service.KerneosModule;
import org.ow2.kerneos.core.service.KerneosAsynchronousService;
import org.ow2.kerneos.core.service.KerneosFactoryService;
import org.ow2.kerneos.core.service.KerneosSimpleService;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

/**
 * This is the component which handles the arrival/departure of the Kerneos services.
 */
@Component
@Instantiate
public final class WrapperFactory {

    private static Log LOGGER = LogFactory.getLog(WrapperFactory.class);

    private Map<String, ComponentInstance> instanceMap = new Hashtable<String, ComponentInstance>();

    @Requires(filter = "(factory.name=org.ow2.kerneos.flex.wrapper.AsynchronousServiceWrapper)")
    private Factory asynchronousFactory;

    @Requires(filter = "(factory.name=org.ow2.kerneos.flex.wrapper.FactoryServiceWrapper)")
    private Factory factoryFactory;

    @Requires(filter = "(factory.name=org.ow2.kerneos.flex.wrapper.SimpleServiceWrapper)")
    private Factory simpleFactory;

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private synchronized void start() {
        LOGGER.debug("Start WrapperFactory");
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private synchronized void stop() {
        LOGGER.debug("Stop WrapperFactory");

        for (ComponentInstance cfg : instanceMap.values()) {
            cfg.dispose();
        }
        instanceMap.clear();
    }

    /**
     * A Kerneos Module arrival
     *
     * @param module the module
     */
    @Bind(aggregate = true, optional = true)
    private synchronized void bindKerneosModule(KerneosModule module) {
        if (module.getConfiguration() instanceof SwfModule) {
            SwfModule configuration = (SwfModule) module.getConfiguration();
            for (Service service : configuration.getServices()) {
                if (instanceMap.containsKey(service.getId())) {
                    LOGGER.error("The Service \"" + service.getId() + "\" already exists");
                    continue;
                }
                try {
                    switch (service.getType()) {
                        case SIMPLE: {
                            Dictionary properties = new Hashtable();
                            Dictionary filters = new Hashtable();
                            filters.put(SimpleServiceWrapper.SERVICE, "(" + KerneosSimpleService.ID + "=" + service.getId() + ")");
                            properties.put("requires.filters", filters);
                            properties.put(SimpleServiceWrapper.CONFIGURATION, service);

                            instanceMap.put(service.getId(), simpleFactory.createComponentInstance(properties));
                            break;
                        }
                        case FACTORY: {
                            Dictionary properties = new Hashtable();
                            Dictionary filters = new Hashtable();
                            filters.put(FactoryServiceWrapper.SERVICE, "(" + KerneosFactoryService.ID + "=" + service.getId() + ")");
                            properties.put("requires.filters", filters);
                            properties.put(FactoryServiceWrapper.CONFIGURATION, service);

                            instanceMap.put(service.getId(), factoryFactory.createComponentInstance(properties));
                            break;

                        }
                        case ASYNCHRONOUS: {
                            Dictionary properties = new Hashtable();
                            Dictionary filters = new Hashtable();
                            filters.put(AsynchronousServiceWrapper.SERVICE, "(" + KerneosAsynchronousService.ID + "=" + service.getId() + ")");
                            properties.put("requires.filters", filters);
                            properties.put(AsynchronousServiceWrapper.CONFIGURATION, service);

                            instanceMap.put(service.getId(), asynchronousFactory.createComponentInstance(properties));
                        }
                        break;
                    }
                    LOGGER.debug("Wrapper created for service: " + service.getId());
                } catch (Exception ie) {
                    LOGGER.error("Can't create wrapper for service: " + service.getId());
                }
            }
        }
    }


    /**
     * A Kerneos Module departure
     *
     * @param module the module
     */
    @Unbind
    private synchronized void unbindKerneosModule(KerneosModule module) {
        if (module.getConfiguration() instanceof SwfModule) {
            SwfModule configuration = (SwfModule) module.getConfiguration();
            for (Service service : configuration.getServices()) {
                ComponentInstance instance = instanceMap.get(service.getId());
                if (instance != null) {
                    instance.dispose();
                    LOGGER.debug("Wrapper destroyed for service: " + service.getId());
                }
            }

        }
    }
}