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
package org.ow2.jasmine.kerneos.core.vo
{

/**
* A module notification
* 
* @author Julien Nicoulaud
*/
public class KerneosNotification
{
    // =========================================================================
    // Properties
    // =========================================================================
        
    /**
    * The module sending the event
    */
    [Bindable]
    public var module : ModuleVO;
    
    /**
    * The message carried by the event
    */
    [Bindable]
    public var message : String;
    
    /**
    * Message allowed levels
    */
    public static var DEBUG:String = "Debug";
    public static var WARNING:String = "Warning";
    public static var INFO:String = "Info";
    public static var ERROR:String = "Error";
    
    /**
    * The level of the message
    */
    [Bindable]
    public var level : String;
    
    /**
    * The emission date of the message
    */
    [Bindable]
    public var date : Date;
    
    
    // =========================================================================
    // Constructor
    // =========================================================================
    
    /**
    * 
    */
    public function KerneosNotification(module:ModuleVO,
                                        message:String,
                                        level:String)
    {
        this.module = module;
        this.message = message;
        this.level = level;
        this.date = new Date();
    }
    
    
    // =========================================================================
    // Utils
    // =========================================================================
        
    /**
    * 
    */
    public function toString():String
    {
        return "[" + level.toUpperCase() + "] " + module.name + ": " + message;
    }
}
}
