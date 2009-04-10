package org.ow2.jasmine.kerneos.core.command
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.ow2.jasmine.kerneos.core.business.IModuleDelegate;
	import org.ow2.jasmine.kerneos.core.model.ModuleModelLocator;
	
	public class ModuleCommand implements ICommand, IResponder{
	    /**
	    * Send the event to the java side, using the business layer of the pattern
	    */
	    public function execute( e:CairngormEvent ):void {
	    	try{
		    	var delegate:IModuleDelegate = ModuleModelLocator.getInstance().getModuleDelegate();
	        	delegate.responder = this;
	        	delegate.moduleSearch();
        	}catch(e:Error){
	     		trace("An error occurred: " + e.message);
	     	}
	    }
	
	    /**
	    * Get the result of the java side. this method is called on each event from
	    * Java.
	    */
	    public function result( event : Object ):void {
	    	try{
		        var model:ModuleModelLocator = ModuleModelLocator.getInstance();
		        var modulesNames:ArrayCollection = (event as ResultEvent).result as ArrayCollection;
		        model.listModules = modulesNames;
	     	}catch(e:Error){
	     		trace("An error occurred: " + e.message);
	     	}
	    }
	    
	    public function fault( event : Object ) : void {
        	var faultEvent : FaultEvent = FaultEvent( event );
        	Alert.show( "Error when get modules names : Unhandled error","Error" );
    	}
	}
}