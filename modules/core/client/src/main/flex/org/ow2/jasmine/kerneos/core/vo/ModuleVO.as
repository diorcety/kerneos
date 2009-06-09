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
 * $Id$
 */
 
package org.ow2.jasmine.kerneos.core.vo
{
import com.adobe.cairngorm.vo.IValueObject;

import mx.collections.ArrayCollection;

[RemoteClass(alias="org.ow2.jasmine.kerneos.service.Module")]
[Bindable]
public class ModuleVO implements IValueObject
{
	public function ModuleVO()
	{
	}

	private var urlValue : String = null;
	private var swfFileValue : String = null;
	private var loadedValue : Boolean = false;
	private var nameValue : String = null;
	private var descriptionValue : String = null;
	
	private var servicesValue : ArrayCollection = null;

	/*
	 *	url
	 */
	public function set url(_url:String) : void {
		this.urlValue = _url;
	}
	
	public function get url() : String {
		return this.urlValue;
	}

	/*
	 *	swfFile
	 */
	public function set swfFile(_swfFile:String) : void {
		this.swfFileValue = _swfFile;
	}
	
	public function get swfFile() : String {
		return this.swfFileValue;
	}

	/*
	 *	Services
	 */
	public function set loaded(_loaded:Boolean) : void {
		this.loadedValue = _loaded;
	}
	
	public function get loaded() : Boolean {
		return this.loadedValue;
	}
	
	/*
	 *	Name
	 */
	
	public function set name(_name:String) : void {
		this.nameValue = _name;
	}
	
	public function get name() : String {
		return this.nameValue;
	}
	
	/*
	 *	Description
	 */
	public function set description(_description:String) : void {
		this.descriptionValue = _description;
	}
	
	public function get description() : String {
		return this.descriptionValue;
	}
	
	
	/*
	 *	Services
	 */
	public function set services(_services:ArrayCollection) : void {
		this.servicesValue = _services;
	}
	
	public function get services() : ArrayCollection {
		return this.servicesValue;
	}

}
}