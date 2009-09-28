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
package org.ow2.jasmine.kerneos.core.view.window
{
import flash.events.Event;
import flash.events.MouseEvent;

import flexlib.mdi.containers.MDIWindow;
import flexlib.mdi.events.MDIWindowEvent;

import mx.controls.Alert;
import mx.events.FlexEvent;

import org.ow2.jasmine.kerneos.core.vo.ModuleVO;

/**
* A window in Kerneos
* 
* @author Julien Nicoulaud
*/
[Bindable]
public class KerneosWindow extends MDIWindow
{

    // =========================================================================
    // Constructor
    // =========================================================================
	
	/**
	* Builds a new window
	*/
	public function KerneosWindow()
	{
	    // Call super class constructor
		super();
		
		// Listen to window events
        this.addEventListener(MDIWindowEvent.MAXIMIZE,onMaximize);
        this.addEventListener(MDIWindowEvent.MINIMIZE,onUnMaximize);
        this.addEventListener(MDIWindowEvent.RESTORE,onUnMaximize);
	}
	

    // =========================================================================
    // Window operations
    // =========================================================================
    
    /**
    * Bring the window to the front
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
