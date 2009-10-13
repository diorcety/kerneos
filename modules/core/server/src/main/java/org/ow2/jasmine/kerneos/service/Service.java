/**
 * JASMINe
 * Copyright (C) 2008 Bull S.A.S.
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

package org.ow2.jasmine.kerneos.service;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Service description.
 *
 * @author Guillaume Renault
 * @author Julien Nicoulaud
 * @see SWFModule
 */
@XmlRootElement(name = "service", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
public class Service {

    /**
     * The id of the service.
     */
    @XmlAttribute
    public String id = null;

    /**
     * The destination of the service.
     */
    @XmlAttribute
    public String destination = null;


    /**
     * Output a String preview of the object.
     */
    @Override
    public String toString() {
        return "id: " + id + ", destination: " + destination;
    }
}
