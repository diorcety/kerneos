/**
 * JASMINe
 * Copyright (C) 2009-2011 Bull S.A.S.
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
 * $Id: ModulesVO.as 5743 2009-12-13 21:59:24Z nicoulaj $
 */
package org.ow2.kerneos.core.vo {
import org.ow2.kerneos.core.vo.ModuleInstanceVO;

/**
 * An event
 *
 * @author Julian Rivera
 */

[RemoteClass(alias="org.ow2.kerneos.core.ModuleEvent")]
[Bindable]
public class ModuleEventVO {

    /**
     * Module Object
     */
    public var moduleInstance:ModuleInstanceVO;

    /**
     * Module's event type (load or unload).
     */
    public var eventType:String;

    /**
     * Load value.
     */
    public static const LOAD:String = "load";

    /**
     * Unload value.
     */
    public static const UNLOAD:String = "unload";

    /**
     * Empty constructor
     */
    public function ModuleEventVO() {
    }
}
}
