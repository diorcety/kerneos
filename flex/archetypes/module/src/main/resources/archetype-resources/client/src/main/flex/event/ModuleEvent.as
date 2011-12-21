#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.event
{
import com.adobe.cairngorm.control.CairngormEvent;
import flash.events.Event;


/**
 * A ModuleEvent is dispatched when the associated action is triggered from the view.
 */
public class ModuleEvent extends CairngormEvent
{
    
    ////////////////////////////////////
    //                                //
    //             Event Type         //
    //                                //
    ////////////////////////////////////
    public static var MY_ACTION : String = "My action to be done";

    /*
     // Example :
     // Message send in the event
     private var message: String = "";
     */

    /**
     * Creates a new ModuleEvent.
     */
    public function ModuleEvent(type : String)
    {
        super(type);
    }

    /*
     // Event message setting and getting
     // Example :
     public function setMessage(message:String):void 
     {
         this.message = message;
     }
     
     public function getMessage(): String
     {
         return message;
     }
     */

    /**
     * Overrides the clone function of the CairngormEvent class.
     * returns a new ModuleEvent
     */
    override public function clone() : Event
    {
        var ev:ModuleEvent = new ModuleEvent(this.type);
        /*
         // Example:
         // Add the event message
         ev.setMessage(this.message);
         */
        return ev;
    }

}
}
