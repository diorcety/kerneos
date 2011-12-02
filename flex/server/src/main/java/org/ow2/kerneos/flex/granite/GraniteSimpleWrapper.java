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

package org.ow2.kerneos.flex.granite;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;

import org.granite.osgi.service.GraniteFactory;

import org.ow2.kerneos.core.service.KerneosSimpleService;


/**
 * Used for interfacing Granite and Kerneos services.
 */
@Component
@Provides
public class GraniteSimpleWrapper implements GraniteFactory {
    public final static String ID = "ID";
    public final static String SERVICE = "SERVICE";

    @Property(name = SERVICE)
    private KerneosSimpleService service;

    @Property(name = ID)
    private String id;

    /**
     * Get the GraniteFactory's id.
     *
     * @return the GraniteFactory's id.
     */
    public String getId() {
        return id;
    }

    /**
     * Called when GraniteDS ask for a new instance of the service provided by this factory.
     *
     * @return the new instance of the service.
     */
    public Object newInstance() {
        return service;
    }
}
