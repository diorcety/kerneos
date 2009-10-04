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

import flexlib.mdi.containers.MDICanvas;
import flexlib.mdi.containers.MDIWindow;
import flexlib.mdi.events.MDIWindowEvent;

import mx.events.FlexEvent;

import org.ow2.jasmine.kerneos.core.util.SharedObjectManager;


/**
 * A window in Kerneos.
 *
 * @author Julien Nicoulaud
 */
[Bindable]
public class KerneosWindow extends MDIWindow
{
    /**
     * New windows size and placement parameters
     */
    public static var WINDOW_DEFAULT_PERCENT_WIDTH : int = 80;
    
    public static var WINDOW_DEFAULT_PERCENT_HEIGHT : int = 90;
    
    public static var WINDOW_DEFAULT_X_OFFSET : int = 30;
    
    public static var WINDOW_DEFAULT_Y_OFFSET : int = 15;
    
    public static var LEFT_MARGIN : int = 150;
    
    public static var TOP_MARGIN : int = 15;
    
    
    
    // =========================================================================
    // Constructor & initialization
    // =========================================================================
    
    /**
     * Builds a new window
     */
    public function KerneosWindow()
    {
        // Call super class constructor
        super();
        
        // Listen to window events
        this.addEventListener(MDIWindowEvent.MAXIMIZE, onMaximize);
        this.addEventListener(MDIWindowEvent.MINIMIZE, onMinimize);
        this.addEventListener(MDIWindowEvent.RESTORE, onUnMaximize);
        this.addEventListener(MDIWindowEvent.CLOSE, saveWindowSetup);
        
        // On creation complete, setup the window size and position
        this.addEventListener(FlexEvent.CREATION_COMPLETE, setupWindowSizeAndPosition);
    }
    
    
    
    /**
     * Create UI children
     */
    override protected function createChildren() : void
    {
        // Call super class method
        super.createChildren();
        
        // Setup the controls
        windowControls.minimizeBtn.toolTip = "Minimize";
        windowControls.maximizeRestoreBtn.toolTip = "Maximize/Restore";
        windowControls.closeBtn.toolTip = "Close";
    }
    
    
    
    /**
     * Setup the window size and position.
     *
     * Tries to read the user preferences from the shared object. If valid, applies it. Else
     * calculates the window size and position.
     */
    protected function setupWindowSizeAndPosition(event : Event = null) : void
    {
        // Define the variables to calculate
        var windowWidth : int;
        var windowHeight : int;
        var xPos : int;
        var yPos : int;
        
        // Try to get the user saved preferences for this window,
        // and check if they are still applicable.
        var userWindowPlacement : Object = SharedObjectManager.getWindowSizeAndPosition(title);
        var userWindowPlacementIsApplicable : Boolean = true;
        
        if (userWindowPlacement == null)
        {
            userWindowPlacementIsApplicable = false;
        }
        else
        {
        	// Read the user preferences
            windowWidth = userWindowPlacement.width as int;
            windowHeight = userWindowPlacement.height as int;
            xPos = userWindowPlacement.x as int;
            yPos = userWindowPlacement.y as int;
            
            // Check that there are still applicable
            if (windowWidth + xPos > (windowManager.container as MDICanvas).width || windowHeight + yPos > (windowManager.container as MDICanvas).height)
            {
                userWindowPlacementIsApplicable = false;
            }
        }
        
        // If no user preference applicable, recalculate the window placement
        if (!userWindowPlacementIsApplicable)
        {
            var xOffset : int = windowManager.windowList.length * WINDOW_DEFAULT_X_OFFSET;
            var yOffset : int = windowManager.windowList.length * WINDOW_DEFAULT_Y_OFFSET;
            windowWidth = Math.min((windowManager.container as MDICanvas).width - LEFT_MARGIN - xOffset, (windowManager.container as MDICanvas).width * (WINDOW_DEFAULT_PERCENT_WIDTH / 100));
            windowHeight = Math.min((windowManager.container as MDICanvas).height - TOP_MARGIN - yOffset, (windowManager.container as MDICanvas).height * (WINDOW_DEFAULT_PERCENT_HEIGHT / 100));
            xPos = Math.max(LEFT_MARGIN, (windowManager.container as MDICanvas).width - windowWidth - xOffset);
            yPos = Math.max(TOP_MARGIN, (windowManager.container as MDICanvas).height - windowHeight - yOffset);
        }
        
        // Assign the calculated coordinates
        width = windowWidth;
        height = windowHeight;
        windowManager.absPos(this, xPos, yPos);
        
        // Setup wether the window is maximized
        setupWindowMaximization();
    }
    
    
    
    /**
     * Setup wether the window must be maximized.
     */
    protected function setupWindowMaximization(event : Event = null) : void
    {
        // Try to get the user saved preference for this window,
        var userWindowIsMaximized : Object = SharedObjectManager.getWindowIsMaximized(title);
        
        if (userWindowIsMaximized == true)
        {
            maximize();
        }
    }


    // =========================================================================
    // Window operations
    // =========================================================================
    
    /**
     * Bring the window to the front.
     */
    public function bringToFront(e : Event = null) : void
    {
        // Bring the window to front
        if (!minimized)
        {
            if (!hasFocus)
            {
                windowManager.bringToFront(this);
            }
        }
        else
        {
            // If the window is minimized, restore it
            if (minimized)
            {
                unMinimize();
            }
            // Bring it to front
            windowManager.bringToFront(this);
        }
    }
    
    
    
    /**
     * Save the window size and placement to the user shared object.
     */
    public function saveWindowSetup(event : Event = null) : void
    {
        // Only save if the window position and size is not minimized or maximized
        if (!minimized && !maximized)
        {
            SharedObjectManager.setWindowSizeAndPosition(title, width, height, x, y);
        }
        
        // Save wether the window is maximized
        SharedObjectManager.setWindowIsMaximized(title, maximized);
    }
    
    
    
    // =========================================================================
    // Window events handling
    // =========================================================================
    
    /**
     * When the window is maximized
     */
    protected function onMaximize(e : Event = null) : void
    {
        // Save the window setup
        SharedObjectManager.setWindowSizeAndPosition(title, width, height, x, y);
        SharedObjectManager.setWindowIsMaximized(title, maximized);
        
        // Disable rounded corners
        this.setStyle("cornerRadius", 0);
        this.setStyle("dropShadowEnabled", "false");
    }
    
    
    
    /**
     * When the window is maximized
     */
    protected function onUnMaximize(e : Event = null) : void
    {
        // Disable rounded corners
        this.setStyle("cornerRadius", 3);
        this.setStyle("dropShadowEnabled", "true");
    }
    
    
    
    /**
     * When the window is maximized
     */
    protected function onMinimize(e : Event = null) : void
    {
        // Save the window placement
        SharedObjectManager.setWindowSizeAndPosition(title, width, height, x, y);
        SharedObjectManager.setWindowIsMaximized(title, maximized);
    }

}
}
