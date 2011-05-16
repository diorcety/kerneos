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
package org.ow2.kerneos.core.view.preloader
{
import flash.display.GradientType;
import flash.display.Sprite;
import flash.filters.DropShadowFilter;
import flash.geom.Matrix;
import flash.text.TextField;
import flash.text.TextFormat;


/**
 * A custom preloader displayed while the user downloads the application.
 *
 * Massively inspired from
 * http://www.pathf.com/blogs/2008/08/custom-flex-3-lightweight-preloader-with-source-code/ .
 *
 * @author Julien Nicoulaud
 * @internal The styles and positions are made to fit the application's ones and make sure a smooth
 * graphical transition is made.
 * @see org.ow2.kerneos.core.view.LoadingView
 */
public class KerneosPreloader extends PreloaderDisplayBase
{
    // =========================================================================
    // Properties
    // =========================================================================

    /**
     * The label text displayed the status.
     */
    private var label : TextField;
    
    /**
     * Drop shadow filter.
     */
    private var shadowFilter : DropShadowFilter = new DropShadowFilter(2, 45, 0x000000, 0.5);
    
    /**
     * The loading bar.
     */
    private var bar : Sprite = new Sprite();
    
    /**
     * The loading bar frame.
     */
    private var barFrame : Sprite;
    
    /**
     * The main color of the UI.
     */
    private var mainColor : uint = 0x666666;
    
    
    // =========================================================================
    // Initialization
    // =========================================================================
    
    /**
     * Build a new KerneosPreloader.
     */
    public function KerneosPreloader()
    {
        super();
    }
    
    
    
    /**
     * @inheritDoc
     */
    override public function initialize() : void
    {
        // Call super class method.
        super.initialize();
        
        // Create the background.
        createBackground();
        
        // Create the UI children.
        createUIChildren();
    }
    
    
    
    /**
     * Create the progress bar and the displayed label.
     */
    protected function createUIChildren() : void
    {
        // Create the progress bar.
        bar = new Sprite();
        bar.graphics.drawRoundRectComplex(0, 0, 298, 18, 12, 0, 0, 12);
        bar.x = stageWidth / 2 - bar.width / 2 + 1;
        bar.y = stageHeight / 2 - bar.height / 2 + 14;
        bar.filters = [shadowFilter];
        addChild(bar);
        
        barFrame = new Sprite();
        barFrame.graphics.lineStyle(2, 0xFFFFFF, 1)
        barFrame.graphics.drawRoundRectComplex(0, 0, 298, 18, 12, 0, 0, 12);
        barFrame.graphics.endFill();
        barFrame.x = stageWidth / 2 - barFrame.width / 2 + 1;
        barFrame.y = stageHeight / 2 - barFrame.height / 2 + 14;
        barFrame.filters = [shadowFilter];
        addChild(barFrame);
        
        // Create the displayed label.
        label = new TextField();
        label.width = 400;
        label.x = barFrame.x;
        label.y = barFrame.y - 25;
        label.filters = [shadowFilter];
        addChild(label);
        
        // Format the label.
        var s : TextFormat = new TextFormat("Verdana", "12", 0xEFEFEF, true, false, null, null, null, "left");
        label.defaultTextFormat = s;
    }
    
    
    
    /**
     * Create the background.
     */
    protected function createBackground() : void
    {
        // Create a gradient background (white => mainColor)
        var b : Sprite = new Sprite;
        var matrix : Matrix = new Matrix();
        matrix.createGradientBox(stageWidth, stageHeight, Math.PI / 2);
        b.graphics.beginGradientFill(GradientType.LINEAR, [0xFFFFFF, mainColor], [1, 1], [0, 255], matrix);
        b.graphics.drawRect(0, 0, stageWidth, stageHeight);
        b.graphics.endFill();
        addChild(b);
    }
    
    
    // =========================================================================
    // UI updates
    // =========================================================================
    
    /**
     * @inheritDoc
     */
    override protected function draw() : void
    {
        // Update the displayed label.
        label.text = "Downloading... " + int(_fractionLoaded * 100).toString() + "% (" + Math.ceil((_bytesLoaded / 1024)).toString() + "/" + Math.ceil((_bytesExpected / 1024)).toString() + "kb)";
        
        // Update the progress bar.
        bar.graphics.beginFill(mainColor, 1)
        bar.graphics.drawRoundRectComplex(0, 0, bar.width * _fractionLoaded, 20, 12, 0, 0, 12);
        bar.graphics.endFill();
    }

}
}
