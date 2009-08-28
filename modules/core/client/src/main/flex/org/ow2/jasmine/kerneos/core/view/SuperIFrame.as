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

package org.ow2.jasmine.kerneos.core.view
{

import flash.events.Event;
import flash.external.ExternalInterface;
import flash.geom.Point;

import mx.core.Container;

/**
 * Flex iFrame component. Basically, it creates HTML "div" holes into the page
 * and puts in a iFrame. The iFrame will therefore *ALWAYS* be superimposed on
 * the Flex content.
 */
[Event(name="frameLoad", type="flash.events.Event")]
public class SuperIFrame extends Container {

    // For giving unique IDs to all DIVs and iFrames
    private static var iFrames : int = 0;

    private var url : String;

    private var frameId : String;
    private var iframeId : String;

    /**
     * Here we define javascript functions which will be inserted into the DOM
     */
    private static var FUNCTION_CREATEIFRAME:String =
        "document.insertScript = function ()" +
        "{ " +
            "if (document.createSuperIFrame==null)" +
            "{" +
                "createSuperIFrame = function (frameID, iframeID, url)" +
                "{ " +
                    // "console.info(\"createSuperIFrame \" + frameID);" +
                    "var newDiv = document.createElement('DIV');" +
                    "newDiv.id = frameID;" +
                    "newDiv.style.position ='absolute';" +
                    "newDiv.style.top = '0px';" +
                    "newDiv.style.left = '0px';" +
                    "newDiv.style.width = '0px';" +
                    "newDiv.style.height = '0px';" +
                    "newDiv.style.visibility = 'hidden';" +
                    "newDiv.innerHTML = \"<iframe id='\" + iframeID + \"' src='\" + url + \"' frameborder='0' width='0' height='0'></iframe>\";" +
                    "document.body.appendChild(newDiv);" +
                "}" +
            "}" +
        "}";

    private static var FUNCTION_MOVE_RESIZE_IFRAME:String =
        "document.insertScript = function ()" +
        "{ " +
            "if (document.moveAndResizeSuperIFrame==null)" +
            "{" +
                "moveAndResizeSuperIFrame = function(frameID, iframeID, x, y, w, h) " +
                "{" +
                    // "console.info(\"moveAndResizeSuperIFrame \" + frameID);" +
                    "var frameRef = document.getElementById(frameID);" +
                    "if (frameRef) {" +
                    "    frameRef.style.visibility = 'visible';" +
                    "    frameRef.style.top = parseInt(y) + 'px';" +
                    "    frameRef.style.left = parseInt(x) + 'px';" +
                    "    frameRef.style.width = parseInt(w) + 'px';" +
                    "    frameRef.style.height = parseInt(h) + 'px';" +
                    "}" +
                    "var iFrameRef = document.getElementById(iframeID);" +
                    "if (iFrameRef) {" +
                    "    iFrameRef.width = parseInt(w);" +
                    "    iFrameRef.height = parseInt(h);" +
                    "}" +
                "}" +
            "}" +
        "}";

    private static var FUNCTION_HIDEIFRAME:String =
        "document.insertScript = function ()" +
        "{ " +
            "if (document.hideSuperIFrame==null)" +
            "{" +
                "hideSuperIFrame = function (frameID, iframeID)" +
                "{" +
                    // "console.info(\"hideSuperIFrame \" + frameID);" +
                    "var frameRef = document.getElementById(frameID);" +
                    "if (frameRef) {" +
                    "    frameRef.style.top = '0px';" +
                    "    frameRef.style.left = '0px';" +
                    "    frameRef.style.width = '0px';" +
                    "    frameRef.style.height = '0px';" +
                    "    frameRef.style.visibility = 'hidden';" +
                    "}" +

                    // some browsers have trouble hiding an iFrame even when
                    // its owner DIV is hidden. set the iFrame's width and
                    // height to 0 so they "hide" the iFrame as well.
                    "var iframeRef = document.getElementById(iframeID);" +
                    "if (iFrameRef) {" +
                    "    iFrameRef.width = 0;" +
                    "    iFrameRef.height = 0;" +
                    "}" +
                "}" +
            "}" +
        "}";

    private static var FUNCTION_DESTROYIFRAME:String =
        "document.insertScript = function ()" +
        "{ " +
            "if (document.hideSuperIFrame==null)" +
            "{" +
                "hideSuperIFrame = function (frameID, iframeID)" +
                "{" +
                    // "console.info(\"destroySuperIFrame \" + frameID);" +
                    "var frameRef = document.getElementById(frameID);" +
                    "if (frameRef) {" +
                    "    frameRef.style.width = '0px';" +
                    "    frameRef.style.height = '0px';" +
                    "    frameRef.style.visibility = 'hidden';" +
                    "    frameRef.innerHTML = \"&nbsp;\";" +
                    "    document.body.removeChild(frameRef);" +
                    "}" +
                "}" +
            "}" +
        "}";

    /**
     * Constructor
     */
    public function SuperIFrame(url : String) {
        super();

        if (!ExternalInterface.available) {
            throw new Error("ExternalInterface is not available in this container. Internet Explorer ActiveX, Firefox, Mozilla 1.7.5 and greater, or other browsers that support NPRuntime are required.");
        }

        this.url = url;
        var id : int = ++SuperIFrame.iFrames;
        this.frameId = "superiFrame_div_" + id;
        this.iframeId = "superiFrame_iframe_" + id;

        // Add functions to DOM if they aren't already there
        ExternalInterface.call(FUNCTION_CREATEIFRAME);
        ExternalInterface.call(FUNCTION_MOVE_RESIZE_IFRAME);
        ExternalInterface.call(FUNCTION_HIDEIFRAME);
        ExternalInterface.call(FUNCTION_DESTROYIFRAME);

        this.addEventListener(Event.REMOVED_FROM_STAGE, handleRemove);
        this.addEventListener(Event.ADDED_TO_STAGE, handleAdd);

        // Do not register for visibility change or resize events,
        // the IFrameModuleWindow will call us if necessary
    }

   /**
    * Triggered by removal of this object from the stage
    *
    * @param event Event trigger
    */
    private function handleRemove(event:Event):void {
        ExternalInterface.call("destroySuperIFrame", frameId, iframeId);
    }

   /**
    * Triggered by addition of this object to the stage
    *
    * @param event Event trigger
    */
    private function handleAdd(event:Event):void {
        ExternalInterface.call("createSuperIFrame", frameId, iframeId, url);
    }

    /**
     * Sets visibility of html iframe.
     *
     * @param visible Boolean flag
     */
    public function set iFrameVisible(value: Boolean):void {
        if (value) {
            var localPt:Point = new Point(0, 0);
            var globalPt:Point = this.localToGlobal(localPt);

            ExternalInterface.call("moveAndResizeSuperIFrame", frameId, iframeId, globalPt.x, globalPt.y, this.width, this.height);
        } else {
            ExternalInterface.call("hideSuperIFrame", frameId, iframeId);
        }
    }
}
}
