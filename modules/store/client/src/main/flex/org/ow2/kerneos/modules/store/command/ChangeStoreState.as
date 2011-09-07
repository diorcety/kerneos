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

import org.ow2.kerneos.modules.store.event.ChangeStoreStateEvent;

import org.ow2.kerneos.modules.store.model.ModuleModelLocator;

/**
 * The command class from the cairngorm model.
 */
public class ChangeStoreState implements ICommand {
    /**
     * Update the model variables
     */
    public function execute(event:CairngormEvent):void {
        ////////////////////////////////////////////////
        //                                            //
        //             Handle the execution           //
        //                                            //
        ////////////////////////////////////////////////

        ModuleModelLocator.getInstance().storeState = (event as ChangeStoreStateEvent).state;
        if ((event as ChangeStoreStateEvent).moduleDetail != null) {
            ModuleModelLocator.getInstance().mainModule = (event as ChangeStoreStateEvent).moduleDetail;
        }

    }

}
}
