/**
 * JASMINe
 * Copyright (C) 2008 Bull S.A.S.
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

package org.ow2.jasmine.kerneos.login.event
{
import com.adobe.cairngorm.control.CairngormEvent;

import flash.events.Event;

/**
 * LogInEvent class. This is the link between the view layer and the command layer
 * @author Guillaume Renault
 */
public class LogInEvent extends CairngormEvent {

    /**
    * type of the event.
    */
    public static var LOG_IN:String = "Log in";

    private var userObj:String = "";

    private var passwordObj:String = "";

    /**
     * Create a new instance, and call the super.
     *
     * @param type String to put in the event.
     */
    public function LogInEvent(type:String) {
        super( type );
    }

    /**
     * Override the inherited clone() method, but don't return any state.
     *
     * @return this event
     */
    override public function clone() : Event {
        return new LogInEvent(this.type);
    }

    // ////////////////////////////////////////////////////////////////////////
    // setters and getters
    // ////////////////////////////////////////////////////////////////////////

    public function set user(you:String):void {
        this.userObj = you;
    }

    public function get user():String {
        return this.userObj;
    }

    public function set password(pass:String):void {
        this.passwordObj = pass;
    }

    public function get password():String {
        return this.passwordObj;
    }

}
}
