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
import mx.controls.Button;

/**
* A button representing a module window, for the taskbar
* 
* @author Julien Nicoulaud
*/
public class MinimizedModuleWindow extends Button
{
	
    // =========================================================================
    // Variables
    // =========================================================================
    
    /**
    * The corresponding module window
    */
	public var _moduleWindow:ModuleWindow;
	
	
    // =========================================================================
    // Constructors
    // =========================================================================
	
	/**
	* Builds a new window for a module
	*/
	public function MinimizedModuleWindow(window:ModuleWindow)
	{
		// Call super class constructor
		super();
		
		// Assign variables
		_moduleWindow = window;
		
		// FIXME move to commitProperties
		this.label = window.moduleName;
		this.setStyle("icon",window.titleIcon);
		this.doubleClickEnabled = true;
		
		// Intercept button click events
		this.addEventListener(MouseEvent.CLICK,simpleClickHandler);
        this.addEventListener(MouseEvent.DOUBLE_CLICK,doubleClickHandler);
        
        // Intercept window events
		_moduleWindow.addEventListener(MDIWindowEvent.MINIMIZE,windowMinimizeHandler);
        _moduleWindow.addEventListener(MDIWindowEvent.RESTORE,windowRestoreHandler);
        _moduleWindow.addEventListener(MDIWindowEvent.FOCUS_START,windowFocusStartHandler);
        _moduleWindow.addEventListener(MDIWindowEvent.FOCUS_END,windowFocusEndHandler);
	}
    
    
    // =========================================================================
    // Button click events handlers
    // =========================================================================
        
    /**
    * When the button is simple clicked
    */
    public function simpleClickHandler(e:MouseEvent):void
    {
    	// If the window has focus, minimize it
        if (!_moduleWindow.minimized) {
            if (_moduleWindow.hasFocus) {
                //_moduleWindow.minimize();
            } else {
                _moduleWindow.windowManager.bringToFront(_moduleWindow as MDIWindow);       
            }
        } else {
            // If the window is minimized, restore it
            if(_moduleWindow.minimized) {
                _moduleWindow.unMinimize();
            }
            // Bring it to front
            _moduleWindow.windowManager.bringToFront(_moduleWindow as MDIWindow);      	
        }
    }
    
    /**
    * When the button is double clicked
    */
    public function doubleClickHandler(e:MouseEvent):void
    {
        if (!_moduleWindow.maximized) {
            // Maximize it
            _moduleWindow.maximize();
        }
    }
    
    
    // =========================================================================
    // Window events handler
    // =========================================================================
                 
    /**
    * When the window is minimized
    */
    public function windowMinimizeHandler(e:Event):void
    {
        windowFocusEndHandler();
    }
    
    /**
    * When the window is restored
    */
    public function windowRestoreHandler(e:Event):void
    {
    }
    
    /**
    * When the window gets the focus
    */
    public function windowFocusStartHandler(e:Event):void
    {
        this.setStyle("borderColor", "#454545");
        this.setStyle("fillColors", [0x000000, 0x000000, 0xffffff, 0xeeeeee]);

    }
    
    /**
    * When the window loses the focus
    */
    public function windowFocusEndHandler(e:Event=null):void
    {
        this.setStyle("borderColor", "#BBBBBB");
        this.setStyle("fillColors", [0x999999, 0x454545, 0xffffff, 0xeeeeee]);
    }      
}
}