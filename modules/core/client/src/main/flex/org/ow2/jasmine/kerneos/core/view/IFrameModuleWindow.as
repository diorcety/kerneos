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
package org.ow2.jasmine.kerneos.core.view
{

    import flash.events.Event;
    import flash.events.MouseEvent;

    import flexlib.mdi.events.MDIWindowEvent;

    import mx.core.Container;

    import org.ow2.jasmine.kerneos.core.vo.ModuleVO;


/**
 * A module window hosting an IFrame
 *
 * @author Julien Nicoulaud
 */
[Bindable]
public class IFrameModuleWindow extends ModuleWindow {

    // =========================================================================
    // Constructor
    // =========================================================================

    /**
     * Build a new window hosting an IFrame
     */
    public function IFrameModuleWindow(module:ModuleVO) {
        // Call super class constructor
        super(module);

        // Listen to window events
        this.addEventListener(MDIWindowEvent.MINIMIZE,hideIFrame);
        this.addEventListener(MDIWindowEvent.DRAG_START,hideIFrame);
        this.addEventListener(MDIWindowEvent.RESIZE_START,hideIFrame);

        this.addEventListener(MDIWindowEvent.RESTORE,showIFrame);
        this.addEventListener(MDIWindowEvent.MAXIMIZE,showIFrame);
        this.addEventListener(MDIWindowEvent.DRAG_END,showIFrame);
        this.addEventListener(MDIWindowEvent.RESIZE_END,showIFrame);
    }

    // =========================================================================
    // Getter & setters
    // =========================================================================

    /**
     * Get the hosted IFrame
     */
    private function get iFrame():SuperIFrame {
        return (this as Container).getChildAt(0) as SuperIFrame;
    }

    // =========================================================================
    // Private methods
    // =========================================================================

    /**
     * Hide the IFrame content
     */
    private function hideIFrame(e:Event=null):void {
        this.iFrame.iFrameVisible = false;
    }

    /**
     * Show the IFrame content
     */
    private function showIFrame(e:Event=null):void {
        if (this.hasFocus) {
            this.iFrame.iFrameVisible = true;
        }
    }

}
}
