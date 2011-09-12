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
 * $Id$
 */
package org.ow2.kerneos.modules.store.vo {

import com.adobe.cairngorm.vo.IValueObject;

[Bindable]
public class StoreVO implements IValueObject {

    private var _name:String;
    private var _description:String;
    private var _url:String;
    private var _id:String;


    public function StoreVO() {
    }

    public function get name():String {
        return this._name;
    }

    public function set name(name:String):void {
        this._name = name;
    }

    public function get description():String {
        return this._description;
    }

    public function set description(description:String):void {
        this._description = description;
    }

    public function get url():String {
        return this._url;
    }

    public function set url(url:String):void {
        this._url = url;
    }

    public function get id():String {
        return this._id;
    }

    public function set id(id:String):void {
        this._id = id;
    }
}
}
