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

import flash.display.Bitmap;

import flash.utils.ByteArray;

[Bindable]
    public class ModuleImageVO implements IValueObject
    {

        private var _idModule:String;
        private var _imgOrig:ByteArray;

        [Transient]
        private var _image:Bitmap;


        public function ModuleImageVO()
        {
        }

        public function get idModule():String
        {
            return _idModule;
        }

        public function set idModule(idModule:String):void
        {
            this._idModule = idModule;
        }

        public function get imgOrig():ByteArray
        {
            return _imgOrig;
        }

        public function set imgOrig(_imgOrig:ByteArray):void
        {
            this._imgOrig = _imgOrig;
        }

        public function get image():Bitmap
        {
            return _image;
        }

        public function set image(_image:Bitmap):void
        {
            this._image = _image;
        }

    }

}