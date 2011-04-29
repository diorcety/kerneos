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
package org.ow2.jasmine.kerneos.common.util
{

import mx.logging.ILogger;
import mx.logging.Log;

/**
* A logger for the modules that outputs the messages to two endpoints:
*  - The classic console with trace()
*  - Javascript console with calls through ExternalInterface (the Javascript
*    console can be read with browser plugins such as FireBug)
* 
* @example How to use it:
*   <listing version="3.0">
*       import org.ow2.jasmine.kerneos.common.util.KerneosLogger;
* 
*       // How to log messages
*       KerneosLogger.debug('My debug info');
*       KerneosLogger.info('My info');
*       KerneosLogger.warn('My warning');
*       KerneosLogger.error("My error");
*       KerneosLogger.fatal('My fatal error');
*       
*       // How to modify the logger settings
*       KerneosLogger.setDebugMode(true); // If false, debug messages are hidden
*       KerneosLogger.setFilteredMode(true); // If true, displays messages only from
*                                            // org.ow2.* classes. If false, shows
*                                            // all messages
*   </listing>
* 
* @example Tip: show GraniteDS detailed message for each client->server call
*   <listing version="3.0">
*       import org.ow2.jasmine.kerneos.common.util.KerneosLogger;
*
*       KerneosLogger.setDebugMode(true);
*       KerneosLogger.setFilteredMode(false);
*   </listing>
*
* @author Julien Nicoulaud
* @see MultiOutputTarget
*/
public class KerneosLogger extends Object
{

    // =========================================================================
    // Get informations about the String
    // =========================================================================
    
    /**
    * The logger output target
    */
    private static var _target:MultiOutputTarget = null;
    
    /**
    * The Flex logging API object
    */
    private static var _log:ILogger;
    
    /**
    * The default class filter set up
    */
    private static const defaultFilters : Array = ["org.ow2.*"];
    
    
    // =========================================================================
    // Initialization
    // =========================================================================
    
    /**
    * KerneosLogger is a static class and cannot be instantiated
    */
    public function KerneosLogger()
    {
        throw(new Error("The KerneosLogger cannot be instanciated.")) ;
    }
    
    /**
    * Initialize the logger
    */
    private static function initLogger():void
    {
        // Initialize the target if needed
        if(_target == null) {
            
            // Create the target
            _target = new MultiOutputTarget();
            
            // Setup the filters
            _target.filters = defaultFilters;
            
            // Declare the target to the logging API
            Log.addTarget(_target);
            
            // Retrieve our ILogger object
            _log = Log.getLogger("org.ow2") ;
        }
    }
    
    
    // =========================================================================
    // Logger setup
    // =========================================================================
    
    /**
    * Toggle the debug mode
    * 
    * @param debug If false, debug messages are hidden
    */
    public static function setDebugMode(debug:Boolean):void
    {
        // Initialize the logger if needed
        initLogger();
        _target.debugMode = debug;
        if(debug) {
            info("Debug mode enabled") ;
        }
    }
    
    /**
    * Set the filtering mode of the Logger
    * 
    * @param value If true, displays messages only from org.ow2.* classes. If
    * false, shows all messages (especially GraniteDS ones)
    */
    public static function setFilteredMode(value:Boolean):void
    {
        if (value) {
            _target.filters = defaultFilters;
            info("[KerneosLogger] Log filters enabled",
                 "filters: " + defaultFilters.toString());
        } else {
            _target.filters = ["*"];
            info("[KerneosLogger] Log filters disabled");
        }
    }
    
    
    // =========================================================================
    // Logging methods
    // =========================================================================
    
    /**
    * Log the message with level "debug"
    */
    public static function debug(message:String, ... additionalStatements):void
    {
        // Initialize the logger if needed
        initLogger();
        
        // Format the message and log it
        _log.debug(formatMessage(message,additionalStatements));
    }
        
    /**
    * Log the message with level "info"
    */
    public static function info(message:String, ... additionalStatements:Array):void
    {
        // Initialize the logger if needed
        initLogger();
        
        // Format the message and log it
        _log.info(formatMessage(message,additionalStatements));
    }
    
    /**
    * Log the message with level "warning"
    */
    public static function warn(message:String, ... additionalStatements):void
    {
        // Initialize the logger if needed
        initLogger();
        
        // Format the message and log it
        _log.warn(formatMessage(message,additionalStatements));
    }
    
    /**
    * Log the message with level "error"
    */
    public static function error(message:String, ... additionalStatements):void
    {
        // Initialize the logger if needed
        initLogger();
        
        // Format the message and log it
        _log.error(formatMessage(message,additionalStatements));
    }
    
    /**
    * Log the message with level "fatal"
    */
    public static function fatal(message:String, ... additionalStatements):void
    {
        // Initialize the logger if needed
        initLogger();
        
        // Format the message and log it
        _log.fatal(formatMessage(message,additionalStatements));
    }
    
    
    // =========================================================================
    // Private methods
    // =========================================================================
    
    /**
    * Format a message for the consoles
    */
    private static function formatMessage(message:String, ... additionalStatements:Array):String
    {
        message += additionalStatements.length == 0 ? "" : ", " + additionalStatements.join(", ");
        return message;
    }
    
}
}
