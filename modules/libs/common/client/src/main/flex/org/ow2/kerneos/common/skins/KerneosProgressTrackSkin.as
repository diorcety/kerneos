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
package org.ow2.kerneos.common.skins
{

import flash.filters.DropShadowFilter;

import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;


/**
 * A custom skin for the track in a ProgressBar.
 *
 * @author Julien Nicoulaud
 */
public class KerneosProgressTrackSkin extends Border
{
    /**
     * Drop shadow filter.
     */
    private var shadowOut : DropShadowFilter = new DropShadowFilter(2, 45, 0x000000, 0.5);
    
    
    
    /**
     * Build a new KerneosProgressTrackSkin.
     */
    public function KerneosProgressTrackSkin()
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
        
        // Draw the progress track
        graphics.clear();
        
        graphics.lineStyle(2, 0xFFFFFF, 1)
        graphics.drawRoundRectComplex(1, 1, w - 2, h - 2, 12, 0, 0, 12);
        graphics.endFill();
        
        // Set the filter
        filters = [shadowOut];
    }
}
}
