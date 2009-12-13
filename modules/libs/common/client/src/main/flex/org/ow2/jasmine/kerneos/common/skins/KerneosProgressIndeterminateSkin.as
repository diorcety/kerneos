/**
 * JASMINe
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
package org.ow2.jasmine.kerneos.common.skins
{

import flash.display.Graphics;
import flash.filters.DropShadowFilter;

import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;


/**
 * A custom skin for the indeterminate state of a ProgressBar.
 *
 * @author Julien Nicoulaud
 */
public class KerneosProgressIndeterminateSkin extends Border
{
    /**
     * Drop shadow filter.
     */
    private var shadowIn : DropShadowFilter = new DropShadowFilter(2, 45, 0x000000, 0.5);
    
    
    
    /**
     * Build a new KerneosProgressIndeterminateSkin.
     */
    public function KerneosProgressIndeterminateSkin()
    {
        super();
    }
    
    
    
    /**
     * @inheritDoc
     */
    override public function get measuredWidth() : Number
    {
        return 195;
    }
    
    
    
    /**
     * @inheritDoc
     */
    override public function get measuredHeight() : Number
    {
        return 6;
    }
    
    
    
    /**
     * @inheritDoc
     */
    override protected function updateDisplayList(w : Number, h : Number) : void
    {
        super.updateDisplayList(w, h);
        
        // Retrieve the user-defined styles
        var fillColors : Array = getStyle("trackColors") as Array;
        StyleManager.getColorNames(fillColors);
        var fillColor : uint = StyleManager.isValidStyleValue(fillColors[0]) ? fillColors[0] : 0x666666;
        
        // Compute the hatch interval        
        var hatchInterval : Number = getStyle("indeterminateMoveInterval");
        
        if (isNaN(hatchInterval))
            hatchInterval = 28;
        
        // Draw the hatches
        var g : Graphics = graphics;
        g.clear();
        
        for (var i : int = 0; i < w; i += hatchInterval)
        {
            g.beginFill(fillColor, 0.8);
            g.moveTo(i, 2);
            g.lineTo(Math.min(i + 14, w), 2);
            g.lineTo(Math.min(i + 10, w), h - 2);
            g.lineTo(Math.max(i - 4, 0), h - 2);
            g.lineTo(i, 2);
            g.endFill();
        }
        
        // Set the filter
        filters = [shadowIn];
    }
}
}
