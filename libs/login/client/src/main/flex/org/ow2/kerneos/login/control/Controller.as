/**
 * Kerneos
 * Copyright (C) 2008 Bull S.A.S.
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
 * $Id:Controller.as 2485 2008-09-30 14:14:35Z renaultgu $
 * --------------------------------------------------------------------------
 */

package org.ow2.kerneos.login.control
{
import com.adobe.cairngorm.control.FrontController;

import org.ow2.kerneos.login.command.LogInCommand;
import org.ow2.kerneos.login.command.LogOutCommand;
import org.ow2.kerneos.login.event.LogInEvent;
import org.ow2.kerneos.login.event.LogOutEvent;

/**
 * Bind a type of command to an action.
 * @author Guillaume Renault
 */
public class Controller extends FrontController {

    /**
    * Create an instance of the controller. Commands are initialized here.
    */
    public function Controller() {
        initialiseCommands();
    }

    /**
    * Add all of the commands to the pool of commands.
    */
    public function initialiseCommands() : void {
        addCommand( LogInEvent.LOG_IN, LogInCommand);
        addCommand(LogOutEvent.LOG_OUT, LogOutCommand);
    }
}
}
