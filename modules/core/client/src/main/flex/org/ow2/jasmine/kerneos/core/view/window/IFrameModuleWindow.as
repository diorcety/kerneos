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
import com.google.code.flexiframe.IFrame;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.URLRequest;
import flash.net.navigateToURL;

import flexlib.mdi.events.MDIWindowEvent;

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
     * The hosted IFrame
     */
    private var _frame : IFrame;
    
    
    
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
        _frame = frame;
        
        // Special controls bar for the IFrame windows...
        this.setStyle("windowControlsClass", IFrameModuleWindowControlsContainer);
        
        // Listen to window events
        this.addEventListener(MDIWindowEvent.DRAG_START, hideIFrame);
        this.addEventListener(MDIWindowEvent.DRAG_END, showIFrame);
        this.addEventListener(MDIWindowEvent.FOCUS_START, showIFrame);
        this.addEventListener(MDIWindowEvent.FOCUS_END, hideIFrame);
        this.addEventListener(MDIWindowEvent.RESIZE_START, hideIFrame);
        this.addEventListener(MDIWindowEvent.RESIZE_END, showIFrame);
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
        addChild(_frame);
        
        // Setup the controls
        setupControls();
    }
    
    
    
    /**
     * Setup which controls should be displayed.
     */
    protected function setupControls():void
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
            (windowControls as IFrameModuleWindowControlsContainer).previousPageButton.visible =
            (windowControls as IFrameModuleWindowControlsContainer).previousPageButton.includeInLayout = false;
            (windowControls as IFrameModuleWindowControlsContainer).nextPageButton.visible =
            (windowControls as IFrameModuleWindowControlsContainer).nextPageButton.includeInLayout = false;
        }
        
        if ((module as IFrameModuleVO).showOpenInBrowserButton)
        {        
            (windowControls as IFrameModuleWindowControlsContainer).navigateExternallyButton.addEventListener(MouseEvent.CLICK, navigateExternally);
            showSeparator = true;
        }
        else
        {
            (windowControls as IFrameModuleWindowControlsContainer).navigateExternallyButton.visible =
            (windowControls as IFrameModuleWindowControlsContainer).navigateExternallyButton.includeInLayout = false;
        }
        
        if (!showSeparator)
        {
            (windowControls as IFrameModuleWindowControlsContainer).separator.visible =
            (windowControls as IFrameModuleWindowControlsContainer).separator.includeInLayout = false;
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
        _frame.removeIFrame();
    }
    
    
    
    /**
     * Hide the IFrame content.
     */
    public function hideIFrame(e : Event = null) : void
    {
        this.iFrame.hidden = true;
    }
    
    
    
    /**
     * Show the Iframe content.
     */
    public function showIFrame(e : Event = null) : void
    {
        if (!minimized)
        {
            this.iFrame.hidden = false;
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
    public function historyBack(event : Event = null):void
    {
        _frame.historyBack();
    }
    
    
    
    /**
    * Load the IFrame's next page in the navigation history.
    */
    public function historyForward(event : Event = null):void
    {
        _frame.historyForward();
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
        
        // Hide the IFrame
        hideIFrame();
    }
    
    
    
    /**
     * Override the default unminimize behaviour
     */
    override public function unMinimize(event : MouseEvent = null) : void
    {
        // Restore the IFrame visibility
        this.iFrame.hidden = false;
        
        // Call super class method
        super.unMinimize(event);
    }

}
}
