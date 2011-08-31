/**
 * Kerneos
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
 * --------------------------------------------------------------------------
 */
package org.ow2.kerneos.modules.store.control
{
import com.adobe.cairngorm.control.FrontController;

import org.ow2.kerneos.modules.store.command.*;
import org.ow2.kerneos.modules.store.event.*;
import org.ow2.kerneos.modules.store.model.*;


/**
 * Bind a type of command to an action.
 */
public class Controller extends FrontController {

    /**
     * Create an instance of the controller. Commands are initialized here.
     */
    public function Controller()
    {
        initialiseCommands();
    }
    
    
    
    /**
     * Add all the commands to the pool of commands.
     *
     * An unique ID was added to all the events in order
     * to prevent a Cairngorm issue: when a command event is dispatched,
     * every controller that registered this event type receives it, even if
     * located in another module. To prevent this from happening and triggering
     * multiple severe unexpected concurrence bugs, each event dispatched is
     * postfixed with this unique ID.
     */

    public function initialiseCommands() : void
    {
        // Add the events to the controler with the associated command
        this.addCommand(ModuleEvent.GET_MODULE_INFO, ModuleCommand);
        this.addCommand(StoreEvent.GET_STORE, GetStoreInfo);
        this.addCommand(ChangeSelectedModuleInstallEvent.SELECTED_MODULE_CHANGE, ChangeSelectedModuleInstall);
        this.addCommand(ChangeStoreStateEvent.CHANGE_MODULE_STATE, ChangeStoreState);
        this.addCommand(GetModuleEvent.GET_MODULE, GetModule);
        this.addCommand(GetModuleEvent.GET_MODULE_IMAGE, GetModuleImage);
        this.addCommand(SearchModulesEvent.SEARCH_MODULES, SearchModules);
        this.addCommand(SearchModulesEvent.SEARCH_MODULES_BY_CATEGORY, SearchModulesByCategory);
    }

    /**
     * Remove all the commands to the pool of commands
     *
     */
    public function removeCommands() : void
    {
        // Remove the events to the controller with the associated command
        this.removeCommand(ModuleEvent.GET_MODULE_INFO);
        this.removeCommand(StoreEvent.GET_STORE);
        this.removeCommand(ChangeSelectedModuleInstallEvent.SELECTED_MODULE_CHANGE);
        this.removeCommand(ChangeStoreStateEvent.CHANGE_MODULE_STATE);
        this.removeCommand(GetModuleEvent.GET_MODULE);
        this.removeCommand(GetModuleEvent.GET_MODULE_IMAGE);
        this.removeCommand(SearchModulesEvent.SEARCH_MODULES);
        this.removeCommand(SearchModulesEvent.SEARCH_MODULES_BY_CATEGORY);
    }

}
}
