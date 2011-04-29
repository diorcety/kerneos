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
import com.google.code.flexiframe.IFrame;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.URLRequest;
import flash.net.navigateToURL;

import mx.collections.ArrayCollection;

import org.ow2.jasmine.kerneos.common.util.KerneosLogger;
import org.ow2.jasmine.kerneos.core.vo.IFrameModuleVO;


/**
 * A module window hosting an IFrame.
 *
 * @see IFrameModuleWindowContainer
 * @author Julien Nicoulaud
 */
[Bindable]
public class IFrameModuleWindow extends ModuleWindow
{
    // =========================================================================
    // Variables
    // =========================================================================

    /**
     * The hosted IFrame.
     */
    protected var _frame : IFrame;

    /**
     * True when the frame content has finished loading.
     */
    protected var _frameLoaded : Boolean = false;

    /**
     * The registry of the windows that currently overlap this window.
     */
    [ArrayElementType('org.ow2.jasmine.kerneos.core.view.window.KerneosWindow')]
    protected var _overlappingWindows : ArrayCollection = new ArrayCollection();



    // =========================================================================
    // Constructor & initialization
    // =========================================================================

    /**
     * Build a new window hosting an IFrame
     */
    public function IFrameModuleWindow(module : IFrameModuleVO, frame : IFrame)
    {
        // Call super class constructor
        super(module);

        // Assign properties
        this._frame = frame;

        // Special controls bar for the IFrame windows...
        this.setStyle("windowControlsClass", IFrameModuleWindowControlsContainer);

        // Listen to the "frameLoad" event
        this._frame.addEventListener("frameLoad", onFrameLoaded);
    }



    /**
     * Create UI children
     */
    override protected function createChildren() : void
    {
        // Call super class method
        super.createChildren();

        // Add the IFrame
        _frame.loadIndicatorClass = IFrameLoadIndicator;
        _frame.source = (module as IFrameModuleVO).url;
        _frame.percentHeight = 100;
        _frame.percentWidth = 100;
        _frame.overlayDetection = true;
        _frame.debug = false;
        addChild(_frame);

        // Setup the controls
        setupControls();
    }



    /**
     * Setup which controls should be displayed.
     */
    protected function setupControls() : void
    {
        var showSeparator : Boolean = false;

        if ((module as IFrameModuleVO).showHistoryNavigationButtons)
        {
            (windowControls as IFrameModuleWindowControlsContainer).previousPageButton.addEventListener(MouseEvent.CLICK, historyBack);
            (windowControls as IFrameModuleWindowControlsContainer).nextPageButton.addEventListener(MouseEvent.CLICK, historyForward);
            showSeparator = true;
        }
        else
        {
            (windowControls as IFrameModuleWindowControlsContainer).previousPageButton.visible = (windowControls as IFrameModuleWindowControlsContainer).previousPageButton.includeInLayout = false;
            (windowControls as IFrameModuleWindowControlsContainer).nextPageButton.visible = (windowControls as IFrameModuleWindowControlsContainer).nextPageButton.includeInLayout = false;
        }

        if ((module as IFrameModuleVO).showOpenInBrowserButton)
        {
            (windowControls as IFrameModuleWindowControlsContainer).navigateExternallyButton.addEventListener(MouseEvent.CLICK, navigateExternally);
            showSeparator = true;
        }
        else
        {
            (windowControls as IFrameModuleWindowControlsContainer).navigateExternallyButton.visible = (windowControls as IFrameModuleWindowControlsContainer).navigateExternallyButton.includeInLayout = false;
        }

        if (!showSeparator)
        {
            (windowControls as IFrameModuleWindowControlsContainer).separator.visible = (windowControls as IFrameModuleWindowControlsContainer).separator.includeInLayout = false;
        }
    }



    // =========================================================================
    // Getter & setters
    // =========================================================================

    /**
     * Get the hosted IFrame.
     */
    public function get iFrame() : IFrame
    {
        return _frame;
    }



    /**
     * Set the hosted IFrame.
     */
    public function set iFrame(value : IFrame) : void
    {
        throw new Error('This is a read only property.');
    }



    // =========================================================================
    // Public methods
    // =========================================================================

    /**
     * Kill the IFrame, and leave nothing.
     */
    public function removeIFrame(e : Event = null) : void
    {
        iFrame.removeIFrame();
    }



    /**
     * Hide the IFrame content.
     */
    public function hideIFrame(e : Event = null) : void
    {
        if (_frameLoaded && iFrame.visible)
        {
            iFrame.visible = false;
        }
    }



    /**
     * Show the Iframe content.
     */
    public function showIFrame(e : Event = null) : void
    {
        if (!minimized && _overlappingWindows.length == 0 && !iFrame.visible)
        {
            iFrame.visible = true;
        }
    }



    /**
     * Open the module URL in the browser.
     *
     * Opens in a new tab, excepted for Internet Explorer where it is opened in a new window...
     */
    public function navigateExternally(event : Event = null) : void
    {
        navigateToURL(new URLRequest((module as IFrameModuleVO).url), "_blank");
    }



    /**
     * Load the IFrame's last page in the navigation history.
     */
    public function historyBack(event : Event = null) : void
    {
        _frame.historyBack();
    }



    /**
     * Load the IFrame's next page in the navigation history.
     */
    public function historyForward(event : Event = null) : void
    {
        _frame.historyForward();
    }



    // =========================================================================
    // Window operations & events
    // =========================================================================

    /**
     * Override the default minimize behaviour
     */
    override public function minimize(event : MouseEvent = null) : void
    {
        // Call super class method
        super.minimize(event);

        // Hide the IFrame
        hideIFrame();
    }



    /**
     * Override the default unminimize behaviour
     */
    override public function unMinimize(event : MouseEvent = null) : void
    {
        // Restore the IFrame visibility
        this.iFrame.visible = true;

        // Call super class method
        super.unMinimize(event);
    }



    /**
     * When the window gets the focus.
     */
    override protected function onFocusStart(e : Event = null) : void
    {
        // Call super class method
        super.onFocusStart(e);

        // Reset the overlapping window registry since we heve the focus
        clearOverlappingWindowsRegistry();

        // Try to show the iframe
        showIFrame();
    }



    /**
     * When the window starts being resized.
     */
    override protected function onResizeStart(e : Event = null) : void
    {
        // Call super class method
        super.onResizeStart(e);

        // Hide the IFrame
        hideIFrame();
    }



    /**
     * When the window stops being resized.
     */
    override protected function onResizeEnd(e : Event = null) : void
    {
        // Call super class method
        super.onResizeEnd(e);

        // Try to show the iframe
        showIFrame();
    }



    /**
     * When the window starts being dragged.
     */
    override protected function onDragStart(e : Event = null) : void
    {
        // Call super class method
        super.onDragStart(e);

        // Hide the IFrame
        hideIFrame();
    }



    /**
     * When the window stops being dragged.
     */
    override protected function onDragEnd(e : Event = null) : void
    {
        // Call super class method
        super.onDragEnd(e);

        // Try to show the iframe
        showIFrame();
    }



    /**
     * When the frame content has finished loading.
     */
    protected function onFrameLoaded(e : Event = null) : void
    {
        // Mark it as loaded
        _frameLoaded = true;

        // Show the iframe
        showIFrame();
    }



    // =========================================================================
    // Overlapping system
    // =========================================================================

    /**
     * Allow another window to declare that it overlaps this one.
     */
    public function declareOverlapping(window : KerneosWindow) : void
    {
        // If this is a new overlapping window
        if (_overlappingWindows.getItemIndex(window) < 0)
        {

            // Hide the iFrame
            hideIFrame();

            // Remember the window
            _overlappingWindows.addItem(window);
        }
    }



    /**
     * Allow another window to declare that it does not overlap this one.
     */
    public function declareNotOverlapping(window : KerneosWindow) : void
    {
        // If the window was overlapping us
        var overlappingWindowIndex : int = _overlappingWindows.getItemIndex(window);

        if (overlappingWindowIndex >= 0)
        {

            // Remove it from the register
            _overlappingWindows.removeItemAt(overlappingWindowIndex);

            // Try to show the iframe
            showIFrame();
        }
    }



    /**
     * Reset the registry of the overlapping windows.
     */
    protected function clearOverlappingWindowsRegistry(e : Event = null) : void
    {
        _overlappingWindows = new ArrayCollection();
    }

}
}
