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
package org.ow2.jasmine.kerneos.core.util
{
import flash.events.NetStatusEvent;
import flash.net.SharedObject;
import flash.net.SharedObjectFlushStatus;

import org.ow2.jasmine.kerneos.common.util.StringUtils;


/**
 * Manages the Flash player cookie (shared object) to store some user settings.
 *
 * @author Julien Nicoulaud
 */
public class SharedObjectManager
{
    // =========================================================================
    // Properties
    // =========================================================================
    
    /**
     * The name of the shared object.
     */
    private static var SHARED_OBJECT_NAME : String = "kerneosSettings";
    
    /**
     * The shared object storing the user settings.
     */
    private static var _sharedObject : SharedObject;
    
    
    
    // =========================================================================
    // Public static methods
    // =========================================================================
    
    /**
     * Load the user shared object
     */
    public static function load() : void
    {
        // Read the shared object
        _sharedObject = SharedObject.getLocal(SHARED_OBJECT_NAME);
    }
    
    
    
    /**
     * Save the modifications to the user disk
     */
    public static function save() : void
    {
        try
        {
            // Write the data to the user disk
            var status : String = _sharedObject.flush();
            
            // If status is "pending", something went wrong...
            if (status == SharedObjectFlushStatus.PENDING)
            {
                _sharedObject.addEventListener(NetStatusEvent.NET_STATUS, flushStatusHandler);
            }
        }
        catch (error : Error)
        {
            // Do nothing, this not a mandatory feature.
            // Alert.show("You must allow local data storing.");
        }
    }
    
    
    
    /**
     * Store the user chosen language.
     */
    public static function setActiveLanguage(value : String) : void
    {
        if (_sharedObject != null && _sharedObject.data != null)
        {
            _sharedObject.data.activeLanguage = value;
        }
    }
    
    
    
    /**
     * Get the user chosen language.
     *
     * @return null if the shared object does not exist, or chosen language never set or invalid.
     */
    public static function getActiveLanguage() : String
    {
        if (_sharedObject == null || _sharedObject.data == null || _sharedObject.data.count == 0)
        {
            return null;
        }
        
        var activeLanguage : String;
        
        try
        {
            activeLanguage = _sharedObject.data.activeLanguage.toString();
        }
        catch (error : Error)
        {
            return null;
        }
        
        return activeLanguage;
    }
    
    
    
    /**
     * Store the user size and position for a window.
     */
    public static function setWindowSizeAndPosition(name : String, width : int, height : int, x : int, y : int) : void
    {
        if (_sharedObject != null && _sharedObject.data != null)
        {
            _sharedObject.data[name + "_width"] = width.toString();
            _sharedObject.data[name + "_height"] = height.toString();
            _sharedObject.data[name + "_x"] = x.toString();
            _sharedObject.data[name + "_y"] = y.toString();
        }
    }
    
    
    
    /**
     * Get the user size and position for a window.
     *
     * @return null if the shared object does not exist, or preferences for this window not set
     *              or invalid.
     *         An Object with the following properties else: "width", "height", "x" and "y".
     */
    public static function getWindowSizeAndPosition(name : String) : Object
    {
        if (_sharedObject == null || _sharedObject.data == null || _sharedObject.data.count == 0)
        {
            return null;
        }
        
        var windowPlacement : Object = new Object();
        
        try
        {
            windowPlacement.width = parseInt(_sharedObject.data[name + "_width"].toString());
            windowPlacement.height = parseInt(_sharedObject.data[name + "_height"].toString());
            windowPlacement.x = parseInt(_sharedObject.data[name + "_x"].toString());
            windowPlacement.y = parseInt(_sharedObject.data[name + "_y"].toString());
        }
        catch (error : Error)
        {
            return null;
        }
        
        if (isNaN(windowPlacement.width as int) || isNaN(windowPlacement.height as int) || isNaN(windowPlacement.x as int) || isNaN(windowPlacement.y as int))
        {
            return null;
        }
        
        return windowPlacement;
    }
    
    
    
    /**
     * Store wether has maximized the window.
     */
    public static function setWindowIsMaximized(name : String, maximized : Boolean) : void
    {
        if (_sharedObject != null && _sharedObject.data != null)
        {
            _sharedObject.data[name + "_maximized"] = maximized.toString();
        }
    }
    
    
    
    /**
     * Check wether the user has maximized the window.
     *
     * @return null if the shared object does not exist, or preferences for this window not set
     *              or invalid.
     */
    public static function getWindowIsMaximized(name : String) : Boolean
    {
        if (_sharedObject == null || _sharedObject.data == null || _sharedObject.data.count == 0)
        {
            return null;
        }
        
        var windowsIsMaximized : Boolean;
        
        try
        {
            windowsIsMaximized = StringUtils.parseBoolean(_sharedObject.data[name + "_maximized"].toString());
        }
        catch (error : Error)
        {
            return null;
        }
        
        return windowsIsMaximized;
    }
    
    
    
    // =========================================================================
    // Private methods
    // =========================================================================
    
    /**
     * Handles the "flush" operation status updates.
     */
    private static function flushStatusHandler(event : NetStatusEvent) : void
    {
        // Remove the listener
        event.target.removeEventListener(NetStatusEvent.NET_STATUS, flushStatusHandler);
        
        // If the status is failed
        if (event.info.code == "SharedObject.Flush.Failed")
        {
            // Do nothing, this not a mandatory feature.
            // Alert.show("You must allow local data storing.");
        }
    }
}
}
