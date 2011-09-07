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
package org.ow2.kerneos.modules.store.command {
import com.adobe.cairngorm.commands.ICommand;
import com.adobe.cairngorm.control.CairngormEvent;

import org.ow2.kerneos.modules.store.event.ChangeSelectedModuleInstallEvent;

import org.ow2.kerneos.modules.store.model.ModuleModelLocator;

/**
 * The command class from the cairngorm model.
 */
public class ChangeSelectedModuleInstall implements ICommand {
    /**
     * Retrieve the delegate and use it to make the call.
     */
    public function execute(event:CairngormEvent):void {
        ////////////////////////////////////////////////
        //                                            //
        //             Handle the execution           //
        //                                            //
        ////////////////////////////////////////////////
        var item:Object = (event as ChangeSelectedModuleInstallEvent).item;

        //If the item isn't into the list
        if (ModuleModelLocator.getInstance().listSelectedModules.getItemIndex(item) == -1) {
            //add the item
            ModuleModelLocator.getInstance().listSelectedModules.addItem(item);
        } else {
            //otherwise delete it
            ModuleModelLocator.getInstance().listSelectedModules.removeItemAt(
                    ModuleModelLocator.getInstance().listSelectedModules.getItemIndex(item));
        }
    }

}
}
