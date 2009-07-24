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
package org.ow2.jasmine.kerneos.common.event
{

import flash.events.Event;

/**
* An event that can be dispatched by modules to let Kerneos notify the user.
* 
* @author Julien Nicoulaud
*/
public class KerneosNotificationEvent extends Event
{
    // =========================================================================
    // Properties
    // =========================================================================

    /**
    * Message allowed types
    */
    public static var KERNEOS_NOTIFICATION:String = "KerneosNotification";
    
    /**
    * The message carried by the event
    */
    [Bindable]
    public var message : String;
    
    /**
    * Message allowed levels
    */
    public static var INFO:String = "Info";
    public static var ALERT:String = "Alert";
    
    /**
    * The level of the message
    */
    [Bindable]
    public var level : String;
    
    /**
    * Wether the message should be displayed as a popup
    */
    [Bindable]
    public var showPopup : Boolean;


    // =========================================================================
    // Constructor
    // =========================================================================
                
    /**
    * Build a new Kerneos notification event
    */
    public function KerneosNotificationEvent(message:String,
                                             level:String="Info",
                                             showPopup:Boolean=true)
    {
        // Call super class constructor
        super(KERNEOS_NOTIFICATION,true);
        
        // Assign variables
        this.message = message;
        this.level = level;
        this.showPopup = showPopup;
    }

}
}
