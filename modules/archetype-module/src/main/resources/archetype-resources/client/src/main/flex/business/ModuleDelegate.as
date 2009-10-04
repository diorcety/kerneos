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
 * --------------------------------------------------------------------------
 */
package business
{
import com.adobe.cairngorm.business.ServiceLocator;

import org.ow2.jasmine.kerneos.common.business.AbsDelegateResponder;

public class ModuleDelegate extends AbsDelegateResponder implements IModuleDelegate
{
   	////////////////////////////////////////////////////////////////////
	//  							 			  					  //
	//			 Function that does the requested operation			  //
	//								  			  					  //
	////////////////////////////////////////////////////////////////////
	
	
	/*
		Put here the method that will trigger the code to execute following a dispatched event
		in the cairngorm architecture.
		Example :
			public function callServerSide(parameters : Object) : void {
				// find service
        		var service : Object = ServiceLocator.getInstance().getRemoteObject("service");
        		
        		// Make the service call. The method called on service is the method name
        		// of the java class bound with the remote object, with its parameters.
        		var call : Object = service.sayHello(parameters);
        		
   		        // add responder to handle the callback
        		call.addResponder(this.responder);
			}
	*/


}
}
