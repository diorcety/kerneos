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

public class SearchModulesEvent extends CairngormEvent {

    public static var SEARCH_MODULES : String = "SEARCH_MODULES";

    public static var SEARCH_MODULES_WITH_IMAGE : String = "SEARCH_MODULES_WITH_IMAGE";

    public static var SEARCH_MODULES_BY_CATEGORY : String = "SEARCH_MODULES_BY_CATEGORY";

    public static var SEARCH_MODULES_WITH_IMAGE_BY_CATEGORY : String = "SEARCH_MODULES_WITH_IMAGE_BY_CATEGORY";

    private var _id : String = null;

    private var _filter : String = null;

    private var _field : String = null;

    private var _order : String = null;

    private var _itemByPage : int = -1;

    private var _page : int = -1;

    public function SearchModulesEvent(type : String)
    {
        super(type);
    }

    public function set id(id:String):void
    {
        this._id = id;
    }

    public function get id(): String
    {
        return _id;
    }

    public function set filter(filter:String) : void
    {
        this._filter = filter;
    }

    public function get filter() : String
    {
        return this._filter;
    }

    public function set field(field:String) : void
    {
        this._field = field;
    }

    public function get field() : String
    {
        return this._field;
    }

    public function set order(order:String) : void
    {
        this._order = order;
    }

    public function get order() : String
    {
        return this._order;
    }

    public function set itemByPage(itemByPage:int) : void
    {
        this._itemByPage = itemByPage;
    }

    public function get itemByPage() : int
    {
        return this._itemByPage;
    }

    public function set page(itemByPage:int) : void
    {
        this._page = page;
    }

    public function get page() : int
    {
        return this._page;
    }

    /**
     * Overrides the clone function of the CairngormEvent class.
     * returns a new ModuleEvent
     */
    override public function clone() : Event
    {
        var ev:SearchModulesEvent = new SearchModulesEvent(this.type);

        ev.id = this.id;
        ev.filter = this.filter;
        ev.field = this.field;
        ev.order = this.order;
        ev.itemByPage = this.itemByPage;
        ev.page = this.page;

        return ev;
    }
}
}
