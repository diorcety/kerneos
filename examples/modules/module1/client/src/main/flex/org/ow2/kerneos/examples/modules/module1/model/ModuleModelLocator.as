/**
 * Kerneos
 * Copyright (C) 2009-2011 Bull S.A.S.
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
 * --------------------------------------------------------------------------
 */
package org.ow2.kerneos.examples.modules.module1.model {

import org.ow2.kerneos.examples.modules.module1.business.IModuleDelegate;

import org.ow2.kerneos.examples.modules.module1.business.ModuleDelegate;

import com.adobe.cairngorm.model.ModelLocator;

import mx.utils.UIDUtil;

/**
 * The model locator for the Module.
 */
[Bindable]
public class ModuleModelLocator implements ModelLocator {

    ////////////////////////////////////
    //                                //
    //             Variables          //
    //                                //
    ////////////////////////////////////

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
     * Unique instance of this locator.
     */
    private static var moduleModel:ModuleModelLocator = null;

    ////////////////////////////////////////////
    //                                        //
    //             Variables Delegate         //
    //                                        //
    ////////////////////////////////////////////

    private var myDelegate:IModuleDelegate = null;


    ////////////////////////////////////////////////
    //                                            //
    //             Variables Of the model         //
    //                                            //
    ////////////////////////////////////////////////


    ////////////////////////////////////
    //                                //
    //             Functions          //
    //                                //
    ////////////////////////////////////

    public function ModuleModelLocator() {
        super();

        if (moduleModel != null) {
            throw new Error("Only one ModelLocator has to be set");
        }
    }


    /**
     * Get the only created instance of the ModuleModelLocator
     */
    public static function getInstance():ModuleModelLocator {
        if (ModuleModelLocator.moduleModel == null) {
            ModuleModelLocator.moduleModel = new ModuleModelLocator();
        }

        return ModuleModelLocator.moduleModel;
    }

    ////////////////////////////////////
    //                                //
    //             Setters            //
    //                                //
    ////////////////////////////////////


    ////////////////////////////////////
    //                                //
    //             Getters            //
    //                                //
    ////////////////////////////////////


    ////////////////////////////////////////////
    //                                        //
    //            Delegate Getters            //
    //                                        //
    ////////////////////////////////////////////

    public function getMyDelegate():IModuleDelegate {
        if (this.myDelegate == null) {
            this.myDelegate = new ModuleDelegate();
        }
        return this.myDelegate;
    }

}
}
