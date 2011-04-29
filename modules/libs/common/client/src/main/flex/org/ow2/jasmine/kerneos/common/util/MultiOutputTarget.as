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
import flash.external.ExternalInterface;

import mx.logging.LogEvent;
import mx.logging.targets.LineFormattedTarget;

/**
* A target for a logger that outputs the messages to two endpoints:
*  - the classic console with trace()
*  - Javascript console with calls through ExternalInterface (the Javascript
*    console can be read with browser plugins such as FireBug)
* 
* @author Julien Nicoulaud
* @see KerneosLogger
*/
public class MultiOutputTarget extends LineFormattedTarget
{

    // =========================================================================
    // Variables
    // =========================================================================
    
    /**
    * The log levels and associated markers
    */
    private var _levels:Array;
    
    /**
    * The names of the JS methods to call to access the Javascript console
    */
    private var _consoleMethods:Array;
    
    /**
    * The level filter of the target. If false, "debug" level messages are not
    * logged.
    */
    private var _debug:Boolean = false;
    

    // =========================================================================
    // Constructor
    // =========================================================================
        
    /**
     * Build a new MultiOutputTarget for a logger
     */        
    public function MultiOutputTarget()
    {
        // Call super class constructor
        super();
        
        // Build the messages levels array
        _levels = new Array() ;
        _levels[0] =    "[ALL]" ;
        _levels[2] =    "[DEBUG]" ;
        _levels[4] =    "[INFO]" ;
        _levels[6] =    "[WARNING]" ;
        _levels[8] =    "[ERROR]" ;
        _levels[1000] = "[FATAL]" ;
        
        // Build the JS method names array
        _consoleMethods = new Array() ;
        _consoleMethods[0] =    "console.log" ;
        _consoleMethods[2] =    "console.debug" ;
        _consoleMethods[4] =    "console.info" ;
        _consoleMethods[6] =    "console.warn" ;
        _consoleMethods[8] =    "console.error" ;
        _consoleMethods[1000] = "console.error" ;
        
    }
    
    
    // =========================================================================
    // Getters & Setters
    // =========================================================================
    
    /**
    * Get the debug mode state
    */
    public function get debugMode():Boolean
    {
        return _debug ;
    }
    
    /**
    * Set the debug mode state
    */
    public function set debugMode(value:Boolean):void
    {
        _debug = value ;
    }    
    
    
    // =========================================================================
    // Logging events handlers
    // =========================================================================
        
    /**
     * Event handler that is called by the logging API -- not to be called directly.
     * @param event
     */        
    public override function logEvent(event:LogEvent):void
    {
        // Do not log if not in debug mode
        if(!_debug && event.level < 4)
        {
            return ;
        }
        
        // Acknowledge the event
        event.preventDefault();
        event.stopImmediatePropagation();
        event.stopPropagation();
        
        // Write the message to the two targets
        writeToFlashConsole(event) ;
        writeToJavaScriptConsole(event) ;
    }
    
    
    // =========================================================================
    // Endpoints message writers
    // =========================================================================
    
    /**
     * Trace to message to the Flex console (with time stamp)
     */
    private function writeToFlashConsole(event:LogEvent):void
    {
        trace(_levels[event.level].toUpperCase() + " " + formatUTCTime() + " - " + event.message );
    }
    
    /**
     * Trace to message to the Javascript console (without time stamp)
     */
    private function writeToJavaScriptConsole(event:LogEvent):void
    {
        ExternalInterface.call(_consoleMethods[event.level], _levels[event.level].toUpperCase() + " " + event.message ) ;
    }
    
    
    // =========================================================================
    // Utils
    // =========================================================================
        
    /**
     * Get the current UTC Time ;
     * @return The date and time in UTC long format. 
     */        
    private function formatUTCTime():String
    {
        var d:Date = new Date() ;
        return d.toUTCString() 
    }

}
}
