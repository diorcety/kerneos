/*********************************************************************************************************************************

 Copyright (c) 2007 Ben Stucki

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 ------------

 2009, two years later:

 some minor changes to the class so it provides displaying any DisplayObject.
 thanks to ben for sharing his code!

 changes by kris (http://krisrok.de)


 *********************************************************************************************************************************/
package org.ow2.kerneos.modules.store.util {
import flash.display.BitmapData;
import flash.display.DisplayObject;
import flash.display.Loader;
import flash.display.LoaderInfo;
import flash.display.Sprite;
import flash.events.Event;
import flash.geom.Matrix;
import flash.geom.Rectangle;
import flash.net.URLRequest;
import flash.system.LoaderContext;
import flash.utils.Dictionary;

import mx.containers.accordionClasses.AccordionHeader;
import mx.controls.tabBarClasses.Tab;
import mx.core.BitmapAsset;
import mx.core.UIComponent;

/**
 * Provides a workaround for using run-time loaded and created graphics in styles and properties which require a Class reference
 */
public class IconUtilityPlus extends BitmapAsset {

    private static var dictionary:Dictionary;
    private var _animated:Boolean;
    private var _displayObject:DisplayObject;

    /**
     * Used to associate run-time graphics with a target
     * @param target A reference to the component associated with this icon
     * @param source Any DisplayObject or a url to a JPG, PNG or GIF file you wish to be loaded and displayed
     * @param width Defines the width of the graphic when displayed
     * @param height Defines the height of the graphic when displayed
     * @return A reference to the IconUtility class which may be treated as a BitmapAsset
     * @example &lt;mx:Button id="button" icon="{IconUtility.getClass(button, 'http://www.yourdomain.com/images/test.jpg')}" /&gt;
     */
    public static function getClass(target:UIComponent, source:Object, animated:Boolean = false, width:Number = -1, height:Number = -1):Class {

        var displayObject:DisplayObject;

        if (!dictionary) {
            dictionary = new Dictionary(false);
        }
        if (source is String) {
            var loader:Loader = new Loader();
            loader.load(new URLRequest(source as String), new LoaderContext(true));
            displayObject = loader;
        }
        else {
            if (source is DisplayObject)
                displayObject = source as DisplayObject;
        }
        dictionary[target] = { source:displayObject, width:width, height:height, animated:animated };
        return IconUtilityPlus;
    }

    /**
     * @private
     */
    public function IconUtilityPlus():void {
        addEventListener(Event.ADDED, addedHandler, false, 0, true)
    }

    private function addedHandler(event:Event):void {
        if (parent) {
            if (parent is AccordionHeader) {
                var header:AccordionHeader = parent as AccordionHeader;
                getData(header.data);
            } else if (parent is Tab) {
                var tab:Tab = parent as Tab;
                getData(tab.data);
            } else {
                getData(parent);
            }
        }
    }

    private function getData(object:Object):void {

        var data:Object = dictionary[object];

        _animated = data.animated;

        var source:DisplayObject = data.source;

        if (data.width > 0 && data.height > 0) {
            bitmapData = new BitmapData(data.width, data.height, true, 0x00FFFFFF);
        }

        if (source is Loader) {
            var loader:Loader = source as Loader;
            if (!loader.content) {
                loader.contentLoaderInfo.addEventListener(Event.COMPLETE, completeHandler, false, 0, true);
            } else {
                _displayObject = loader.content;
                startDisplaySource();
            }
        }
        else {
            if (source is DisplayObject) {
                _displayObject = source;
                startDisplaySource();
            }
        }

    }

    private function startDisplaySource():void {
        if (_animated) {
            addEventListener(Event.ENTER_FRAME, onEnterFrame);
            addEventListener(Event.REMOVED_FROM_STAGE, onRemoveFromStage);
        }
        else {
            displaySource();
        }
    }

    private function onRemoveFromStage(e:Event):void {
        removeEventListener(Event.ENTER_FRAME, onEnterFrame);
        removeEventListener(Event.REMOVED_FROM_STAGE, onRemoveFromStage);
    }

    private function onEnterFrame(e:Event):void {
        if (_animated)
            displaySource();
    }

    private function displaySource():void {

        if (!bitmapData) {
            bitmapData = new BitmapData(_displayObject.width, _displayObject.height, true, 0x00FFFFFF);
        }

        var bounds:Rectangle = _displayObject.getBounds(_displayObject);
        bitmapData.draw(_displayObject, new Matrix(bitmapData.width / bounds.width, 0, 0, bitmapData.height / bounds.height, -bounds.x * (bitmapData.width / bounds.width), -bounds.y * (bitmapData.height / bounds.height)));

        if (parent is UIComponent) {
            var component:UIComponent = parent as UIComponent;
            component.invalidateSize();
        }
    }

    private function completeHandler(event:Event):void {
        if (event && event.target && event.target is LoaderInfo) {
            LoaderInfo(event.target).removeEventListener(Event.COMPLETE, completeHandler);
            _displayObject = LoaderInfo(event.target).content;
            startDisplaySource();
        }
    }

    public function get animated():Boolean {
        return _animated;
    }

    public function set animated(value:Boolean):void {
        _animated = value;
    }

}
}
