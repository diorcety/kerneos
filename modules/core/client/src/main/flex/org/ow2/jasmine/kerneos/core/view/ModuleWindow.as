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

import flexlib.mdi.containers.MDIWindow;
import flexlib.mdi.events.MDIWindowEvent;

import mx.controls.Alert;
import mx.events.FlexEvent;

import org.ow2.jasmine.kerneos.core.vo.ModuleVO;

/**
* A window containing a module
* 
* @author Julien Nicoulaud
*/
[Bindable]
public class ModuleWindow extends MDIWindow
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
	
	/**
	* The header icon
	*/
    private var headerIcon : Class;
    
    	
    // =========================================================================
    // Constructor
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
		this.titleIcon = headerIcon;
		this.title = module.name;
		
		// Listen to window events
		this.addEventListener(FlexEvent.CREATION_COMPLETE,onCreationComplete);
        this.addEventListener(MDIWindowEvent.MAXIMIZE,onMaximize);
        this.addEventListener(MDIWindowEvent.MINIMIZE,onUnMaximize);
        this.addEventListener(MDIWindowEvent.RESTORE,onUnMaximize);
	}
	
    /**
    * 
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
    	this.visible = false;
    	this.includeInLayout = false;
    }

    /**
    * Override the default unminimize behaviour
    */
    override public function unMinimize(event:MouseEvent=null):void
    {
    	// Restore the window visibility
        this.showControls = true;
        this.visible = true;
        this.includeInLayout = true;
        
        // Call super class method
        super.unMinimize(event);
    }
    
    /**
    * Bring the window
    */
    public function bringToFront(e:Event=null):void
    {
        // Bring the window to front
        if (!minimized) {
            if (hasFocus) {
                //_moduleWindow.minimize();
            } else {
                windowManager.bringToFront(this);       
            }
        } else {
            // If the window is minimized, restore it
            if(minimized) {
                unMinimize();
            }
            // Bring it to front
            windowManager.bringToFront(this);       
        }
    }
    
    
    // =========================================================================
    // Window events handling
    // =========================================================================
    
    /**
    * When the window is maximized
    */
    public function onMaximize(e:Event=null):void
    {
        // Disable rounded corners
        this.setStyle("cornerRadius",0);
        this.setStyle("dropShadowEnabled","false");
    }
    
    /**
    * When the window is maximized
    */
    public function onUnMaximize(e:Event=null):void
    {
        // Disable rounded corners
        this.setStyle("cornerRadius",3);
        this.setStyle("dropShadowEnabled","true");
    }
}
}