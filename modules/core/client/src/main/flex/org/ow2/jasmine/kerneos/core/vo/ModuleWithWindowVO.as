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
package org.ow2.jasmine.kerneos.core.vo
{
import com.adobe.cairngorm.vo.IValueObject;


/**
 * Describes the configuration of a Kerneos module with its own window.
 * 
 * @author Guillaume Renault
 * @author Julien Nicoulaud
 */
[RemoteClass(alias="org.ow2.jasmine.kerneos.config.generated.ModuleWithWindow")]
[Bindable]
public class ModuleWithWindowVO extends ModuleVO
                                implements IValueObject
{
    
    // =========================================================================
    // Properties
    // =========================================================================
   
    /**
    * Load the module on application startup.
    */
    public var loadOnStartup : Boolean = false;
    
    /**
    * Load the module maximized.
    */
    public var loadMaximized : Boolean = false;
    
    /**
    * The wished width for the module window.
    */
    public var width : Number = NaN;
    
    /**
    * The wished height for the module window.
    */
    public var height : Number = NaN;
    
    /**
    * Wether the module window should be resizable.
    */
    public var resizable : Boolean = true;
    
    /**
    * Wether the module window should be maximizable.
    */
    public var maximizable : Boolean = true;
    
    /**
    * Prompt the user before closing the module
    * 
    * Can be "never", "always", or "default". "default" means that if the module implements 
    * {@see KerneosModule}, the method canBeCloseWithoutPrompt() is called. If not, the user is
    * prompted by default.
    */

    public var promptBeforeClose : PromptBeforeCloseVO = PromptBeforeCloseVO.DEFAULT;
    
}
}
