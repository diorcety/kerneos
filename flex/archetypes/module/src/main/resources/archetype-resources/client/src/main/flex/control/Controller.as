#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.control
{
import com.adobe.cairngorm.control.FrontController;
import com.adobe.cairngorm.control.CairngormEventDispatcher;

import ${package}.command.*;
import ${package}.event.*;
import ${package}.model.*;


/**
 * Bind a type of command to an action.
 */
public class Controller extends FrontController {

    /**
     * Create an instance of the controller. Commands are initialized here.
     */
    public function Controller(dispatcher: CairngormEventDispatcher) {
        super(dispatcher);
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
        // Add the events to the controller with the associated command
        // Example :
        this.addCommand(ModuleEvent.MY_ACTION, ModuleCommand);
    }

    /**
     * Remove all the commands to the pool of commands
     *
     */
    public function removeCommands() : void
    {
        // Remote the events from the controller
        // Example :
        this.removeCommand(ModuleEvent.MY_ACTION);
    }

}
}
