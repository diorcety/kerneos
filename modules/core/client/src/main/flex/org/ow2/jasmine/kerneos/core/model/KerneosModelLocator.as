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

import mx.collections.ArrayCollection;
import mx.utils.UIDUtil;

import org.ow2.jasmine.kerneos.core.business.*;
import org.ow2.jasmine.kerneos.core.vo.KerneosConfigVO;

/**
* Kerneos model
* 
* @author Guillaume Renault, Julien Nicoulaud
*/
public class KerneosModelLocator implements ModelLocator {
    
    // =========================================================================
    // Properties
    // =========================================================================
    
    /**
    * The unique ID of this component
    * 
    * @internal
    *   Used to prevent a Cairngorm issue: when a command event is dispatched,
    * every controller that registered this event type receives it, even if
    * located in another module. To prevent this from happening and triggering
    * multiple severe unexpected concurrence bugs, each event dispatched is
    * postfixed with this unique ID.
    */
    public var componentID:String = UIDUtil.createUID();
    
    /**
    * The config of Kerneos
    */
    [Bindable]
    public var config:KerneosConfigVO;

    /**
    * The stored notifications
    */
    [Bindable]
    [ArrayElementType('org.ow2.jasmine.kerneos.common.vo.KerneosNotification')]
    public var notifications:ArrayCollection = new ArrayCollection();

    
    // Singleton class unique instance
    
	/**
    * Unique instance of this locator.
    */
    private static var model:KerneosModelLocator;
    
    
    // Delegates unique instances
    
    /**
    * "Load Kerneos config file" delegate unique instance
    */
    private var loadKerneosConfigDelegate:ILoadKerneosConfigDelegate = null;

    
    // =========================================================================
    // Construction
    // =========================================================================
    
    /**
    * Build a new KerneosModelLocator
    */
    public function KerneosModelLocator(){
        super();
        if (model != null){
            throw new Error("Only one KerneosModelLocator can be instantiated.");
        }
    }
    
    /**
    * Get the unique instance of KerneosModelLocator
    */
    public static function getInstance() : KerneosModelLocator{
        if (KerneosModelLocator.model == null) {
            KerneosModelLocator.model = new KerneosModelLocator();
        }
        return KerneosModelLocator.model;
    }
    
    
    // =========================================================================
    // Delegates getters
    // =========================================================================
    
    /**
    * Get the "Load Kerneos config file" delegate unique instance
    */
    public function getLoadKerneosConfigDelegate():ILoadKerneosConfigDelegate {
        if (this.loadKerneosConfigDelegate == null) {
            this.loadKerneosConfigDelegate = new LoadKerneosConfigDelegate();
        }
        return this.loadKerneosConfigDelegate;
    }
}
}
