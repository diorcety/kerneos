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
* A data object that describes a configuration for Kerneos
* 
* @author Julien Nicoulaud
*/
[RemoteClass(alias="org.ow2.jasmine.kerneos.service.KerneosConfig")]
[Bindable]
public class KerneosConfigVO implements IValueObject
{
    // =========================================================================
    // Properties
    // =========================================================================
    
    // Settings
    
    /**
    * The name of the project (totally random example: "JASMINe")
    */
    public var consoleProject : String = "JASMINe";
    
    /**
    * The name of the console
    */
    public var consoleName : String = "Kerneos";
    
    /**
    * Allow to show the taskbar "Minimize all" icon
    */
    public var showMinimizeAllIcon : Boolean = true;
    
    /**
    * Allow to show the taskbar "cascade" icon
    */
    public var showCascadeIcon : Boolean = true;
    
    /**
    * Allow to show the taskbar "tile" icon
    */
    public var showTileIcon : Boolean = true;
    
    /**
    * Allow to show notification popups
    */
    public var showNotificationPopUps : Boolean = true;
    
    /**
    * Allow to show notification popups, even on windows that have the focus
    */
    public var showPopupsWhenFocused : Boolean = false;
    
    /**
    * Enable notifications logging
    */
    public var enableNotificationsLog : Boolean = true;
    
    /**
    * Show a "confirm close" dialog when closing the browser window
    */
    public var showConfirmCloseDialog : Boolean = true;
    
    
    // Modules
    
    /**
    * The modules
    */
    [ArrayElementType('org.ow2.jasmine.kerneos.core.vo.ModuleVO')]
    public var modules : ArrayCollection;

}
}
