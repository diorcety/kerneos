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
import flash.geom.Point;

import flexlib.mdi.containers.MDIWindow;
import flexlib.mdi.events.MDIWindowEvent;

import mx.controls.Button;
import mx.core.UIComponent;
import mx.effects.Fade;
import mx.effects.Glow;
import mx.effects.Sequence;
import mx.events.ToolTipEvent;
import mx.graphics.ImageSnapshot;
import mx.resources.ResourceManager;

import org.ow2.kerneos.core.managers.LanguagesManager;


/**
 * A button representing a module window, for the taskbar.
 *
 * @author Julien Nicoulaud
 */
public class MinimizedModuleWindow extends Button
{
    
    // =========================================================================
    // Variables
    // =========================================================================
    
    /**
     * The default displayed snapshot width.
     */
    public static var SNAPSHOT_WIDTH : Number = 300;
    
    /**
     * The corresponding module window.
     */
    public var _moduleWindow : ModuleWindow;
    
    /**
     * A snapshot of the attached window.
     */
    protected var snapshot : ImageSnapshot;
    
    /**
     * Wether the snapshot is marked as obsolete.
     */
    protected var snapshotObsolete : Boolean = true;
    
    
    
    // =========================================================================
    // Constructors
    // =========================================================================
    
    /**
     * Builds a new window for a module.
     */
    public function MinimizedModuleWindow(window : ModuleWindow)
    {
        // Call super class constructor
        super();
        
        // Assign variables
        this._moduleWindow = window;
        this.label = window.module.name;
        this.toolTip = ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE,'kerneos.windows.taskbar.label',[window.module.name]);
        this.setStyle("icon", window.module.getSmallIcon(this, true) as Class);
        this.doubleClickEnabled = true;
        
        // Intercept button click events
        this.addEventListener(MouseEvent.CLICK, simpleClickHandler);
        this.addEventListener(MouseEvent.DOUBLE_CLICK, doubleClickHandler);
        
        // Snapshot ToolTip
        this.addEventListener(ToolTipEvent.TOOL_TIP_CREATE, createSnapshotToolTip);
        this.addEventListener(ToolTipEvent.TOOL_TIP_SHOW, positionToolTip);
        this.addEventListener(MouseEvent.MOUSE_OVER, updateSnapshot);
        
        // Intercept window events
        _moduleWindow.addEventListener(MDIWindowEvent.MINIMIZE, windowMinimizeHandler, false, 0, true);
        _moduleWindow.addEventListener(MDIWindowEvent.RESTORE, windowRestoreHandler, false, 0, true);
        _moduleWindow.addEventListener(MDIWindowEvent.FOCUS_START, windowFocusStartHandler, false, 0, true);
        _moduleWindow.addEventListener(MDIWindowEvent.FOCUS_END, windowFocusEndHandler, false, 0, true);
    }
    
    
    
    // =========================================================================
    // Public methods
    // =========================================================================
    
    /**
     * Flash the button.
     */
    public function flash(color : uint) : void
    {
        var effect : Glow = new Glow();
        effect.blurXTo = 50;
        effect.blurXFrom = 0;
        effect.blurYFrom = 0;
        effect.blurYTo = 15;
        effect.strength = 5;
        effect.duration = 300;
        effect.color = color;
        effect.repeatCount = 3;
        effect.play([this]);
    }
    
    
    
    /**
     * Blink the button.
     */
    public function blink() : void
    {
        var fadeIn : Fade = new Fade();
        fadeIn.alphaFrom = 0;
        fadeIn.alphaTo = 1;
        fadeIn.duration = 200;
        
        var fadeOut : Fade = new Fade();
        fadeOut.alphaFrom = 1;
        fadeOut.alphaTo = 0;
        fadeOut.duration = 200;
        
        var effect : Sequence = new Sequence();
        effect.addChild(fadeOut);
        effect.addChild(fadeIn);
        effect.repeatCount = 3;
        
        effect.play([this]);
    }
    
    
    
    // =========================================================================
    // Button click events handlers
    // =========================================================================
    
    /**
     * When the button is simple clicked.
     */
    protected function simpleClickHandler(e : MouseEvent) : void
    {
        // If the window has focus, minimize it
        if (!_moduleWindow.minimized)
        {
            if (_moduleWindow.hasFocus)
            {
                //_moduleWindow.minimize();
            }
            else
            {
                _moduleWindow.windowManager.bringToFront(_moduleWindow as MDIWindow);
            }
        }
        else
        {
            // If the window is minimized, restore it
            if (_moduleWindow.minimized)
            {
                _moduleWindow.unMinimize();
            }
            // Bring it to front
            _moduleWindow.windowManager.bringToFront(_moduleWindow as MDIWindow);
        }
    }
    
    
    
    /**
     * When the button is double clicked.
     */
    protected function doubleClickHandler(e : MouseEvent) : void
    {
        if (!_moduleWindow.maximized)
        {
            // Maximize it
            _moduleWindow.maximize();
        }
    }
    
    
    
    // =========================================================================
    // Window events handler
    // =========================================================================
    
    /**
     * When the window is minimized.
     */
    protected function windowMinimizeHandler(e : Event) : void
    {
        windowFocusEndHandler();
    }
    
    
    
    /**
     * When the window is restored.
     */
    protected function windowRestoreHandler(e : Event) : void
    {
    }
    
    
    
    /**
     * When the window gets the focus.
     */
    protected function windowFocusStartHandler(e : Event) : void
    {
        this.setStyle("borderColor", "#454545");
        this.setStyle("fillColors", [0x000000, 0x000000, 0xffffff, 0xeeeeee]);
    }
    
    
    
    /**
     * When the window loses the focus.
     */
    protected function windowFocusEndHandler(e : Event = null) : void
    {
        this.setStyle("borderColor", "#BBBBBB");
        this.setStyle("fillColors", [0x999999, 0x454545, 0xffffff, 0xeeeeee]);
        snapshotObsolete = true;
    }
    
    
    
    // =========================================================================
    // Window snapshot ToolTip
    // =========================================================================
    
    /**
     * Update the window snapshot.
     */
    public function updateSnapshot(e : Event = null) : void
    {
        // Don't take the snapshot if it is not obsolete, the window is minimized or this is an
        // IFrame window.
        if (snapshotObsolete && !_moduleWindow.minimized && !(_moduleWindow is IFrameModuleWindow))
        {
            snapshot = ImageSnapshot.captureImage(_moduleWindow);
            snapshotObsolete = false;
        }
    }
    
    
    
    /**
     * Position the ToolTip.
     */
    protected function positionToolTip(e : ToolTipEvent) : void
    {
        var targetPoint : Point = ((this as UIComponent).parent as UIComponent).contentToGlobal(new Point(this.x, this.y));
        e.toolTip.x = Math.max(2, (targetPoint.x + this.width / 2) - e.toolTip.width / 2);
        e.toolTip.y = targetPoint.y - e.toolTip.height - 4;
    }
    
    
    
    /**
     * Create a window snapshot ToolTip.
     */
    protected function createSnapshotToolTip(event : ToolTipEvent) : void
    {
        if (snapshot != null)
        {
            var ptt : ModuleSnapshotTooltip = new ModuleSnapshotTooltip();
            ptt.text = _moduleWindow.module.name;
            ptt.bitmapData = snapshot.data;
            ptt.snapshotWidth = SNAPSHOT_WIDTH;
            ptt.snapshotHeight = SNAPSHOT_WIDTH * snapshot.height / snapshot.width;
            event.toolTip = ptt;
        }
    }
}
}
