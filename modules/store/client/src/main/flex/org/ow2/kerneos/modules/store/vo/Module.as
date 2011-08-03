package org.ow2.kerneos.modules.store.vo
{
	
	[Bindable]public class Module
	{
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
		private var _releaseDate:Date;
		
		/**
		 * Size in Bytes of the module
		 */
		private var _size:int;
		
		/**
		 * Category of the module
		 */
		private var _category:String;
		
		/**
		 * Number of installs made
		 */
		private var _installsNumber:int;
		
		/**
		 * It is the module selected in the datagrid
		 */
		private var _selected:Boolean;
		
		
		public function Module()
		{
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

		public function get releaseDate():Date
		{
			return _releaseDate;
		}

		public function set releaseDate(value:Date):void
		{
			_releaseDate = value;
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


	}
	
}