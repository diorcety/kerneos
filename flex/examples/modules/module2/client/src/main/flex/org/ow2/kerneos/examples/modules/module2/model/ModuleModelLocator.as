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
package org.ow2.kerneos.examples.modules.module2.model {

import org.ow2.kerneos.examples.modules.module2.business.IPostDelegate;

import org.ow2.kerneos.examples.modules.module2.business.PostDelegate;

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

    ////////////////////////////////////////////
    //                                        //
    //             Variables Delegate         //
    //                                        //
    ////////////////////////////////////////////

    private var myDelegate:IPostDelegate = null;


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

    public function getMyDelegate():IPostDelegate {
        if (this.myDelegate == null) {
            this.myDelegate = new PostDelegate();
        }
        return this.myDelegate;
    }

}
}
