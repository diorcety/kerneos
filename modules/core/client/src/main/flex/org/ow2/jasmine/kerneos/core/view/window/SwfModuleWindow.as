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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.jasmine.kerneos.core.view.window
{
import mx.core.Container;
import mx.modules.ModuleLoader;

import org.ow2.jasmine.kerneos.core.vo.ModuleVO;
	

/**
* A window hosting a Swf ModuleLoader
* 
* @author Julien Nicoulaud
*/
[Bindable]
public class SwfModuleWindow extends ModuleWindow
{
        
    // =========================================================================
    // Variables
    // =========================================================================
    
    /**
    * The SWF module loader
    */
    private var _loader : ModuleLoader;
    
    
    // =========================================================================
    // Constructor & intiialization
    // =========================================================================
    
    /**
    * Build a new Swf module hosting window
    */
	public function SwfModuleWindow(module:ModuleVO)
	{
		// Call super classe constructor
		super(module);
	}
    
    /**
    * Create UI children
    */
    override protected function createChildren():void
    {
        // Call super class method
        super.createChildren();
        
        // Add the SWF module loader
        _loader = new ModuleLoader();
        _loader.url = module.swfFile;
        _loader.percentWidth = 100;
        _loader.percentHeight = 100;
        addChild(_loader);
    }
	
	
    // =========================================================================
    // Getter & setters
    // =========================================================================
    
    /**
    * Get the hosted module loader
    */
    public function get moduleLoader():ModuleLoader
    {
        return _loader;
    }
}
}
