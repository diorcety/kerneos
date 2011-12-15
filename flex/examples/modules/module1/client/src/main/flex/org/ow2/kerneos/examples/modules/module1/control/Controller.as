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

package org.ow2.kerneos.examples.modules.module1.control {
import com.adobe.cairngorm.control.FrontController;

import org.ow2.kerneos.examples.modules.module1.command.*;
import org.ow2.kerneos.examples.modules.module1.event.*;
import org.ow2.kerneos.examples.modules.module1.model.*;


/**
 * Bind a type of command to an action.
 */
public class Controller extends FrontController {

    /**
     * Create an instance of the controller. Commands are initialized here.
     */
    public function Controller() {
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

    public function initialiseCommands():void {
        var moduleModel:ModuleModelLocator = ModuleModelLocator.getInstance();

        this.addCommand(ModuleEvent.NORMAL, ModuleCommand);
        this.addCommand(ModuleEvent.ADMIN, ModuleCommand);
    }

    /**
     * Remove all the commands to the pool of commands
     *
     */
    public function removeCommands():void {
        this.removeCommand(ModuleEvent.NORMAL);
        this.removeCommand(ModuleEvent.ADMIN);
    }

}
}
