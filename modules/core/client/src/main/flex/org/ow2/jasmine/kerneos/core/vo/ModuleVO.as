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

import mx.core.UIComponent;

import org.ow2.jasmine.kerneos.common.util.IconUtility;


/**
 * Describes the configuration of a Kerneos module.
 * 
 * @author Guillaume Renault
 * @author Julien Nicoulaud
 */
[RemoteClass(alias="org.ow2.jasmine.kerneos.service.Module")]
[Bindable]
public class ModuleVO implements IValueObject
{
	
    // =========================================================================
    // Properties
    // =========================================================================
   
    // Assets
         
    /**
    * Default module small icon (16x16).
    */
    [Transient]
    [Embed(source="/../assets/module16.png")]
    public static var defaultSmallIcon : Class;
    
    /**
    * Default module big icon (64x64).
    */
    [Transient]
    [Embed(source="/../assets/module64.png")]
    public static var defaultBigIcon : Class;
    
    
    // Fields
    
    /**
    * The displayed name of the module.
    */
	public var name : String = null;
	
	/**
	* The description of the module.
	*/
	public var description : String = null;
	
    /**
    * The small icon (16x16) path.
    */
    public var smallIcon : String = null;
    
    /**
    * The big icon (64x64) path.
    */
    public var bigIcon : String = null;
    
    
    // Transient fields (client-side only)
    
    /**
    * The current state of the module
    */
    [Transient]
    public var loaded : Boolean = false;
    
    
    // =========================================================================
    // Public methods
    // =========================================================================
    
    /**
    * Get the the small icon asset
    */
    public function getSmallIconClass(target:UIComponent):Class
    {
        // If no icon specified, return the default one
        if (smallIcon == null)
        {
            return defaultSmallIcon;
        }
        
        // Else load the given URL
        else
        {
            return IconUtility.getClass(target,bigIcon,16,16);
        }
    }
    
    /**
    * Get the the big icon asset
    */
    public function getBigIconClass(target:UIComponent):Class
    {
        // If no icon specified, return the default one
        if (bigIcon == null)
        {
            return defaultBigIcon;
        }
        
        // Else load the given URL
        else
        {
            return IconUtility.getClass(target,bigIcon,64,64);
        }
    }
    
    /**
    * Ouput the module name
    */
    public function toString():String
    {
        return name;
    }
}
}
