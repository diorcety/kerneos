/**
 * Kerneos
 * Copyright (C) 2011 Bull S.A.S.
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
 */
package org.ow2.kerneos.modules.store.event {
import flash.events.Event;
import com.adobe.cairngorm.control.CairngormEvent;

public class StoreEvent extends CairngormEvent {

    public static var GET_STORE : String = "GET_STORE";


    private var _url : String = "";

    public function StoreEvent(type : String)
    {
        super(type);
    }

    public function set url(url:String):void
    {
        this._url = url;
    }

    public function get url(): String
    {
        return _url;
    }

    /**
     * Overrides the clone function of the CairngormEvent class.
     * returns a new ModuleEvent
     */
    override public function clone() : Event
    {
        var ev:StoreEvent = new StoreEvent(this.type);

         ev.url = this._url;

        return ev;
    }
}
}
