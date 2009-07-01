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
 * $Id:Controller.as 2485 2008-09-30 14:14:35Z renaultgu $
 * --------------------------------------------------------------------------
 */
package org.ow2.jasmine.kerneos.core.view
{
import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.LinkButton;
import mx.controls.Text;
import mx.controls.listClasses.IListItemRenderer;
import mx.events.FlexEvent;

/**
* A thumbnail displayed on the home view representing a module
* 
* @author Julien Nicoulaud
*/
public class ModuleThumb extends HBox
                         implements IListItemRenderer {

    
    // =========================================================================
    // Variables
    // =========================================================================
    
    // The item renderer stored data
    
    /**
    * The data displayed by the renderer
    */
	private var _data:Object;
	
    /**
    * The module load state
    */
    private var moduleLoadState:String = "not_loaded";
    
    
    // GUI components
    
    /**
    * The module name
    */
	private var moduleName:LinkButton;

    /**
    * The module description text
    */
    private var moduleDescription:Text;
    
	/**
	* The container for the module informations
	*/ 
	private var moduleInfoVBox:VBox;
	
    /**
    * The module thumbnail
    */
    private var moduleThumbnail:SmoothImage;
    		
    
    // Assets
    
    /**
    * The default module thumbnail
    */
    [Embed(source="/../assets/module.png")]
    [Bindable]
    private var defaultModuleThumbnail:Class;
    
    
    // =========================================================================
    // Constructors
    // =========================================================================
    
    /**
    * Build a new ModuleThumb item renderer
    */
    public function ModuleThumb()
    {
        super();
    }


    // =========================================================================
    // Data object handling
    // =========================================================================
    
    /**
    * Get the item renderer data object
    */
	[Bindable("dataChange")]
	override public function get data():Object
	{
	    return _data;
	}
	
	/**
	* Set the item renderer data object
	*/
	override public function set data(value:Object):void
	{
	    _data = value;
	    invalidateProperties();
	    dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
	}
	
	
    // =========================================================================
    // UIComponent overrides
    // =========================================================================
	
	/**
	* Create the component children
	*/
	override protected function createChildren() : void
	{ 
         // Call super class method 
	     super.createChildren();
	     
	     moduleThumbnail = new SmoothImage();
	     //moduleThumbnail.visible = true;
	     
	     moduleInfoVBox = new VBox();
	     moduleInfoVBox.visible = true;
	     
	     moduleName = new LinkButton();
	     moduleName.visible = true;
	     
	     moduleDescription = new Text();
	     moduleDescription.visible = true;
	     
	     // Add childs to the module info container
	     moduleInfoVBox.addChild(moduleName);
	     moduleInfoVBox.addChild(moduleDescription);
	     
	     // Add childs to the renderer
	     this.addChild(moduleThumbnail);
	     this.addChild(moduleInfoVBox);
	}
	
	/**
	* Commit the component and its children properties
	*/
	override protected function commitProperties():void
	{
	     // Call super class method 
	     super.commitProperties();
	     
	     // Adjust some component properties
	     this.height = 140;
	     this.width = 300;
	     
	     // Retrieve the module load state
	     // TODO add a "loading" state
         switch(_data.load as Boolean) {
            case true:
         	  moduleLoadState = "loaded";
         	  break;
 	        case false:
 	          moduleLoadState = "not_loaded";
 	          break;
         }
         
         // Module thumbnail properties
	     moduleThumbnail.data = defaultModuleThumbnail; // FIXME set the real thumbnail
	     moduleThumbnail.height = 128;
	     moduleThumbnail.width = 128;
	     moduleThumbnail.visible = true;
	     
	     // Module name properties
	     moduleName.label = _data.name;
	     moduleName.visible = true;
	     moduleName.percentWidth = 100;
         moduleName.toolTip = "Launch the module " + _data.name;
	     if(moduleLoadState == "not_loaded") {
            moduleName.addEventListener(MouseEvent.CLICK,startModule);
	     }
         moduleName.setStyle("textAlign","center");
         moduleName.setStyle("fontWeight","bold");
         		     
	     // Module description properties
	     moduleDescription.text = _data.description;
	     moduleDescription.visible = true;
	     moduleDescription.percentWidth = 100;
	     moduleDescription.selectable = false;
         moduleDescription.setStyle("textAlign","left");

	     // Module information container properties
	     moduleInfoVBox.percentHeight = 100;
         moduleInfoVBox.percentWidth = 100;
         moduleInfoVBox.setStyle("horizontalAlign","left");
         moduleInfoVBox.setStyle("verticalAlign","middle");
	     moduleInfoVBox.visible = true;
	}


    // =========================================================================
    // Private methods
    // =========================================================================
    
    /**
    * Start the module
    */
    private function startModule(e:Event):void {
        parentDocument.startModule(_data);
        moduleName.removeEventListener(MouseEvent.CLICK,startModule);
    }
}
}