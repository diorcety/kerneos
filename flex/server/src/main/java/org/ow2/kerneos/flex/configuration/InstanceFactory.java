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

package org.ow2.kerneos.flex.configuration;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;


import org.osgi.framework.ServiceReference;

import org.ow2.kerneos.common.service.KerneosModule;
import org.ow2.kerneos.core.service.KerneosAsynchronousService;
import org.ow2.kerneos.core.service.KerneosFactoryService;
import org.ow2.kerneos.core.service.KerneosSimpleService;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.*;

/**
 * This is the component which handles the arrival/departure of the Kerneos services.
 */
@Component
@Instantiate
public final class InstanceFactory {

    private static Log LOGGER = LogFactory.getLog(InstanceFactory.class);

    private Map<Object, ComponentInstance> instanceMap = new Hashtable<Object, ComponentInstance>();

    @Requires(filter = "(factory.name=org.ow2.kerneos.flex.configuration.AsynchronousInstance)")
    private Factory asynchronousFactory;

    @Requires(filter = "(factory.name=org.ow2.kerneos.flex.configuration.FactoryInstance)")
    private Factory factoryFactory;

    @Requires(filter = "(factory.name=org.ow2.kerneos.flex.configuration.SimpleInstance)")
    private Factory simpleFactory;

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private synchronized void start() {
        LOGGER.debug("Start Factory");
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private synchronized void stop() {
        LOGGER.debug("Stop Factory");

        for (ComponentInstance cfg : instanceMap.values()) {
            cfg.dispose();
        }
        instanceMap.clear();
    }

    /**
     * Called when a new Kerneos Simple Service is registered.
     *
     * @param service the instance of the service
     */
    @Bind(aggregate = true, optional = true)
    private synchronized void bindSimple(final KerneosSimpleService service, final ServiceReference reference) {
        try {
            Dictionary properties = new Hashtable();
            Dictionary filters = new Hashtable();
            filters.put("kerneosModule", "(" + KerneosModule.BUNDLE + "=" + reference.getBundle().getBundleId() + ")");
            properties.put("requires.filters", filters);
            properties.put("serviceReference", reference);

            instanceMap.put(service, simpleFactory.createComponentInstance(properties));
        } catch (Exception ie) {
            LOGGER.error("Can't create configuration for service: " + service);
        }
    }


    /**
     * Called when a Kerneos Simple Service isn't registered anymore.
     *
     * @param service the instance of the service
     */
    @Unbind
    private synchronized void unbindSimple(final KerneosSimpleService service, final ServiceReference reference) {
        ComponentInstance cfg = instanceMap.remove(service);
        if (cfg != null) {
            cfg.dispose();
        } else {
            LOGGER.error("Can't find instance associated: " + service);
        }
    }

    /**
     * Called when a Kerneos Factory Service is registered.
     *
     * @param service the instance of the service
     */
    @Bind(aggregate = true, optional = true)
    private synchronized void bindFactory(final KerneosFactoryService service, final ServiceReference reference) {
        try {
            Dictionary properties = new Hashtable();
            Dictionary filters = new Hashtable();
            filters.put("kerneosModule", "(" + KerneosModule.BUNDLE + "=" + reference.getBundle().getBundleId() + ")");
            properties.put("requires.filters", filters);
            properties.put("serviceReference", reference);

            instanceMap.put(service, factoryFactory.createComponentInstance(properties));
        } catch (Exception ie) {
            LOGGER.error("Can't create configuration for service: " + service);
        }
    }


    /**
     * Called when a Kerneos Factory Service isn't registered anymore.
     *
     * @param service the instance of the service
     */
    @Unbind
    private synchronized void unbindFactory(final KerneosFactoryService service, final ServiceReference reference) {
        ComponentInstance cfg = instanceMap.remove(service);
        if (cfg != null) {
            cfg.dispose();
        } else {
            LOGGER.error("Can't find instance associated: " + service);
        }
    }


    /**
     * Called when a Kerneos Asynchronous Service is registered.
     *
     * @param service the instance of the service
     */
    @Bind(aggregate = true, optional = true)
    private synchronized void bindAsynchronous(final KerneosAsynchronousService service, final ServiceReference reference) {
        try {
            Dictionary properties = new Hashtable();
            Dictionary filters = new Hashtable();
            filters.put("kerneosModule", "(Bundle=" + reference.getBundle().getBundleId() + ")");
            properties.put("requires.filters", filters);
            properties.put("serviceReference", reference);

            instanceMap.put(service, asynchronousFactory.createComponentInstance(properties));
        } catch (Exception ie) {
            LOGGER.error("Can't create configuration for service: " + service);
        }
    }

    /**
     * Called when a Kerneos Asynchronous Service isn't registered anymore.
     *
     * @param service the instance of the service
     */
    @Unbind
    private synchronized void unbindAsynchronous(final KerneosAsynchronousService service, final ServiceReference reference) {
        ComponentInstance cfg = instanceMap.remove(service);
        if (cfg != null) {
            cfg.dispose();
        } else {
            LOGGER.error("Can't find instance associated: " + service);
        }
    }

}
