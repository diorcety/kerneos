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

package org.ow2.jasmine.kerneos.service.impl;

import org.granite.osgi.service.GraniteFactory;

import org.ow2.jasmine.kerneos.service.KerneosFactoryService;


/**
 * Used for interfacing Granite and Kerneos factories.
 */
public class GraniteKerneosFactory implements GraniteFactory {

    /**
     * The corresponding Kerneos factory.
     */
    private final KerneosFactoryService service;
    /**
     * The id of the GraniteFactory.
     */
    private final String id;

    /**
     * Create a interface between KerneosFactory and GraniteFactory.
     *
     * @param service the service to interface.
     * @param id      the id of the GraniteFactory.
     */
    GraniteKerneosFactory(final KerneosFactoryService service, final String id) {
        this.service = service;
        this.id = id;
    }

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
        return service.newInstance();
    }
}
