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
package org.ow2.jasmine.kerneos.common.skins
{

import flash.filters.DropShadowFilter;

import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;


/**
 * A custom skin for a ProgressBar.
 *
 * @author Julien Nicoulaud
 */
public class KerneosProgressBarSkin extends Border
{
    /**
     * Drop shadow filter.
     */
    private var shadowOut : DropShadowFilter = new DropShadowFilter(2, 45, 0x000000, 0.5);
    
    /**
     * Drop shadow filter.
     */
    private var shadowIn : DropShadowFilter = new DropShadowFilter(2, 45, 0x000000, 0.5, 4, 4, 1, 1, true);
    
    
    
    /**
     * Build a new KerneosProgressBarSkin.
     */
    public function KerneosProgressBarSkin()
    {
        super();
    }
    
    
    
    /**
     * @inheritDoc
     */
    override public function get measuredWidth() : Number
    {
        return 200;
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
        var barColorStyle : * = getStyle("barColor");
        var barColor : uint = StyleManager.isValidStyleValue(barColorStyle) ? barColorStyle : getStyle("themeColor");
        
        // Draw the progress bar
        graphics.clear();
        
        if (barColor != 0xFFFFFF)
        {
            graphics.beginFill(barColor, 1);
            graphics.drawRoundRectComplex(1, 1, w - 2, h - 2, 12, 0, 0, 12);
            graphics.endFill();
        }
        
        // Set the filters
        filters = [shadowIn, shadowOut];
    }
}
}
