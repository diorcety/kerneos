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

/**
* Describes the configuration of a Kerneos module
* 
* @ author Guillaume Renault, Julien Nicoulaud
*/
[RemoteClass(alias="org.ow2.jasmine.kerneos.service.Module")]
[Bindable]
public class ModuleVO implements IValueObject
{
    /**
    * The web page URL (for IFrame modules)
    */
	public var url : String = null;
	
	/**
	* The SWF file path (for SWF modules)
	*/
	public var swfFile : String = null;
	
	/**
	* The module services (for SWF modules)
	*/
    public var services : ArrayCollection = null;
    
    /**
    * The displayed name of the module
    */
	public var name : String = null;
	
	/**
	* The description of the module
	*/
	public var description : String = null;
	
	/**
	* The default small icon (16x16) path
	*/
    public var defaultSmallIcon : String = "resources/icons/module16.png";
    
    /**
    * The small icon (16x16) path
    */
    public var smallIcon : String = defaultSmallIcon;
    
    /**
    * The default big icon (64x64) path
    */
    public var defaultBigIcon : String = "resources/icons/module64.png";
    
    /**
    * The big icon (64x64) path
    */
    public var bigIcon : String = defaultBigIcon;
    
    /**
    * Load the module on application startup
    */
    public var loadOnStartup : Boolean = false;
    
    /**
    * Load the module maximized
    */
    public var loadMaximized : Boolean = false;
    
    /**
    * The current state of the module
    */
    [Transient]
    public var loaded : Boolean = false;
    
    /**
    * Ouput the module name
    */
    public function toString():String
    {
        return name;
    }
}
}
