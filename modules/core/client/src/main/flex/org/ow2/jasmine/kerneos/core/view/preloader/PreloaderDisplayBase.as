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
package org.ow2.jasmine.kerneos.core.view.preloader
{
import flash.display.Sprite;
import flash.events.Event;
import flash.events.ProgressEvent;
import flash.events.TimerEvent;
import flash.utils.Timer;

import mx.events.FlexEvent;
import mx.preloaders.IPreloaderDisplay;

/**
 * The base mechanism for a custom preloader.
 *
 * Massively inspired from
 * http://www.pathf.com/blogs/2008/08/custom-flex-3-lightweight-preloader-with-source-code/ .
 *
 * @author Julien Nicoulaud
 * @see KerneosPreloader
 */
public class PreloaderDisplayBase extends Sprite implements IPreloaderDisplay
{
    // =========================================================================
    // Properties
    // =========================================================================

    /**
    * Whether the initialization is complete.
    */
    protected var _IsInitComplete : Boolean = false;
    
    /**
    * The timer used to update the UI.
    */
    protected var _timer : Timer;
    
    /**
    * The number of bytes already loaded.
    */
    protected var _bytesLoaded : uint = 0;
    
    /**
    * The number of bytes expected.
    */
    protected var _bytesExpected : uint = 1;
    
    /**
    * The percentage loaded.
    */
    protected var _fractionLoaded : Number = 0;
    
    /**
    * The actual preloader.
    */
    protected var _preloader : Sprite;
    
    /**
    * The background color.
    */
    protected var _backgroundColor : uint = 0xffffffff;

    /**
    * The stage height.
    */
    protected var _stageHeight : Number = 300;

    /**
    * The stage width.
    */
    protected var _stageWidth : Number = 400;


    // =========================================================================
    // Implementation of IPreloaderDisplay
    // =========================================================================
    
    /**
    * Build a new PreloaderDisplayBase.
    */
    public function PreloaderDisplayBase()
    {
        super();
    }
    
    
    
    /**
    * This function is called whenever the state of the preloader changes: to be overriden.
    * 
    * Use the _fractionLoaded variable to draw your progress bar.
    */
    virtual protected function draw() : void
    {
    }
    
    
    
    /**
    * This function is called when the PreloaderDisplayBase has been created and is ready for action.
    */
    virtual public function initialize() : void
    {
        _timer = new Timer(1);
        _timer.addEventListener(TimerEvent.TIMER, timerHandler);
        _timer.start();
    }
    
    /**
     * The Preloader class passes in a reference to itself to the display class
     * so that it can listen for events from the preloader.
     */
    virtual public function set preloader(value : Sprite) : void
    {
        _preloader = value;
        
        value.addEventListener(ProgressEvent.PROGRESS, progressHandler);
        value.addEventListener(Event.COMPLETE, completeHandler);
        
        // value.addEventListener(RSLEvent.RSL_PROGRESS, rslProgressHandler);
        // value.addEventListener(RSLEvent.RSL_COMPLETE, rslCompleteHandler);
        // value.addEventListener(RSLEvent.RSL_ERROR, rslErrorHandler);
        
        value.addEventListener(FlexEvent.INIT_PROGRESS, initProgressHandler);
        value.addEventListener(FlexEvent.INIT_COMPLETE, initCompleteHandler);
    }
    
    
    /**
    * Set the background alpha.
    */
    virtual public function set backgroundAlpha(alpha : Number) : void
    {
    }
    
    
    /**
    * The background alpha.
    */
    virtual public function get backgroundAlpha() : Number
    {
        return 1;
    }
    
    /**
    * Set the background color.
    */
    virtual public function set backgroundColor(color : uint) : void
    {
        _backgroundColor = color;
    }
    
    
    /**
    * The background color.
    */
    virtual public function get backgroundColor() : uint
    {
        return _backgroundColor;
    }
    
    
    /**
    * Set the background image.
    */
    virtual public function set backgroundImage(image : Object) : void
    {
    }
    
    
    /**
    * The background image.
    */
    virtual public function get backgroundImage() : Object
    {
        return null;
    }
    
    
    /**
    * Set the background size.
    */
    virtual public function set backgroundSize(size : String) : void
    {
    }
    
    
    /**
    * The background size.
    */
    virtual public function get backgroundSize() : String
    {
        return "auto";
    }
    
    /**
    * Set the stage height.
    */
    virtual public function set stageHeight(height : Number) : void
    {
        _stageHeight = height;
    }
    
    
    /**
    * The stage height.
    */
    virtual public function get stageHeight() : Number
    {
        return _stageHeight;
    }
    
    
    
    /**
    * Set the stage width.
    */
    virtual public function set stageWidth(width : Number) : void
    {
        _stageWidth = width;
    }
    
    
    /**
    * The stage width.
    */
    virtual public function get stageWidth() : Number
    {
        return _stageWidth;
    }
    
    
    // =========================================================================
    // Event handlers
    // =========================================================================
    
    /**
    * Handler for download progress events.
    */
    virtual protected function progressHandler(event : ProgressEvent) : void
    {
    	// Update the variables.
        _bytesLoaded = event.bytesLoaded;
        _bytesExpected = event.bytesTotal;
        _fractionLoaded = Number(_bytesLoaded) / Number(_bytesExpected);
        
        // Update the UI.
        draw();
    }
    
    
    /**
    * Handler for the download complete event.
    * 
    * Called when the download is complete, but initialization might not be done yet. There are two
    * phases: download and init.
    */
    virtual protected function completeHandler(event : Event) : void
    {
    }
    
    
    /**
    * Handler for initiialization progress events.
    */
    virtual protected function initProgressHandler(event : Event) : void
    {
        draw();
    }
    
    
    
    /**
    * Handler for the initialization complete event.
    * 
    * Called when the initialization is complete, but initialization might not be done yet. There
    * are two phases: download and init.
    */
    virtual protected function initCompleteHandler(event : Event) : void
    {
        _IsInitComplete = true;
    }
    
    
    
    /**
    * Handler for the UI update timer.
    * 
    * Called as often as possible.
    */
    virtual protected function timerHandler(event : Event) : void
    {
        if (_IsInitComplete)
        {
            // Download and initialization are now complete.
            _timer.stop();
            dispatchEvent(new Event(Event.COMPLETE));
        }
        else
        {
        	// Update the UI.
            draw();
        }
    }
}
}
