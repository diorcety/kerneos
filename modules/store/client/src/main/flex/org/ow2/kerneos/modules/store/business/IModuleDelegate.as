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


    function getStore(url:Object):void;

    function getModule(id:Object):void;

    function getModuleImage(id:Object):void;

    function searchModules(filter:Object, field:Object, order:Object, itemByPage:Object, page:Object):void;

    function searchModulesWithImage(filter:Object, field:Object, order:Object, itemByPage:Object, page:Object):void;

    function searchModulesByCategory(id:Object, field:Object, order:Object, itemByPage:Object, page:Object):void;

    function searchModulesWithImageByCategory(id:Object, field:Object, order:Object, itemByPage:Object, page:Object):void;

    function installModule(id:Object):void;

    function uninstallModule(id:Object):void;

    function updateModule(id:Object):void;

    function getInstalledModules():void;

    function addStore(store:Object):void;

    function updateStore(store:Object):void;

    function deleteStore(url:Object):void;

    function getStores():void;

}
}
