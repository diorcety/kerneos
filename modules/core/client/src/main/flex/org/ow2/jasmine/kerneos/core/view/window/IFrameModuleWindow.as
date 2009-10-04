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
import flash.net.URLRequest;
import flash.net.navigateToURL;

import flexlib.mdi.events.MDIWindowEvent;

import org.ow2.jasmine.kerneos.common.view.KerneosIFrame;
import org.ow2.jasmine.kerneos.core.vo.ModuleVO;


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
    private var _frame : KerneosIFrame;
    
    
    
    // =========================================================================
    // Constructor & initialization
    // =========================================================================
    
    /**
     * Build a new window hosting an IFrame
     */
    public function IFrameModuleWindow(module : ModuleVO, frame : KerneosIFrame)
    {
        // Call super class constructor
        super(module);
        
        // Assign properties
        _frame = frame;
        
        // Special controls bar for the IFrame windows...
        this.setStyle("windowControlsClass", org.ow2.jasmine.kerneos.core.view.window.IFrameModuleWindowControlsContainer);
        
        // Listen to window events
        this.addEventListener(MDIWindowEvent.DRAG_START, hideIFrame);
        this.addEventListener(MDIWindowEvent.DRAG_START, hideWhiteBackground);
        this.addEventListener(MDIWindowEvent.DRAG_END, showIFrame);
        this.addEventListener(MDIWindowEvent.DRAG_END, showWhiteBackground);
        this.addEventListener(MDIWindowEvent.FOCUS_START, showIFrame);
        this.addEventListener(MDIWindowEvent.FOCUS_START, showWhiteBackground);
        this.addEventListener(MDIWindowEvent.FOCUS_END, hideIFrame);
        this.addEventListener(MDIWindowEvent.FOCUS_END, hideWhiteBackground);
        this.addEventListener(MDIWindowEvent.RESIZE_START, hideIFrame);
        this.addEventListener(MDIWindowEvent.RESIZE_START, hideWhiteBackground);
        this.addEventListener(MDIWindowEvent.RESIZE_END, showIFrame);
        this.addEventListener(MDIWindowEvent.RESIZE_END, showWhiteBackground);
    }
    
    
    
    /**
     * Create UI children
     */
    override protected function createChildren() : void
    {
        // Call super class method
        super.createChildren();
        
        // Add the IFrame
        _frame.source = module.url;
        _frame.percentHeight = 100;
        _frame.percentWidth = 100;
        _frame.visible = true;
        addChild(_frame);
        
        // Setup the controls
        (windowControls as IFrameModuleWindowControlsContainer).navigateExternallyButton.addEventListener(MouseEvent.CLICK, navigateExternally);
    }
    
    
    
    // =========================================================================
    // Getter & setters
    // =========================================================================
    
    /**
     * Get the hosted IFrame.
     */
    public function get iFrame() : KerneosIFrame
    {
        return _frame;
    }
    
    
    
    /**
     * Set the hosted IFrame.
     */
    public function set iFrame(value : KerneosIFrame) : void
    {
        throw new Error('This is a read only property.');
    }
    
    
    
    // =========================================================================
    // Public methods
    // =========================================================================
    
    /**
     * Kill the IFrame, and leave nothing.
     */
    public function kill(e : Event = null) : void
    {
        _frame.kill();
    }
    
    
    
    /**
     * Hide the IFrame content.
     */
    public function hideIFrame(e : Event = null) : void
    {
        this.iFrame.overlayDetection = false;
        this.iFrame.visible = false;
    }
    
    
    
    /**
     * Show the Iframe content.
     */
    public function showIFrame(e : Event = null) : void
    {
        if (!minimized)
        {
            this.iFrame.visible = true;
            this.iFrame.overlayDetection = true;
        }
    }
    
    
    
    /**
     * Hide the white background.
     */
    public function hideWhiteBackground(e : Event = null) : void
    {
        this.setStyle("backgroundColor", 0x666666);
    }
    
    
    
    /**
     * Show the white background.
     */
    public function showWhiteBackground(e : Event = null) : void
    {
        this.setStyle("backgroundColor", 0xFFFFFF);
    }
    
    
    
    /**
     * Open the module URL in the browser.
     * 
     * Opens in a new tab, expect for Internet Explorer where it is opened in a new window...
     */
    public function navigateExternally(event : Event = null) : void
    {
        navigateToURL(new URLRequest(module.url), "_blank");
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
        this.iFrame.visible = true;
        this.iFrame.overlayDetection = true;
        
        // Call super class method
        super.unMinimize(event);
    }

}
}
