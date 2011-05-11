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

package org.ow2.jasmine.kerneos.examples.example1;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.jasmine.kerneos.service.KerneosFactory;
import org.ow2.jasmine.kerneos.service.KerneosFactoryService;
import org.ow2.jasmine.kerneos.service.KerneosService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;


@Component
@Instantiate
@Provides

@KerneosService(destination = "ObjectService")
@KerneosFactory(scope = KerneosFactory.SCOPE.SESSION)
public class Factory implements KerneosFactoryService<MyService> {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(Factory.class);

    /**
     *
     */
    @Validate
    private void start() {
        logger.info("Start Factory");
    }

    /**
     *
     */
    @Invalidate
    private void stop() {
        logger.info("Stop Factory");
    }

    /**
     *
     * @return a new instance of MyService
     */
    public MyService newInstance() {
        return new MyService();
    }
}
