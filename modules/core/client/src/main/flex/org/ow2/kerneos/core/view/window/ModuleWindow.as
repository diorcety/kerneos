/**
 * Kerneos
 * Copyright (C) 2009 Bull S.A.S.
 * Contact: jasmine AT ow2.org
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
package org.ow2.kerneos.core.view.window
{
import flash.events.Event;
import flash.events.MouseEvent;

import flexlib.mdi.containers.MDICanvas;

import mx.events.FlexEvent;

import org.ow2.kerneos.core.managers.SharedObjectManager;
import org.ow2.kerneos.core.vo.ModuleWithWindowVO;


/**
 * A window containing a module.
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
    public var module : ModuleWithWindowVO = null;
    
    /**
     * The minimized module item
     */
    public var minimizedModuleWindow : MinimizedModuleWindow;
    
    
    
    // =========================================================================
    // Constructor & initialization
    // =========================================================================
    
    /**
     * Builds a new window for a module.
     */
    public function ModuleWindow(module : ModuleWithWindowVO)
    {
        // Call super class constructor
        super();
        
        // Assign some properties
        this.module = module;
        this.title = module.name;
        this.resizable = module.resizable;
        
        // Listen to window events
        this.addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
    }
    
    
    
    /**
     * Create UI children
     */
    override protected function createChildren() : void
    {
        // Call super class method
        super.createChildren();
        
        // Setup the controls
        windowControls.maximizeRestoreBtn.visible = windowControls.maximizeRestoreBtn.includeInLayout = module.maximizable;
    }
    
    
    
    /**
     * On creation complete.
     */
    private function onCreationComplete(e : Event = null) : void
    {
        this.titleIcon = module.getSmallIcon(this.titleBar) as Class;
    }
    
    
    
    // =========================================================================
    // Window operations
    // =========================================================================
    
    /**
     * Override the default minimize behaviour
     */
    override public function minimize(event : MouseEvent = null) : void
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
    override public function unMinimize(event : MouseEvent = null) : void
    {
        // Restore the window visibility
        this.showControls = true;
        this.visible = true;
        this.includeInLayout = true;
        
        // Call super class method
        super.unMinimize(event);
    }



    // =========================================================================
    // Window size and placement
    // =========================================================================
    
    /**
     * Setup the window size and position.
     *
     * Tries to read the user preferences from the shared object. If valid, applies it. Else
     * if settings defined in Kerneos config and valid, applies it. Else calculates the window
     * size and position.
     */
    override protected function setupWindowSizeAndPosition(event : Event = null) : void
    {
        // Try the user settings
        if (!setupUserWindowSizeAndPosition())
        {
            // Try the Kerneos config settings
            if (!setupKerneosConfigWindowSizeAndPosition())
            {
                // Else apply the default algorithm
                setupDefaultWindowSizeAndPosition();
            }
        }
        
        // Setup wether the window should be maximized
        setupWindowMaximization();
    }
    
    /**
    * Try to apply the user saved setting for positioning and sizing this window.
    * 
    * @return false
    *           if no setting saved or not applicable anymore.
    */
    protected function setupKerneosConfigWindowSizeAndPosition():Boolean
    {
        // Only apply it if both width and height are specified and reasonable
        if (isNaN(module.width) ||
            isNaN(module.height) ||
            module.width < MINIMUM_ACCEPTED_WINDOW_WIDTH ||
            module.height < MINIMUM_ACCEPTED_WINDOW_HEIGHT)
        {
            return false;
        }
        else
        {
            // Check that settings are still applicable
            if (module.width > (windowManager.container as MDICanvas).width || module.height > (windowManager.container as MDICanvas).height)
            {
                return false;
            }
            
            // Calculate the placement
            var xOffset : int = windowManager.windowList.length * WINDOW_DEFAULT_X_OFFSET;
            var xPos : int;
            if (LEFT_MARGIN + xOffset + module.width < (windowManager.container as MDICanvas).width)
            {
               xPos = Math.max(LEFT_MARGIN, (windowManager.container as MDICanvas).width - module.width - xOffset);
            }
            else if (LEFT_MARGIN + module.width < (windowManager.container as MDICanvas).width)
            {
                xPos = LEFT_MARGIN;
            }
            else if (xOffset + module.width < (windowManager.container as MDICanvas).width)
            {
                xPos = (windowManager.container as MDICanvas).width - module.width - xOffset;
            }
            else
            {
                xPos = (windowManager.container as MDICanvas).width - module.width;
            }

            var yOffset : int = windowManager.windowList.length * WINDOW_DEFAULT_Y_OFFSET;
            var yPos : int;
            if (TOP_MARGIN + yOffset + module.height < (windowManager.container as MDICanvas).height)
            {
               yPos = Math.max(TOP_MARGIN, (windowManager.container as MDICanvas).height - module.height - yOffset);
            }
            else if (TOP_MARGIN + module.height < (windowManager.container as MDICanvas).height)
            {
                yPos = TOP_MARGIN;
            }
            else if (yOffset + module.height < (windowManager.container as MDICanvas).height)
            {
                yPos = (windowManager.container as MDICanvas).height - module.height - yOffset;
            }
            else
            {
                yPos = (windowManager.container as MDICanvas).height - module.height;
            }
            
            // Assign the calculated coordinates
            width = module.width;
            height = module.height;
            windowManager.absPos(this, xPos, yPos);
            
            return true;
        }
    }
    
    /**
     * Setup wether the window must be maximized.
     */
    override protected function setupWindowMaximization(event : Event = null) : void
    {
        // If maximization is not forbidden
        if (module.maximizable)
        {
            // Try to get the user setting
            var userWindowIsMaximized : Object = SharedObjectManager.getWindowIsMaximized(title);
            if (userWindowIsMaximized != null)
            {
                if (userWindowIsMaximized == true)
                {
                    maximize();
                }
            }
            
            // Else get the kerneos config setting
            else
            {
                if (module.loadMaximized)
                {
                    maximize();
                }
            }
        }
    }
}
}
