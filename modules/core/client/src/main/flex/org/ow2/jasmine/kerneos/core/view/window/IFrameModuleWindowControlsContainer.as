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
import flash.geom.Point;

import flexlib.mdi.containers.MDIWindowControlsContainer;

import mx.controls.Button;
import mx.controls.VRule;
import mx.core.UIComponent;
import mx.events.ToolTipEvent;


/**
 * The  controls container for IFramemoduleWindows.
 *
 * @see IFramemoduleWindow
 * @author Julien Nicoulaud
 */
public class IFrameModuleWindowControlsContainer extends MDIWindowControlsContainer
{
    /**
     * The "Previous page" button.
     */
    public var previousPageButton : Button;
    
    /**
     * The "Next page" button.
     */
    public var nextPageButton : Button;
    
    /**
     * The "Navigate externally" button.
     */
    public var navigateExternallyButton : Button;
    
    /**
     * The separator.
     */
    public var separator : VRule;
    
    
    
    /**
     * Build a new IFrameModuleWindowControlsContainer.
     */
    public function IFrameModuleWindowControlsContainer()
    {
        super();
    }
    
    
    
    /**
     * Create UI children.
     */
    override protected function createChildren() : void
    {
        // Create the "Previous page" button
        if (!previousPageButton)
        {
            previousPageButton = new Button();
            previousPageButton.buttonMode = true;
            previousPageButton.setStyle("styleName", "iFrameWindowPreviousPageButton");
            previousPageButton.toolTip = "Previous page";
            addChild(previousPageButton);
        }
        
        // Create the "Next page" button
        if (!nextPageButton)
        {
            nextPageButton = new Button();
            nextPageButton.buttonMode = true;
            nextPageButton.setStyle("styleName", "iFrameWindowNextPageButton");
            nextPageButton.toolTip = "Next page";
            addChild(nextPageButton);
        }
        
        // Create the "Navigate externally" button
        if (!navigateExternallyButton)
        {
            navigateExternallyButton = new Button();
            navigateExternallyButton.buttonMode = true;
            navigateExternallyButton.setStyle("styleName", "iFrameWindowNavigateExternallyButton");
            navigateExternallyButton.toolTip = "Open in the web browser";
            addChild(navigateExternallyButton);
        }
        
        // Create the separator
        if (!separator)
        {
            separator = new VRule();
            separator.setStyle("styleName", "iFrameWindowSeparator");
            separator.height = 16;
            separator.alpha = 0.3;
            addChild(separator);
        }
        
        // Create super class UI children
        super.createChildren();
        
        // Redefine tooltips positioning
        minimizeBtn.addEventListener(ToolTipEvent.TOOL_TIP_SHOW, positionToolTip);
        maximizeRestoreBtn.addEventListener(ToolTipEvent.TOOL_TIP_SHOW, positionToolTip);
        closeBtn.addEventListener(ToolTipEvent.TOOL_TIP_SHOW, positionToolTip);
        navigateExternallyButton.addEventListener(ToolTipEvent.TOOL_TIP_SHOW, positionToolTip);
        previousPageButton.addEventListener(ToolTipEvent.TOOL_TIP_SHOW, positionToolTip);
        nextPageButton.addEventListener(ToolTipEvent.TOOL_TIP_SHOW, positionToolTip);
    }
    
    
    
    /**
     * Overrides the default positioning of the Tooltip, to avoid it being hidden by
     * the IFrame.
     */
    protected function positionToolTip(e : ToolTipEvent) : void
    {
        var targetPoint : Point = ((e.target as UIComponent).parent as UIComponent).contentToGlobal(new Point(e.target.x, e.target.y));
        e.toolTip.x = targetPoint.x - e.toolTip.width;
        e.toolTip.y = targetPoint.y - 5;
    }
}
}
