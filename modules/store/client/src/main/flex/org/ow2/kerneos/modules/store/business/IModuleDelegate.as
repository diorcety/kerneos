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
 * --------------------------------------------------------------------------
 */
package org.ow2.kerneos.modules.store.business {
import org.ow2.kerneos.common.business.IDelegateResponder;

/**
 * The interface of the delegate.
 */
public interface IModuleDelegate extends IDelegateResponder {
    ////////////////////////////////////////////////////////////////////
    //                                                                //
    //             Function that does the requested operation         //
    //                                                                //
    ////////////////////////////////////////////////////////////////////


    function getStore(parameters:Object):void;

    function getModule(parameters:Object):void;

    function getModuleImage(parameters:Object):void;

    function searchModules(filter:Object, field:Object, order:Object, itemByPage:Object, page:Object):void;

    function searchModulesWithImage(filter:Object, field:Object, order:Object, itemByPage:Object, page:Object):void;

    function searchModulesByCategory(id:Object, field:Object, order:Object, itemByPage:Object, page:Object):void;

    function searchModulesWithImageByCategory(id:Object, field:Object, order:Object, itemByPage:Object, page:Object):void;

    function installModule(parameters:Object):void;

    function uninstallModule(parameters:Object):void;

    function updateModule(parameters:Object):void;

    function getInstalledModules():void;

    function addStore(parameters:Object):void;

    function updateStore(parameters:Object):void;

    function deleteStore(parameters:Object):void;

    function getStores():void;

}
}
