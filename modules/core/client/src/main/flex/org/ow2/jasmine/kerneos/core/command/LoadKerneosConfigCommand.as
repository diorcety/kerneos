/**
 * JASMINe
 * Copyright (C) 2009 Bull S.A.S.
 * Contact: jasmine@ow2.org
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
 * $Id$
 */
package org.ow2.jasmine.kerneos.core.command
{
import com.adobe.cairngorm.commands.ICommand;
import com.adobe.cairngorm.control.CairngormEvent;

import mx.controls.Alert;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

import org.ow2.jasmine.kerneos.core.business.ILoadKerneosConfigDelegate;
import org.ow2.jasmine.kerneos.core.model.KerneosModelLocator;
import org.ow2.jasmine.kerneos.core.vo.KerneosConfigVO;

/**
* Load the Kerneos configuration
* 
* @author Guillaume Renault, Julien Nicoulaud
*/ 
public class LoadKerneosConfigCommand implements ICommand, IResponder{
    
    /**
    * Send the event to the java side, using the business layer of the pattern
    */
    public function execute( e:CairngormEvent ):void {
    	try {
	    	var delegate:ILoadKerneosConfigDelegate = KerneosModelLocator.getInstance().getLoadKerneosConfigDelegate();
        	delegate.responder = this;
        	delegate.loadKerneosConfig();
    	} catch(e:Error){
     		trace("An error occurred: " + e.message);
     	}
    }

    /**
    * Get the result of the java side. this method is called on each event from
    * Java.
    */
    public function result(event : Object):void {
    	try {
    	    // Retrieve the model
	        var model:KerneosModelLocator = KerneosModelLocator.getInstance();
	        
	        // Retrieve the result
	        var result:KerneosConfigVO = (event as ResultEvent).result as KerneosConfigVO;
	        
	        // Extract the data and update the model
	        model.config = result;
	        
     	} catch(e:Error){
     		trace("An error occurred: " + e.message);
     	}
    }
    
    /**
    * Handle faults
    */
    public function fault( event : Object ) : void {
    	var faultEvent : FaultEvent = FaultEvent( event );
    	Alert.show( "Error while loading Kerneos configuration file : Unhandled error","Error" );
	}
}
}
