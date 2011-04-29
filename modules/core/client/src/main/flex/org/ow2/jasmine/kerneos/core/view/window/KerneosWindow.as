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
package org.ow2.jasmine.kerneos.core.view.window
{
import flash.events.Event;

import flexlib.mdi.containers.MDICanvas;
import flexlib.mdi.containers.MDIWindow;
import flexlib.mdi.events.MDIWindowEvent;

import mx.effects.Fade;
import mx.events.FlexEvent;
import mx.resources.ResourceManager;

import org.ow2.jasmine.kerneos.core.managers.LanguagesManager;
import org.ow2.jasmine.kerneos.core.managers.SharedObjectManager;


/**
 * A window in Kerneos.
 *
 * @author Julien Nicoulaud
 */
[Bindable]
public class KerneosWindow extends MDIWindow
{
    // =========================================================================
    // Properties
    // =========================================================================
    
    /**
     * New windows size and placement parameters
     */
    public static var WINDOW_DEFAULT_PERCENT_WIDTH : int = 80;
    
    public static var WINDOW_DEFAULT_PERCENT_HEIGHT : int = 90;
    
    public static var WINDOW_DEFAULT_X_OFFSET : int = 30;
    
    public static var WINDOW_DEFAULT_Y_OFFSET : int = 15;
    
    public static var LEFT_MARGIN : int = 150;
    
    public static var TOP_MARGIN : int = 15;
    
    public static var MINIMUM_ACCEPTED_WINDOW_WIDTH : int = 100;
    
    public static var MINIMUM_ACCEPTED_WINDOW_HEIGHT : int = 100;
    
    public static var WINDOW_MOVE_COLLISION_MARGIN : int = 50;
    
    // Effects
    
    /**
     * The effect shown when displaying the window.
     */
    public var showEffect : Fade;
    
    
    
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
        
        // Event listeners are declared here, sub-classes must not redeclare them and override
        // the event handlers instead.
        this.addEventListener(MDIWindowEvent.MAXIMIZE, onMaximize);
        this.addEventListener(MDIWindowEvent.MINIMIZE, onMinimize);
        this.addEventListener(MDIWindowEvent.RESTORE, onRestore);
        this.addEventListener(MDIWindowEvent.CLOSE, onClose);
        this.addEventListener(MDIWindowEvent.FOCUS_START, onFocusStart);
        this.addEventListener(MDIWindowEvent.FOCUS_END, onFocusEnd);
        this.addEventListener(MDIWindowEvent.RESIZE_START, onResizeStart);
        this.addEventListener(MDIWindowEvent.RESIZE, onResize);
        this.addEventListener(MDIWindowEvent.RESIZE_END, onResizeEnd);
        this.addEventListener(MDIWindowEvent.DRAG_START, onDragStart);
        this.addEventListener(MDIWindowEvent.DRAG, onDrag);
        this.addEventListener(MDIWindowEvent.DRAG_END, onDragEnd);
        
        // On creation complete, setup the window size and position
        this.addEventListener(FlexEvent.CREATION_COMPLETE, setupWindowSizeAndPosition);
        
        // Setup the effects
        showEffect = new Fade();
        showEffect.alphaFrom = 0;
        showEffect.alphaTo = 1;
        showEffect.duration = 500;
        this.setStyle("showEffect", showEffect);
        this.setStyle("addedEffect", showEffect);
    }
    
    
    
    /**
     * Create UI children
     */
    override protected function createChildren() : void
    {
        // Call super class method
        super.createChildren();
        
        // Setup the controls
        windowControls.minimizeBtn.toolTip = ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.windows.minimizeButton.tooltip');
        windowControls.maximizeRestoreBtn.toolTip = ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.windows.maximizeRestoreButton.tooltip');
        windowControls.closeBtn.toolTip = ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.windows.closeButton.tooltip');
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
     * When the window is maximized.
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
     * When the window is restored.
     */
    protected function onRestore(e : Event = null) : void
    {
        // Disable rounded corners
        this.setStyle("cornerRadius", 3);
        this.setStyle("dropShadowEnabled", "true");
    }
    
    
    
    /**
     * When the window is minimized.
     */
    protected function onMinimize(e : Event = null) : void
    {
        // Save the window placement
        SharedObjectManager.setWindowSizeAndPosition(title, width, height, x, y);
        SharedObjectManager.setWindowIsMaximized(title, maximized);
    }
    
    
    
    /**
     * When the window is closed.
     */
    protected function onClose(e : Event = null) : void
    {
        // Save the window layout for the next time it is opened.
        saveWindowSetup();
    }
    
    
    
    /**
     * When the window gets the focus.
     */
    protected function onFocusStart(e : Event = null) : void
    {
        // Check collisions with iframes.
        checkCollisions();
    }
    
    
    
    /**
     * When the window loses the focus.
     */
    protected function onFocusEnd(e : Event = null) : void
    {
    }
    
    
    
    /**
     * When the window starts being resized.
     */
    protected function onResizeStart(e : Event = null) : void
    {
    }
    
    
    
    /**
     * When the window is resized.
     */
    protected function onResize(e : Event = null) : void
    {
        // Check collisions with iframes, with a margin to avoid the mouse "falling" in one of them.
        checkCollisions(WINDOW_MOVE_COLLISION_MARGIN);
    }
    
    
    
    /**
     * When the window stops being resized.
     */
    protected function onResizeEnd(e : Event = null) : void
    {
        // Check collisions with iframes.
        checkCollisions();
    }
    
    
    
    /**
     * When the window starts being dragged.
     */
    protected function onDragStart(e : Event = null) : void
    {
    }
    
    
    
    /**
     * When the window is being dragged.
     */
    protected function onDrag(e : Event = null) : void
    {
        // Check collisions with iframes, with a margin to avoid the mouse "falling" in one of them.
        checkCollisions(WINDOW_MOVE_COLLISION_MARGIN);
    }
    
    
    
    /**
     * When the window stops being dragged.
     */
    protected function onDragEnd(e : Event = null) : void
    {
        // Check collisions with iframes.
        checkCollisions();
    }
    
    
    
    // =========================================================================
    // Window size and placement
    // =========================================================================
    
    /**
     * Setup the window size and position.
     *
     * Tries to read the user preferences from the shared object. If valid, applies it. Else
     * calculates the window size and position.
     */
    protected function setupWindowSizeAndPosition(event : Event = null) : void
    {
        // Try the user settings, then the default settings
        if (!setupUserWindowSizeAndPosition())
        {
            setupDefaultWindowSizeAndPosition();
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
    protected function setupUserWindowSizeAndPosition() : Boolean
    {
        // Try to get the user saved preferences for this window,
        // and check if they are still applicable.
        var userWindowPlacement : Object = SharedObjectManager.getWindowSizeAndPosition(title);
        
        // If the user has no saved setting
        if (userWindowPlacement == null)
        {
            return false;
        }
        else
        {
            // Read the user preferences
            var windowWidth : int = userWindowPlacement.width as int;
            var windowHeight : int = userWindowPlacement.height as int;
            var xPos : int = userWindowPlacement.x as int;
            var yPos : int = userWindowPlacement.y as int;
            
            // Check that they are still applicable
            if (windowWidth + xPos > (windowManager.container as MDICanvas).width || windowHeight + yPos > (windowManager.container as MDICanvas).height)
            {
                return false;
            }
            
            // Assign the calculated coordinates
            width = windowWidth;
            height = windowHeight;
            windowManager.absPos(this, xPos, yPos);
            
            return true;
        }
    
    }
    
    
    
    /**
     * Apply the default algorithm for positioning and sizing the window.
     */
    protected function setupDefaultWindowSizeAndPosition() : void
    {
        // Compute the default settings
        var xOffset : int = windowManager.windowList.length * WINDOW_DEFAULT_X_OFFSET;
        var yOffset : int = windowManager.windowList.length * WINDOW_DEFAULT_Y_OFFSET;
        var windowWidth : int = Math.min((windowManager.container as MDICanvas).width - LEFT_MARGIN - xOffset, (windowManager.container as MDICanvas).width * (WINDOW_DEFAULT_PERCENT_WIDTH / 100));
        var windowHeight : int = Math.min((windowManager.container as MDICanvas).height - TOP_MARGIN - yOffset, (windowManager.container as MDICanvas).height * (WINDOW_DEFAULT_PERCENT_HEIGHT / 100));
        var xPos : int = Math.max(LEFT_MARGIN, (windowManager.container as MDICanvas).width - windowWidth - xOffset);
        var yPos : int = Math.max(TOP_MARGIN, (windowManager.container as MDICanvas).height - windowHeight - yOffset);
        
        // Assign the calculated coordinates
        width = windowWidth;
        height = windowHeight;
        windowManager.absPos(this, xPos, yPos);
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
    // Collisions with IFrameWindows
    // =========================================================================
    
    /**
     * Calculate which IFrameWindows this one overlaps and notify them.
     *
     * @param margin the margin to apply when calculating the collisions
     */
    protected function checkCollisions(margin : Number = 0) : void
    {
        for each (var window : MDIWindow in windowManager.windowList)
        {
            if (window != this && window is IFrameModuleWindow)
            {
                var iFrameWindow : IFrameModuleWindow = window as IFrameModuleWindow;
                
                if (checkCollisionWithWindow(iFrameWindow, margin))
                {
                    iFrameWindow.declareOverlapping(this);
                }
                else
                {
                    iFrameWindow.declareNotOverlapping(this);
                }
            }
        }
    }
    
    
    
    /**
     * Check if the current window overlaps the given window.
     *
     * @param margin the margin to apply when calculating the collisions
     */
    protected function checkCollisionWithWindow(window : IFrameModuleWindow, margin : Number = 0) : Boolean
    {
        // The windows overlap each other if they overlap on both X and Y axis.
        var overlapX : Boolean = false;
        var overlapY : Boolean = false;
        
        // Calcul for the X axis.
        var myLeft : int = this.x;
        var myRight : int = this.x + this.width;
        var windowLeft : int = window.x - margin;
        var windowRight : int = window.x + window.width + margin;
        
        overlapX = windowLeft >= myLeft && windowLeft <= myRight;
        overlapX ||= windowLeft <= myLeft && windowRight >= myLeft;
        
        // Calcul for the Y axis.
        var myTop : int = this.y;
        var myBottom : int = this.y + this.height;
        var windowTop : int = window.y - margin;
        var windowBottom : int = window.y + window.height + margin;
        
        overlapY = windowTop >= myTop && windowTop <= myBottom;
        overlapY ||= windowTop <= myTop && windowBottom >= myTop;
        
        return overlapX && overlapY;
    }
}
}
