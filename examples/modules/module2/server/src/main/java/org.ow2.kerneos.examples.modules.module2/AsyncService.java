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
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.kerneos.core.service.KerneosAsynchronous;
import org.ow2.kerneos.core.service.KerneosAsynchronousService;
import org.ow2.kerneos.core.service.KerneosService;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

// iPOJO annotations Component, Instantiate and Provides
@Component
@Instantiate
@Provides

// Don't forget to add all the classes used by the service but no used directly by this class
@KerneosService(id = "module2-serviceAsync", classes = Post.class)
@KerneosAsynchronous(type = KerneosAsynchronous.TYPE.EVENTADMIN, properties = {
        @KerneosAsynchronous.Property(name = "prefix", value = "kerneos-module2/")}
)
public class AsyncService implements KerneosAsynchronousService {
    /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(AsyncService.class);

    /**
     * Start
     */
    @Validate
    private void start() {
        logger.info("Start Module2 - ServiceSync");
    }

    /**
     * Stop
     */
    @Invalidate
    private void stop() {
        logger.info("Stop Module2 - ServiceSync");
    }
}
