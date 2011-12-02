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

package org.ow2.kerneos.examples.modules.module2;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.kerneos.core.service.KerneosAsynchronousService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

// iPOJO annotations Component, Instantiate and Provides
@Component
@Instantiate
@Provides
public class AsyncService implements KerneosAsynchronousService {
    /**
     * The logger
     */
    private static Log LOGGER = LogFactory.getLog(AsyncService.class);

    @ServiceProperty(name = KerneosAsynchronousService.ID, value = "module2-serviceAsync")
    private String id;

    @ServiceProperty(name = KerneosAsynchronousService.TYPE, value = Type.EVENTADMIN)
    private String type;

    @ServiceProperty(name = "prefix", value = "kerneos-module2/")
    private String prefix;

    /**
     * Start
     */
    @Validate
    private void start() {
        LOGGER.info("Start Module2 - ServiceSync");
    }

    /**
     * Stop
     */
    @Invalidate
    private void stop() {
        LOGGER.info("Stop Module2 - ServiceSync");
    }
}
