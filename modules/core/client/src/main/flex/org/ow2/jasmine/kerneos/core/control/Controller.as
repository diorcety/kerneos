package org.ow2.jasmine.kerneos.core.control
{
import com.adobe.cairngorm.control.FrontController;

import org.ow2.jasmine.kerneos.core.command.ModuleCommand;
import org.ow2.jasmine.kerneos.core.event.ModulesEvent;
import mx.controls.Alert;

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
    * Add all of the commands to the pool of commands.
    */
    public function initialiseCommands() : void {
        addCommand( ModulesEvent.GET_MODULE, ModuleCommand);
    }
}
}