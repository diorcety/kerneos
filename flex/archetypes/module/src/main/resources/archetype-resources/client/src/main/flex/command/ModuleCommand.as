#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.command
{
import com.adobe.cairngorm.commands.ICommand;
import com.adobe.cairngorm.control.CairngormEvent;

import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

import org.ow2.kerneos.common.event.ServerSideExceptionEvent;
import org.ow2.kerneos.common.managers.ErrorManager;
import org.ow2.kerneos.common.view.ServerSideException;

import ${package}.business.*;
import ${package}.event.ModuleEvent;
import ${package}.model.ModuleModelLocator;
import ${package}.MyModule
/**
  * The command class from the cairngorm model.
  */
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
        

        // - Get the delegate
        // - Register the responder
        // - Make the call
        // Example :
        var delegate:IModuleDelegate = MyModule.getInstance().getModel().getMyDelegate();
        delegate.responder = this;
        var parameters : String = (event as ModuleEvent).getMessage();
        delegate.callServerSide(parameters);
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
        

        // Handle the result of the call. Usely, the model is updated.
        // Example :
        var moduleModel:ModuleModelLocator = MyModule.getInstance().getModel()
        moduleModel.myDataObj = (data as ResultEvent).result as String;
    }

    /**
     * Raise an alert when something is wrong.
     */
    public function fault(event:Object):void {

        ////////////////////////////////////////
        //                                    //
        //             Handle fault           //
        //                                    //
        ////////////////////////////////////////
        if (!ErrorManager.handleError(event, ServerSideExceptionEvent.SERVER_SIDE_EXCEPTION, MyModule.getInstance().getDispatcher())) {
            // Retrieve the fault event
            var faultEvent:FaultEvent = FaultEvent(event);

            // Tell the view and let it handle this
            var serverSideExceptionEvent:ServerSideExceptionEvent =
                    new ServerSideExceptionEvent(
                            ServerSideExceptionEvent.SERVER_SIDE_EXCEPTION,
                            new ServerSideException("Error while Executing the action",
                                    "The operation could not be performed."
                                            + "\n" + faultEvent.fault.faultCode
                                            + "\n" + faultEvent.fault.faultString,
                                    faultEvent.fault.getStackTrace()));

            // Dispatch the event using the cairngorm event dispatcher
            MyModule.getInstance().getDispatcher().dispatchEvent(serverSideExceptionEvent);
        }
    }

}
}
