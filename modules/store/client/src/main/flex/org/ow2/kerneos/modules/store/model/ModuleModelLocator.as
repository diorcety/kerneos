/**
 * Kerneos
 * Copyright (C) 2011 Bull S.A.S.
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
package org.ow2.kerneos.modules.store.model {

import avmplus.metadataXml;

import flash.display.Bitmap;

import org.ow2.kerneos.modules.store.business.IModuleDelegate;

import org.ow2.kerneos.modules.store.business.ModuleDelegate;

import com.adobe.cairngorm.model.ModelLocator;

import mx.collections.ArrayCollection;

import mx.utils.UIDUtil;

import org.ow2.kerneos.modules.store.vo.ModuleVO;

import org.ow2.kerneos.modules.store.vo.StoreVO;

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

    // Put here the delegate instances of your model.
    // Example :
    private var myDelegate:IModuleDelegate = null;


    ////////////////////////////////////////////////
    //                                            //
    //             Variables Of the model         //
    //                                            //
    ////////////////////////////////////////////////


    // Put here all the variables of the model

    private var _currentStore:StoreVO = null;

    private var _storeState:String = "";

    private var _mainModule:ModuleVO = null;

    /**
     * List of selected modules of the installed modules list
     */
    private var _listSelectedModules:ArrayCollection = null;

    private var _keywords:String = null;

    /**
     * List of modules showed in the main view, also is the list
     * of results finding modules
     */
    [ArrayElementType('org.ow2.kerneos.modules.store.vo.ModuleVO')]
    private var _listModules:ArrayCollection = null;

    private var _imageTest:Bitmap = null;

    [ArrayElementType('String')]
    private var _categories:ArrayCollection = null;

    /**
     * List of installed modules
     */
    [ArrayElementType('org.ow2.kerneos.modules.store.vo.ModuleVO')]
    private var _listInstalledModules:ArrayCollection = null;

    /**
     * List of installed modules
     */
    [ArrayElementType('org.ow2.kerneos.modules.store.vo.StoreVO')]
    private var _listStores:ArrayCollection = null;

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

    /**
     * Return the item index if the store with this id is contained in the list, otherwise it return -1
     * @param id Store id
     * @return item index into listStores or -1 if not funded
     */
    public function getListStoreItemIndex(id:String):int {
        if (this._listStores == null || this._listStores.length == 0) {
            return -1;
        }

        var index:int = 0;
        for each (var store:StoreVO in this._listStores) {
            if (store.id == id) {
                return index;
            }
        }

        return -1;
    }

    ////////////////////////////////////
    //                                //
    //             Setters            //
    //                                //
    ////////////////////////////////////


    // Put here all the setters for the model update.

    public function set currentStore(_myStore:org.ow2.kerneos.modules.store.vo.StoreVO):void {
        this._currentStore = _myStore;
    }

    public function set storeState(_state:String):void {
        this._storeState = _state;
    }

    public function set listSelectedModules(_listModules:ArrayCollection):void {
        this._listSelectedModules = _listModules;
    }

    public function set listModules(_listModules:ArrayCollection):void {
        this._listModules = _listModules;
    }

    public function set mainModule(_mainModule:ModuleVO):void {
        this._mainModule = _mainModule;
    }

    public function set imageTest(_imageTest:Bitmap):void {
        this._imageTest = _imageTest;
    }

    public function set categories(_categories:ArrayCollection):void {
        this._categories = _categories;
    }

    public function set listInstalledModules(_listInstalledModules:ArrayCollection):void {
        this._listInstalledModules = _listInstalledModules;
    }

    public function set listStores(_listStores:ArrayCollection):void {
        this._listStores = _listStores;
    }


    public function set keywords(value:String):void {
        _keywords = value;
    }

    ////////////////////////////////////
    //                                //
    //             Getters            //
    //                                //
    ////////////////////////////////////


    // Put here all the getters to access the model variables

    public function get currentStore():org.ow2.kerneos.modules.store.vo.StoreVO {
        return this._currentStore
    }

    public function get storeState():String {
        return this._storeState;
    }

    public function get listSelectedModules():ArrayCollection {
        return this._listSelectedModules;
    }

    public function get listModules():ArrayCollection {
        return this._listModules;
    }

    public function get mainModule():ModuleVO {
        return this._mainModule;
    }

    public function get imageTest():Bitmap {
        return this._imageTest;
    }

    public function get categories():ArrayCollection {
        return this._categories;
    }

    public function get listInstalledModules():ArrayCollection {
        return this._listInstalledModules;
    }

    public function get listStores():ArrayCollection {
        return this._listStores;
    }

    public function get keywords():String {
        return _keywords;
    }

    ////////////////////////////////////////////
    //                                        //
    //            Delegate Getters            //
    //                                        //
    ////////////////////////////////////////////

    // Put here the getters to access all the delegates of the created module
    public function getMyDelegate():IModuleDelegate {
        if (this.myDelegate == null) {
            this.myDelegate = new ModuleDelegate();
        }
        return this.myDelegate;
    }

}
}
