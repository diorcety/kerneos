/**
 * JASMINe
 * Copyright (C) 2008 Bull S.A.S.
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
package org.ow2.jasmine.kerneos.common.view
{

import mx.skins.ProgrammaticSkin;
import flash.geom.Matrix;


/**
 * Programmatic skin : gradient image. Allows to use a gradient background in
 * classic containers such as Box or Canvas.
 *
 * @example
 * - In the MXML:
 *   <listing version="3.0">
 *       <Box styleName="myGradient"/>
 *   </listing>
 *
 * - In the CSS (linear gradient):
 *   <listing version="3.0">
 *       .myGradient {
 *           backgroundImage: ClassReference("org.ow2.jasmine.monitoring.eos.common.view.GradientBackground");
 *           backgroundSize: "100%";
 *           fillColors: #FF0000, #000000;
 *           fillAlphas: 1.0,1.0;
 *       }
 *   </listing>
 *
 * - In the CSS (radial gradient):
 *   <listing version="3.0">
 *       .myGradient {
 *           backgroundImage: ClassReference("org.ow2.jasmine.monitoring.eos.common.view.GradientBackground");
 *           backgroundSize: “100%”;
 *           gradientType: radial;
 *           fillColors: #FFCC33, #999999;
 *           fillAlphas: 0.2, 0.5;
 *           angle: 210;
 *           focalPointRatio: 0.75;
 *       }
 *   </listing>
 * 
 * @author Julien Nicoulaud
 */
public class GradientBackground extends ProgrammaticSkin
{
    /**
     * Return a false measured width
     */
    override public function get measuredWidth() : Number
    {
        return 20;
    }
    
    
    
    /**
     * Return a false mesured height
     */
    override public function get measuredHeight() : Number
    {
        return 20;
    }
    
    
    
    /**
     * Override the default display
     */
    override protected function updateDisplayList(unscaledWidth : Number, unscaledHeight : Number) : void
    {
        var fillColors : Array = getStyle("fillColors");
        var fillAlphas : Array = getStyle("fillAlphas");
        var cornerRadius : int = getStyle("cornerRadius");
        var gradientType : String = getStyle("gradientType");
        var angle : Number = getStyle("angle");
        var focalPointRatio : Number = getStyle("focalPointRatio");
        
        // Default values, if styles aren’t defined
        if (fillColors == null)
            fillColors = [0xEEEEEE, 0x999999];
        
        if (fillAlphas == null)
            fillAlphas = [1, 1];
        
        if (gradientType == "" || gradientType == null)
            gradientType = "linear";
        
        if (isNaN(angle))
            angle = 90;
        
        if (isNaN(focalPointRatio))
            focalPointRatio = 0.5;
        
        var matrix : Matrix = new Matrix();
        matrix.createGradientBox(unscaledWidth, unscaledHeight, angle * Math.PI / 180);
        
        graphics.beginGradientFill(gradientType, fillColors, fillAlphas, [0, 255], matrix, "pad", "rgb", focalPointRatio);
        graphics.drawRect(0, 0, unscaledWidth, unscaledHeight);
        //graphics.drawRoundRect(0, 0, unscaledWidth, unscaledHeight,cornerRadius*2/this.scaleX, cornerRadius*2/this.scaleY);
        graphics.endFill();
    }
}
}
