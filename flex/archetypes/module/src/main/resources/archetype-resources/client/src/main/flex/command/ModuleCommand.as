#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.command
{
import com.adobe.cairngorm.commands.ICommand;
import com.adobe.cairngorm.control.CairngormEvent;
import com.adobe.cairngorm.control.CairngormEventDispatcher;

import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

/*// Server Exceptions imports
import org.ow2.kerneos.common.event.ServerSideExceptionEvent;
import org.ow2.kerneos.common.view.ServerSideException;
*/
import ${package}.business.*;
import ${package}.event.ModuleEvent;
import ${package}.model.ModuleModelLocator;

/**
  * The command class from the cairngorm model.
  */
[Event(name="serverSideException", type="org.ow2.kerneos.common.event.ServerSideExceptionEvent")]
public class ModuleCommand implements ICommand, IResponder
{
    /**
     * Retrieve the delegate and use it to make the call.
     */
    public function execute(event:CairngormEvent):void
    {
        ////////////////////////////////////////////////
        //                                            //
        //             Handle the execution           //
        //                                            //
        ////////////////////////////////////////////////
        
        /*
            // - Get the delegate
            // - Register the responder
            // - Make the call
            // Example :
                var delegate:IModuleDelegate = ModuleModelLocator.getInstance().getMyDelegate();       
                delegate.responder = this;
                var parameters : String = (event as ModuleEvent).getMessage();
                delegate.callServerSide(parameters);
        */
    }

    /**
     * Handle the result of the server call.
     */
    public function result(data:Object):void
    {
        ////////////////////////////////////////////////
        //                                            //
        //             Handle the result              //
        //                                            //
        ////////////////////////////////////////////////
        
        /*
            // Handle the result of the call. Usely, the model is updated.
            // Example :
                var moduleModel:ModuleModelLocator = ModuleModelLocator.getInstance();
                moduleModel.myDataObj = (data as ResultEvent).result as String;
            
        */
    }

    /**
     * Raise an alert when something is wrong.
     */
    public function fault(info:Object):void
    {
    
        ////////////////////////////////////////
        //                                    //
        //             Handle fault           //
        //                                    //
        ////////////////////////////////////////
        
        /*
            // The following code generates a formated panel that contains
            // the fault. However, librairies from jasmine-eos should be included
            // to get the common and util classes
            
            // Code :
            
                 // Retrieve the fault event
                var faultEvent : FaultEvent = FaultEvent(info);
        
                // Tell the view and let it handle this
                var serverSideExceptionEvent : ServerSideExceptionEvent =
                    new ServerSideExceptionEvent(
                        "serverSideException",
                        new ServerSideException("Error while Executing the action",
                                                "The operation could not be performed."
                                                + "\n" + faultEvent.fault.faultCode
                                                + "\n" + faultEvent.fault.faultString,
                                                faultEvent.fault.getStackTrace()));
                                                
                // Dispatch the event using the cairngorm event dispatcher
                CairngormEventDispatcher.getInstance().dispatchEvent(serverSideExceptionEvent);
             
        */
        
    }

}
}
