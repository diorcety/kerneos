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
package org.ow2.kerneos.core.model {

import com.adobe.cairngorm.model.ModelLocator;

import mx.collections.ArrayCollection;

import org.ow2.kerneos.core.business.*;
import org.ow2.kerneos.core.vo.ApplicationVO;


/**
 * Kerneos model.
 *
 * @author Guillaume Renault
 * @author Julien Nicoulaud
 */
public class KerneosModelLocator implements ModelLocator {
    // Singleton class unique instance

    /**
     * Unique instance of this locator.
     */
    private static var model:KerneosModelLocator;

    // =========================================================================
    // Properties
    // =========================================================================

    /**
     * The config of Kerneos.
     */
    [Bindable]
    public var application:ApplicationVO = null;

    /**
     * The Module configurations.
     */
    [Bindable]
    [ArrayElementType('org.ow2.kerneos.core.vo.ModuleVO')]
    public var modules:ArrayCollection = new ArrayCollection();

    /**
     * The state of the application.
     */
    [Bindable]
    public var state:String = KerneosState.INIT;

    /**
     * The stored notifications.
     */
    [Bindable]
    [ArrayElementType('org.ow2.kerneos.core.vo.KerneosNotification')]
    public var notifications:ArrayCollection = new ArrayCollection();


    // Delegates unique instances

    /**
     * "Get Kerneos config file" delegate unique instance
     */
    private var getKerneosConfigDelegate:IGetApplicationConfigDelegate = null;

    /**
     * "Get Modules" delegate unique instance
     */
    private var getModulesDelegate:IGetModulesDelegate = null;

    // =========================================================================
    // Construction
    // =========================================================================

    /**
     * Build a new KerneosModelLocator.
     */
    public function KerneosModelLocator() {
        super();

        if (model != null) {
            throw new Error("Only one KerneosModelLocator can be instantiated.");
        }
    }


    /**
     * Get the unique instance of KerneosModelLocator.
     */
    public static function getInstance():KerneosModelLocator {
        if (KerneosModelLocator.model == null) {
            KerneosModelLocator.model = new KerneosModelLocator();
        }
        return KerneosModelLocator.model;
    }

    // =========================================================================
    // Delegates getters
    // =========================================================================

    /**
     * Get the "Get Kerneos config file" delegate unique instance.
     */
    public function getGetKerneosConfigDelegate():IGetApplicationConfigDelegate {
        if (this.getKerneosConfigDelegate == null) {
            this.getKerneosConfigDelegate = new GetApplicationDelegate();
        }
        return this.getKerneosConfigDelegate;
    }

    /**
     * Get the "Get Modules" delegate unique instance.
     */
    public function getGetModulesDelegate():IGetModulesDelegate {
        if (this.getModulesDelegate == null) {
            this.getModulesDelegate = new GetModulesDelegate();
        }
        return this.getModulesDelegate;
    }
}
}
