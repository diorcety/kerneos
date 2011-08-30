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
    import mx.collections.ArrayCollection;

	[RemoteClass(alias="org.ow2.kerneos.modules.store.impl.ModuleImpl")]
    [Bindable]
    public class ModuleVO implements IValueObject
	{

        private var _id:String;
		/**
		 * Module's name
		 */
		private var _name:String;
		
		/**
		 * Module's creator or author
		 */
		private var _author:String;
		
		/**
		 * Icon, 128 x 200 dimensions 
		 */
        [Transient]
		private var _icon:Class;
		
		/**
		 * Module's version
		 */
		private var _version:String;
		
		/**
		 * Module's description, not more than xxx letters  
		 */
		private var _description:String;
		
		/**
		 * Module's release date
		 */
		private var _date:Date;
		
		/**
		 * Size in Bytes of the module
		 */
        [Transient]
		private var _size:int;
		
		/**
		 * Category of the module
		 */
        [Transient]
		private var _category:String;
		
		/**
		 * Number of installs made
		 */
        [Transient]
		private var _installsNumber:int;

        /**
         * Url of the method
         */
        private var _url:String;

        [ArrayElementType('org.ow2.kerneos.modules.store.vo.CategoryVO')]
        private var _categories:ArrayCollection;
		
		/**
		 * It is the module selected in the datagrid
		 */
        [Transient]
		private var _selected:Boolean;

		
		
		public function ModuleVO()
		{
		}

        public function get id():String
        {
            return _id;
        }

        public function set id(id:String):void
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

		public function get author():String
		{
			return _author;
		}

		public function set author(value:String):void
		{
			_author = value;
		}

		public function get icon():Class
		{
			return _icon;
		}

		public function set icon(value:Class):void
		{
			_icon = value;
		}

		public function get version():String
		{
			return _version;
		}

		public function set version(value:String):void
		{
			_version = value;
		}

		public function get description():String
		{
			return _description;
		}

		public function set description(value:String):void
		{
			_description = value;
		}

		public function get date():Date
		{
			return _date;
		}

		public function set date(value:Date):void
		{
			_date = value;
		}

		public function get size():int
		{
			return _size;
		}

		public function set size(value:int):void
		{
			_size = value;
		}

		public function get category():String
		{
			return _category;
		}

		public function set category(value:String):void
		{
			_category = value;
		}

		public function get installsNumber():int
		{
			return _installsNumber;
		}

		public function set installsNumber(value:int):void
		{
			_installsNumber = value;
		}

		public function get selected():Boolean
		{
			return _selected;
		}

		public function set selected(value:Boolean):void
		{
			_selected = value;
		}

        public function get url():String
        {
         return _url;
        }

        public function set url(url:String):void
        {
            this._url = url;
        }

        public function get categories():ArrayCollection
        {
            return this._categories;
        }

        public function set categories(categories:ArrayCollection):void
        {
            this._categories = categories;
        }

	}
	
}