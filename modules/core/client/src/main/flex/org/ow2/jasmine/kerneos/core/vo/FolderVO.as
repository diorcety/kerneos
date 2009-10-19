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
import mx.core.UIComponent;

import org.ow2.jasmine.kerneos.common.util.IconUtility;


/**
 * A group of modules displayed as a folder in Kerneos.
 *
 * @author Julien Nicoulaud
 */
[RemoteClass(alias="org.ow2.jasmine.kerneos.service.Folder")]
[Bindable]
public class FolderVO extends ModuleWithWindowVO
                      implements IValueObject
{
    
    // =========================================================================
    // Properties
    // =========================================================================
    
    // Assets
         
    /**
    * Default folder small icon (16x16).
    */
    [Transient]
    [Embed(source="/../assets/folder16.png")]
    public static var defaultFolderSmallIcon : Class;
    
    /**
    * Default folder big icon (64x64).
    */
    [Transient]
    [Embed(source="/../assets/folder64.png")]
    public static var defaultFolderBigIcon : Class;
    
    
    // Fields
    
    /**
     * The modules.
     */
    [ArrayElementType('org.ow2.jasmine.kerneos.core.vo.ModuleVO')]
    public var modules : ArrayCollection;
    
    
    // =========================================================================
    // Public methods
    // =========================================================================
    
    /**
    * Get the the small icon asset.
    */
    override public function getSmallIconClass(target:UIComponent):Class
    {
        // If no icon specified, return the default one
        if (smallIcon == null)
        {
            return defaultFolderSmallIcon;
        }
        
        // Else load the given URL
        else
        {
            return IconUtility.getClass(target,bigIcon,16,16);
        }
    }
    
    /**
    * Get the the big icon asset.
    */
    override public function getBigIconClass(target:UIComponent):Class
    {
        // If no icon specified, return the default one
        if (bigIcon == null)
        {
            return defaultFolderBigIcon;
        }
        
        // Else load the given URL
        else
        {
            return IconUtility.getClass(target,bigIcon,64,64);
        }
    }
}
}
