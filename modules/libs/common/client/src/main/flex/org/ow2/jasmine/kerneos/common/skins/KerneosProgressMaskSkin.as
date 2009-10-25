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

import flash.display.Graphics;

import mx.skins.ProgrammaticSkin;


/**
 * A custom skin for ProgressBar mask.
 * 
 * @author Julien Nicoulaud
 */
public class KerneosProgressMaskSkin extends ProgrammaticSkin
{
    
    /**
     * Build a new KerneosProgressMaskSkin.
     */
    public function KerneosProgressMaskSkin()
    {
        super();
    }
    
    
    
    /**
     * @inheritDoc
     */
    override protected function updateDisplayList(w : Number, h : Number) : void
    {
        super.updateDisplayList(w, h);
        
        // Draw the mask
        var g : Graphics = graphics;
        g.clear();
        g.beginFill(0xFF0000);
        g.drawRoundRectComplex(2, 2, w - 4, h - 4, 12, 0, 0, 12);
        g.endFill();
    }

}
}
