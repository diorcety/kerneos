/**
 * Copyright (c) 2007 Ben Stucki
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * -------------------------------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------------------------------
 */
package org.ow2.jasmine.kerneos.common.util
{
import flash.display.BitmapData;
import flash.display.Loader;
import flash.display.LoaderInfo;
import flash.errors.IOError;
import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.geom.Matrix;
import flash.net.URLRequest;
import flash.system.LoaderContext;
import flash.utils.Dictionary;

import mx.containers.accordionClasses.AccordionHeader;
import mx.controls.tabBarClasses.Tab;
import mx.core.BitmapAsset;
import mx.core.UIComponent;


/**
 * Provides a workaround for using run-time loaded graphics in styles and properties which
 * require a Class reference.
 *
 * @author Ben Stucki
 * @author Julien Nicoulaud
 */
public class IconUtility extends BitmapAsset
{
    /**
     * The association UIComponent => Loader.
     */
    private static var dictionary : Dictionary;
    
    /**
     * The association URL => Loader.
     */
    private static var dictionaryByURL : Dictionary;
    
    
    
    /**
     * Used to associate run-time graphics with a target.
     *
     * @param target A reference to the component associated with this icon.
     * @param source A url to a JPG, PNG or GIF file you wish to be loaded and displayed.
     * @param width Defines the width of the graphic when displayed.
     * @param height Defines the height of the graphic when displayed.
     * @return A reference to the IconUtility class which may be treated as a BitmapAsset.
     * @example How to use it:
     * <listing version="3.0">
     * &lt;mx:Button id="button" icon="{IconUtility.getClass(button, 'http://www.yourdomain.com/images/test.jpg')}" /&gt;
     * </listing>
     */
    public static function getClass(target : UIComponent, source : String, width : Number = NaN, height : Number = NaN) : Class
    {
        // Initialize the dictionnaries if needed.
        if (!dictionary)
        {
            dictionary = new Dictionary(false);
        }
        
        if (!dictionaryByURL)
        {
            dictionaryByURL = new Dictionary(false);
        }
        
        // Prepare to create or locate a Loader for the asset.        
        var loader : Loader;
        
        // If a Loader is already associated to this URL, choose it.
        if (dictionaryByURL[source] != null)
        {
            loader = dictionaryByURL[source];
        }
        
        // Else
        else
        {
            // Create a new one.
            loader = new Loader();
            
            try
            {
                // Start loading
                loader.load(new URLRequest(source as String), new LoaderContext(true));
                
                // Store the reference to the new Loader.
                dictionaryByURL[source] = loader;
            }
            catch (e : IOError)
            {
                // Do nothing
            }
        }
        
        // Store the selected Loader for the UIComponent.
        dictionary[target] = {source: loader, width: width, height: height};
        
        // Return this
        return IconUtility;
    }
    
    
    
    /**
     * Build a new IconUtility.
     */
    public function IconUtility() : void
    {
        // Wait to be added to the stage.
        addEventListener(Event.ADDED, addedHandler, false, 0, true);
    }
    
    
    
    /**
     * Triggered when this class is added to the stage.
     */
    private function addedHandler(event : Event) : void
    {
        // Resolve the parent component to serve the data to.
        if (parent)
        {
            if (parent is AccordionHeader)
            {
                var header : AccordionHeader = parent as AccordionHeader;
                getData(header.data);
            }
            else if (parent is Tab)
            {
                var tab : Tab = parent as Tab;
                getData(tab.data);
            }
            else
            {
                getData(parent);
            }
        }
    }
    
    
    
    /**
     * Get data bytes for an Object.
     */
    private function getData(object : Object) : void
    {
        // Get the stored data for this Object.
        var data : Object = dictionary[object];
        
        if (data)
        {
            // Get the loader.
            var source : Object = data.source;
            
            // Init a BitmapData with the specified width and height if specified.
            if (data.width > 0 && data.height > 0)
            {
                bitmapData = new BitmapData(data.width, data.height, true, 0x00FFFFFF);
            }
            
            if (source is Loader)
            {
                var loader : Loader = source as Loader;
                
                // If loader not ready, wait for the completion.
                if (!loader.content)
                {
                    loader.contentLoaderInfo.addEventListener(Event.COMPLETE, completeHandler, false, 0, true);
                }
                
                // Else directly display it.
                else
                {
                    displayLoader(loader);
                }
            }
        }
    }
    
    
    
    /**
     * Triggered when the Loader has finished loading.
     */
    private function completeHandler(event : Event) : void
    {
        if (event && event.target && event.target is LoaderInfo)
        {
            // Display the loaded content.
            displayLoader(event.target.loader as Loader);
        }
    }
    
    
    
    /**
     * Display a Loader's content.
     */
    private function displayLoader(loader : Loader) : void
    {
        // If the Bitmap data is no initialized, do it now.
        if (!bitmapData)
        {
            bitmapData = new BitmapData(loader.content.width, loader.content.height, true, 0x00FFFFFF);
        }
        
        // Draw the loader contents to the Bitmap data.
        bitmapData.draw(loader, new Matrix(bitmapData.width / loader.width, 0, 0, bitmapData.height / loader.height, 0, 0));
        
        // Force the parent component to recalculate its size.
        if (parent is UIComponent)
        {
            var component : UIComponent = parent as UIComponent;
            component.invalidateSize();
        }
    }

}
}
