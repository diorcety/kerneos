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

import org.ow2.kerneos.modules.store.vo.ModuleVO;

public class ChangeStoreStateEvent extends CairngormEvent {

    public static var CHANGE_MODULE_STATE:String = "CHANGE_MODULE_STATE";
    public static var CHANGE_MODULE_STATE_DETAIL:String = "CHANGE_MODULE_STATE_DETAIL";

    private var _state:String = "";
    private var _moduleDetail:ModuleVO = null;

    public function ChangeStoreStateEvent(type:String) {
        super(type);
    }

    public function set state(state:String):void {
        this._state = state;
    }

    public function get state():String {
        return _state;
    }

    public function set moduleDetail(moduleDetail:ModuleVO):void {
        this._moduleDetail = moduleDetail;
    }

    public function get moduleDetail():ModuleVO {
        return _moduleDetail;
    }

    /**
     * Overrides the clone function of the CairngormEvent class.
     * returns a new ModuleEvent
     */
    override public function clone():Event {
        var ev:ChangeStoreStateEvent = new ChangeStoreStateEvent(this.type);

        ev.state = this._state;
        ev.moduleDetail = this._moduleDetail;

        return ev;
    }
}
}
