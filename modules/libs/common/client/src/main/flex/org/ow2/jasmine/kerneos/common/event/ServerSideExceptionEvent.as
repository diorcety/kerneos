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
import com.adobe.cairngorm.control.CairngormEvent;

import flash.events.Event;

import org.ow2.jasmine.kerneos.common.view.ServerSideException;


/**
 * A Cairngorm Event that carries informations about a server
 * side thrown exception
 * 
 * @author Julien Nicoulaud
 */
public class ServerSideExceptionEvent extends CairngormEvent
{

    // =========================================================================
    // Variables
    // =========================================================================
    
    // Event types
    
    /**
    * Default server side exception event type
    */
    public static var SERVER_SIDE_EXCEPTION:String = "serverSideException";
    
    
    // Fields
    
    /**
    * A short statement that describes the exception
    */
    [Bindable]
    public var exception : ServerSideException;
        
    /**
    * A function to execute when the exception occurs. 
    */
    private var funcObj : Function = null;


    // =========================================================================
    // Initialization
    // =========================================================================
    
    /**
    * Create a new ServerSideExceptionEvent
    */
    public function ServerSideExceptionEvent(type:String,
                                             exception:ServerSideException,
                                             func:Function=null)
    {
        super(type);
        this.exception = exception;
        if (func != null) {
            this.funcObj = func;
        }
    }
    
    
    // =========================================================================
    // Utils
    // =========================================================================
    
    /**
    * Clone a ServerSideExceptionEvent
    */
    override public function clone() : Event
    {
        return new ServerSideExceptionEvent(this.type,
                                            this.exception);
    }
    
    /**
    * Shows the exception in a window
    */
    public function show():void
    {
        if (this.funcObj != null) {    
            this.funcObj.call(this);
        }        
        this.exception.show();
    }
        
    /**
    * Show a given exception in a window
    */
    public static function show(exceptionEvent:ServerSideExceptionEvent):void
    {
        // Stop the event propagation
        exceptionEvent.stopImmediatePropagation();
        exceptionEvent.preventDefault();
        
        // Show it
        exceptionEvent.show();
        
    }
    
}
}
