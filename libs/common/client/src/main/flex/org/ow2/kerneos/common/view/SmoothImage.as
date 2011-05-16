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
package org.ow2.kerneos.common.view
{
import flash.display.Bitmap;
import mx.controls.Image;

/**
* An Image object with bitmap smoothing activated by default, for a much
* better rendering
* 
* @author Julien Nicoulaud
*/
public class SmoothImage extends Image
{
    /**
    * Overridden display function to activate bitmap smoothing
    */
    override protected function updateDisplayList (unscaledWidth : Number,
                                                   unscaledHeight : Number):void
    {
        // Call super class function
        super.updateDisplayList (unscaledWidth, unscaledHeight);

        // Check if the image is a bitmap
        if (content is Bitmap) {
            
            // Retrieve the bitmap content
            var bitmap : Bitmap = content as Bitmap;
            
            // If not empty and no bitmap smoothing...
            if (bitmap != null && bitmap.smoothing == false) {
                
                // Activate it
                bitmap.smoothing = true;
            }
        }
    }
}
}
