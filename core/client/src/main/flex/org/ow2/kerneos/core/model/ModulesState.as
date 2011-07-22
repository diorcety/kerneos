/**
 * Kerneos
 * Copyright (C) 2011 Bull S.A.S.
 * Contact: jasmine AT ow2.org
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
 * $Id$
 */
package org.ow2.kerneos.core.model {

/**
 * An enumerated type representing the states of Kerneos.
 *
 * @author Julien Nicoulaud
 */
[Bindable]
public class ModulesState {

    /**
     * Initial state.
     */
    public static var IDLE:String = "idle";

    /**
     * When load the modules.
     */
    public static var LOAD:String = "load";

    /**
     * When unload the modules.
     */
    public static var UNLOAD:String = "unload";

    /**
     * When the modules are loaded.
     */
    public static var LOADED:String = "loaded";

    /**
     * Constructor
     */
    function ModulesState() {

    }
}
}
