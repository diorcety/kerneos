
package control
{
import com.adobe.cairngorm.control.FrontController;

import command.*;
import event.*;
import model.*;


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
        /*
        // Retrieve the component model
        var moduleModel : ModuleModelLocator = ModuleModelLocator.getInstance();

        // Add the events to the controler with the associated command
        // Example :
        this.addCommand(ModuleEvent.MY_ACTION, ModuleCommand);
        */
    }

    /**
     * Remove all the commands to the pool of commands
     *
     */
    public function removeCommands() : void
    {

        /*
            // Remove the events to the controller with the associated command
            // Example :

                this.removeCommand(ModuleEvent.MY_ACTION);
        */
    }

}
}
