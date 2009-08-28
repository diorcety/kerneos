/**
 * JASMINe
 * Copyright (C) 2009 Bull S.A.S.
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
package org.ow2.jasmine.kerneos.core.event
{
import com.adobe.cairngorm.control.CairngormEvent;

import flash.events.Event;

/**
* An event that holds the order to retrieve the Kerneos configuration file
* 
* @author Guillaume Renault, Julien Nicoulaud
*/
public class KerneosConfigEvent extends CairngormEvent {

    /**
    * Type of the event
    */
    public static var LOAD_KERNEOS_CONFIG:String = "loadKerneosConfig";

    /**
     * Create a new instance, and call the super.
     *
     * @param type String to put in the event.
     */
    public function KerneosConfigEvent(type:String) {
        super( type );
    }

    /**
     * Override the inherited clone() method, but don't return any state.
     *
     * @return this event
     */
    override public function clone() : Event {
        return new KerneosConfigEvent(this.type);
    }
}
}
