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
package org.ow2.jasmine.kerneos.core.model{

import com.adobe.cairngorm.model.ModelLocator;

import org.ow2.jasmine.kerneos.core.business.IModuleDelegate;
import org.ow2.jasmine.kerneos.core.business.ModuleDelegate;
import org.ow2.jasmine.kerneos.core.vo.KerneosConfigVO;

/**
* Kerneos model
* 
* @author Guillaume Renault, Julien Nicoulaud
*/
public class ModuleModelLocator implements ModelLocator{
    
    // =========================================================================
    // Properties
    // =========================================================================

    /**
    * The config of Kerneos
    */
    [Bindable]
    public var config:KerneosConfigVO;
    
    
    // Singleton class (beurk) unique instance
    
	/**
    * Unique instance of this locator.
    */
    private static var model:ModuleModelLocator;
    
    
    // Delegates unique instances
    
    /**
    * Unique instance of delegate.
    */
    private var moduleDelegate:IModuleDelegate = null;

    
    // =========================================================================
    // Construction
    // =========================================================================
    
    /**
    * 
    */
    public function ModuleModelLocator(){
        super();
        if (model != null){
            throw new Error("Only one ModuleModelLocator has to be set");
        }
    }
    
    /**
    * 
    */
    public static function getInstance() : ModuleModelLocator{
        if (ModuleModelLocator.model == null) {
            ModuleModelLocator.model = new ModuleModelLocator();
        }
        return ModuleModelLocator.model;
	}	
    
    
    // =========================================================================
    // Delegates getters
    // =========================================================================
    
    /**
    * 
    */
    public function getModuleDelegate():IModuleDelegate {
        if (this.moduleDelegate == null) {
            this.moduleDelegate = new ModuleDelegate();
        }
        return this.moduleDelegate;
    }
}
}