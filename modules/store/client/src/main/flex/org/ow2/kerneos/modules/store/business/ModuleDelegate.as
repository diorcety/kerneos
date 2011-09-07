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
import com.adobe.cairngorm.business.ServiceLocator;

import org.ow2.kerneos.common.business.AbsDelegateResponder;

public class ModuleDelegate extends AbsDelegateResponder implements IModuleDelegate {
    ////////////////////////////////////////////////////////////////////
    //                                                                //
    //             Function that does the requested operation         //
    //                                                                //
    ////////////////////////////////////////////////////////////////////


    // Put here the method that will trigger the code to execute following a dispatched event
    // in the cairngorm architecture.

    public function getStore(parameters:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.getStore(parameters);
        call.addResponder(this.responder);
    }

    public function getModule(parameters:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.getModule(parameters);
        call.addResponder(this.responder);
    }

    public function getModuleImage(parameters:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.getModuleImage(parameters);
        call.addResponder(this.responder);
    }

    public function searchModules(filter:Object, field:Object, order:Object, itemByPage:Object, page:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.searchModules(filter, field, order, itemByPage, page);
        call.addResponder(this.responder);
    }

    public function searchModulesWithImage(filter:Object, field:Object, order:Object, itemByPage:Object, page:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.searchModulesWithImage(filter, field, order, itemByPage, page);
        call.addResponder(this.responder);
    }

    public function searchModulesByCategory(id:Object, field:Object, order:Object, itemByPage:Object, page:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.searchModulesByCategory(id, field, order, itemByPage, page);
        call.addResponder(this.responder);
    }

    public function searchModulesWithImageByCategory(id:Object, field:Object, order:Object, itemByPage:Object, page:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.searchModulesWithImageByCategory(id, field, order, itemByPage, page);
        call.addResponder(this.responder);
    }

    public function installModule(parameters:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.installModule(parameters);
        call.addResponder(this.responder);
    }

    public function getInstalledModules():void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.getInstalledModules();
        call.addResponder(this.responder);
    }

    public function uninstallModule(id:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.uninstallModule(id);
        call.addResponder(this.responder);
    }

    public function updateModule(id:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.updateModule(id);
        call.addResponder(this.responder);
    }

    public function addStore(parameters:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.addStore(parameters);
        call.addResponder(this.responder);
    }

    public function updateStore(parameters:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.updateStore(parameters);
        call.addResponder(this.responder);
    }

    public function deleteStore(parameters:Object):void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.deleteStore(parameters);
        call.addResponder(this.responder);
    }

    public function getStores():void {
        var service:Object = ServiceLocator.getInstance().getRemoteObject("store_service");
        var call:Object = service.getStores();
        call.addResponder(this.responder);
    }
}
}
