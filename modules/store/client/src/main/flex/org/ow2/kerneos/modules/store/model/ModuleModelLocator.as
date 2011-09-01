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
package org.ow2.kerneos.modules.store.model
{

import avmplus.metadataXml;

import org.ow2.kerneos.modules.store.business.IModuleDelegate;

import org.ow2.kerneos.modules.store.business.ModuleDelegate;

import com.adobe.cairngorm.model.ModelLocator;

import mx.collections.ArrayCollection;

import mx.utils.UIDUtil;

import org.ow2.kerneos.modules.store.vo.StoreInfo;

/**
 * The model locator for the Module.
 */
[Bindable]
public class ModuleModelLocator implements ModelLocator
{

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
    private static var moduleModel : ModuleModelLocator = null;

    ////////////////////////////////////////////
    //                                        //
    //             Variables Delegate         //
    //                                        //
    ////////////////////////////////////////////

    // Put here the delegate instances of your model.
    // Example :
    private var myDelegate : IModuleDelegate = null;


    ////////////////////////////////////////////////
    //                                            //
    //             Variables Of the model         //
    //                                            //
    ////////////////////////////////////////////////


    // Put here all the variables of the model
    // Example :
    private var _myDataObj : String = null;

    private var _myStoreInfo : StoreInfo = null;

    private var _storeState : String = "";

    /**
     * List of selected modules of the installed modules list
     */
    private var _listeSelectedModules : ArrayCollection = null;


    ////////////////////////////////////
    //                                //
    //             Functions          //
    //                                //
    ////////////////////////////////////

    public function ModuleModelLocator()
    {
        super();

        if (moduleModel != null)
        {
            throw new Error("Only one ModelLocator has to be set");
        }
    }


    /**
     * Get the only created instance of the ModuleModelLocator
     */
    public static function getInstance() : ModuleModelLocator
    {
        if (ModuleModelLocator.moduleModel == null)
        {
            ModuleModelLocator.moduleModel = new ModuleModelLocator();
        }

        return ModuleModelLocator.moduleModel;
    }

    ////////////////////////////////////
    //                                //
    //             Setters            //
    //                                //
    ////////////////////////////////////


    // Put here all the setters for the model update.
    // Example :
    public function set myDataObj(_myData : String) : void {
        this._myDataObj = _myData;
    }

    public function set myStoreInfo (_myStore : StoreInfo) : void {
        this._myStoreInfo = _myStore;
    }

    public function set storeState (_state : String) : void {
        this._storeState = _state;
    }

    public function set listeSelectedModules (_listeModules : ArrayCollection) : void {
        this._listeSelectedModules = _listeModules;
    }


    ////////////////////////////////////
    //                                //
    //             Getters            //
    //                                //
    ////////////////////////////////////


    // Put here all the getters to access the model variables
    // Example :
    public function get myDataObj() : String {
        return this._myDataObj;
    }

    public function get myStoreInfo () : StoreInfo {
        return this._myStoreInfo
    }

     public function get storeState () : String {
       return this._storeState;
    }

    public function get listeSelectedModules () : ArrayCollection {
        return this._listeSelectedModules;
    }

    ////////////////////////////////////////////
    //                                        //
    //            Delegate Getters            //
    //                                        //
    ////////////////////////////////////////////


    // Put here the getters to access all the delegates of the created module
    // Example :
    public function getMyDelegate() : IModuleDelegate {
        if (this.myDelegate == null) {
            this.myDelegate = new ModuleDelegate();
        }
        return this.myDelegate;
    }



}
}
