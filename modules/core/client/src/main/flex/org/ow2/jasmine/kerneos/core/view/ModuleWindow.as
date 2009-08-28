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
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.jasmine.kerneos.core.view
{
import flash.events.Event;
import flash.events.MouseEvent;

import flexlib.mdi.events.MDIWindowEvent;

import mx.events.FlexEvent;

import org.ow2.jasmine.kerneos.core.vo.ModuleVO;

/**
* A window containing a module
* 
* @author Julien Nicoulaud
*/
[Bindable]
public class ModuleWindow extends KerneosWindow
{
	
    // =========================================================================
    // Variables
    // =========================================================================
    
    /**
    * The module
    */
	public var module:ModuleVO = null;
	
    /**
    * The minimized module item
    */
    public var minimizedModuleWindow:MinimizedModuleWindow;
    	
	
    // =========================================================================
    // Constructor & initialization
    // =========================================================================
	
	/**
	* Builds a new window for a module
	*/
	public function ModuleWindow(module:ModuleVO)
	{
	    // Call super class constructor
		super();
		
		// Assign some properties
		this.module = module;
		this.title = module.name;
		
		// Listen to window events
		this.addEventListener(FlexEvent.CREATION_COMPLETE,onCreationComplete);
	}
	
    /**
    * On creation complete
    */
    private function onCreationComplete(e:Event=null):void
    {
        this.titleIcon = IconUtility.getClass(this.titleBar,module.smallIcon,16,16);
    }


    // =========================================================================
    // Window operations
    // =========================================================================
    
    /**
    * Override the default minimize behaviour
    */
    override public function minimize(event:MouseEvent = null):void
    {
    	// Call super class method
    	super.minimize(event);
    	
    	// Hide the minimized window
        this.showControls = false;
    	this.includeInLayout = false;
    }

    /**
    * Override the default unminimize behaviour
    */
    override public function unMinimize(event:MouseEvent=null):void
    {
    	// Restore the window visibility
        this.showControls = true;
        this.includeInLayout = true;
        
        // Call super class method
        super.unMinimize(event);
    }

}
}
