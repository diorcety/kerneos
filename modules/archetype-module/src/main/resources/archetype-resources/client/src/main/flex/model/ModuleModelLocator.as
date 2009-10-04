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
package model
{

import com.adobe.cairngorm.model.ModelLocator;

 /**
  * The model locator for the Module.
  */
[Bindable]
public class ModuleModelLocator implements ModelLocator
{

	////////////////////////////////////
	//  							  //
	//			 Variables			  //
	//								  //
	////////////////////////////////////

  /**
    * The unique ID of this component
    *
    * @internal
    *   Used to prevent a Cairngorm issue: when a command event is dispatched,
    * every controller that registered this event type receives it, even if
    * located in another module. To prevent this from happening and triggering
    * multiple severe unexpected concurrence bugs, each event dispatched is
    * postfixed with this unique ID.
    */
    public var componentID:String = UIDUtil.createUID();


    /**
    * Unique instance of this locator.
    */
    private static var model : ModuleModelLocator = null;
    
	////////////////////////////////////////////
	//  							          //
	//			 Variables Delegate			  //
	//								          //
	////////////////////////////////////////////
	/*
	Put here the delegate instances of your model.
	Example :
		private var myDelegate : IMyDelegate = null;
	*/

	////////////////////////////////////////////////
	//  							          	  //
	//			 Variables Of the model			  //
	//								          	  //
	////////////////////////////////////////////////
	
	/*
	Put here all the variables of the model
	Example :
		private var myDataObj : String = null;
	*/
    
	////////////////////////////////////
	//  							  //
	//			 Functions			  //
	//								  //
	////////////////////////////////////

    public function ModuleModelLocator()
    {
        super();
        
        if (model != null)
        {
            throw new Error("Only one ModelLocator has to be set");
        }
    }
    
    
    
    public static function getInstance() : ModuleModelLocator
    {
        if (ModuleModelLocator.model == null)
        {
            ModuleModelLocator.model = new ModuleModelLocator();
        }
        
        return ModuleModelLocator.model;
    }

   	////////////////////////////////////
	//  							  //
	//			 Setters			  //
	//								  //
	////////////////////////////////////

	/*
	Put here all the setters for the model update.
	Example :
		public function set myData(_myData : String) : void {
			this.myDataObj = _myData;
		}
	*/

	////////////////////////////////////
	//  							  //
	//			 Getters			  //
	//								  //
	////////////////////////////////////
	
	/*
	Put here all the getters to access the model variables
	Example :
		public function get myData() : String {
			return this.myDataObj;
		}
	*/


	////////////////////////////////////////////
	//  							 		  //
	//  	      Delegate Getters			  //
	//								  		  //
	////////////////////////////////////////////

	/*
	Put here the getters to access all the delegates of the created module
	Example :
	    public function getMyDelegate() : IMyDelegate {
	        if (this.myDelegate == null) {
	            this.myDelegate = new MyDelegate();
	        }
	        return this.myDelegate;
	    }
	
	*/

}
}
