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
 * $Id$
 */
package org.ow2.kerneos.modules.store.vo
{
    import com.adobe.cairngorm.vo.IValueObject;

    [RemoteClass(alias="org.ow2.kerneos.modules.store.impl.CategoryImpl")]
    [Bindable]
    public class CategoryVO implements IValueObject
    {

        private var _id:Number;
        private var _name:String;
        private var _description:String;


        public function CategoryVO()
        {
        }

        public function get id():Number
        {
            return _id;
        }

        public function set id(id:Number):void
        {
            this._id = id;
        }

        public function get name():String
        {
            return _name;
        }

        public function set name(value:String):void
        {
            _name = value;
        }

        public function get description():String
        {
            return _description;
        }

        public function set description(value:String):void
        {
            _description = value;
        }

    }

}