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
 * 
 */

package org.ow2.jasmine.kerneos.core.api{
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.collections.ArrayCollection;
	import mx.containers.Canvas;
	import mx.managers.DragManager;
	
	public class Core extends Canvas implements ICore{

		/**
		 * Constructor of Core
		 * Singleton
		 */
	    private static var _instance:Core;
	    private static var allowInstantiation:Boolean = true;

	    public function Core(){
         	 if(allowInstantiation){
         		allowInstantiation = false; 
         		_instance = this;
         	}else{
            	throw new Error("Error: Instantiation failed: Use Core.getInstance() instead of new.");
          	}
	    }
		
		public static function getInstance():ICore{
			if (!allowInstantiation) {
				return _instance;
			}else {
				return new Core();
			}
		}
		
	    private var dragManager:DragManager;

	    /**
	    * Format :
	    *   {id:<ID>,canvas:<Canvas>}
	    */
        [Bindable]
        private var modules:ArrayCollection = new ArrayCollection();
        
        /**
        * Add the module in the list of modules
        * 
        * Format :
        *   {module:<ModuleName>,name:<DisplayedName>,load:<State>,load:<LoadState>}
        */
		public function addModule(module:Object, name:String, status:Boolean ,description:String):void{
			modules.addItem({module:module,name:name,load:status,description:description});
		}
		
		
		/**
        * Format :
        *   {serviceId<the ident of Service>, destination<the Destination of Service>}
        */
		public function setService(serviceId:String, destination:String):void{
			var service:ServiceLocator = ServiceLocator.getInstance();
			service.setServiceForId(serviceId, destination);
		}
		
		/**
		 * Getter for Modules
		 */
		public function get modulesList():ArrayCollection{
			return modules;
		}
	}
}
