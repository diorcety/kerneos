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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.jasmine.kerneos.core.view.window {
import com.adobe.cairngorm.business.ServiceLocator;

import flash.system.ApplicationDomain;

import flash.utils.Dictionary;

import mx.controls.Alert;
import mx.controls.ProgressBarMode;
import mx.events.ModuleEvent;
import mx.modules.IModuleInfo;
import mx.modules.Module;

import mx.modules.ModuleManager;
import mx.resources.ResourceManager;

import org.granite.util.GraniteClassRegistry;
import org.ow2.jasmine.kerneos.common.controls.KerneosProgressBar;
import org.ow2.jasmine.kerneos.core.api.KerneosModule;
import org.ow2.jasmine.kerneos.core.managers.KerneosLifeCycleManager;
import org.ow2.jasmine.kerneos.core.managers.LanguagesManager;
import org.ow2.jasmine.kerneos.core.vo.SWFModuleVO;
import org.ow2.jasmine.kerneos.core.vo.ServiceVO;


/**
 * A window hosting a Swf ModuleLoader
 *
 * @author Julien Nicoulaud
 */
[Bindable]
public class SwfModuleWindow extends ModuleWindow {

    // =========================================================================
    // Variables
    // =========================================================================

    /**
     * The SWF module loader
     */
    private var _moduleInfo:IModuleInfo;
    private var _child:Module;

    /**
     * The progress bar
     */
    private var _progressBar:KerneosProgressBar;

    // =========================================================================
    // Constructor & initialization
    // =========================================================================

    /**
     * Build a new Swf module hosting window
     */
    public function SwfModuleWindow(module:SWFModuleVO) {
        // Call super classe constructor
        super(module);
    }

    /**
     * Create UI children
     */
    override protected function createChildren():void {
        // Call super class method
        super.createChildren();

        if (!_child) {
            // Setup the progress bar
            _progressBar = new KerneosProgressBar();
            _progressBar.setStyle("trackHeight", 20);
            _progressBar.setStyle("barColor", 0x444444);
            _progressBar.setStyle("color", 0xEFEFEF);
            _progressBar.setStyle("borderColor", 0xFFFFFF);
            _progressBar.conversion = 1024;
            _progressBar.mode = ProgressBarMode.MANUAL;
            _progressBar.label = ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE,
                                                                         'kerneos.windows.swf.loading-bar.label') + " %3%% (%1/%2 kb)";
            addChild(_progressBar);

            // Start loading
            this.setStyle("backgroundColor", 0x666666);
        }
    }

    // =========================================================================
    // Public methods
    // =========================================================================

    /**
     * Get the KerneosModule object associated with this SwfModule
     * @return null if there is not KerneosModule
     */
    public function getKerneosModule():KerneosModule {
        if (_child is KerneosModule) {
            return _child as KerneosModule;
        }
        return null;
    }

    /**
     * Load the module
     */
    public function load():void {
        var currentDate:Date = new Date();
        var params:String = new Number(currentDate.getTime()).toString();

        registerServices();

        // Setup the SWF module loader
        _moduleInfo = ModuleManager.getModule((module as SWFModuleVO).file + "?" + params);
        _moduleInfo.addEventListener(ModuleEvent.READY, onLoaderReady, false, 0, true);
        _moduleInfo.addEventListener(ModuleEvent.ERROR, onLoaderError, false, 0, true);
        _moduleInfo.addEventListener(ModuleEvent.PROGRESS, onProgress, false, 0, true);
        _moduleInfo.load(new ApplicationDomain(ApplicationDomain.currentDomain));
    }

    private function registerClasses():void {

        // Register classes used by the services if the module is a Kerneos module type
        if (getKerneosModule()) {
            var dic:Dictionary = getKerneosModule().servicesClasses();
            if (dic != null) {
                for (var key:String in dic) {
                    for each(var service:ServiceVO in (module as SWFModuleVO).services.service) {
                        if (service.id == key)
                            GraniteClassRegistry.registerClasses(service.destination, dic[key] as Array);
                    }
                }
            }
        }
    }

    private function registerServices():void {
        var serviceLocator:ServiceLocator = ServiceLocator.getInstance();

        // Register services
        for each(var service:ServiceVO in (module as SWFModuleVO).services.service) {
            serviceLocator.setServiceForId(service.id, service.destination, service.asynchronous);
            if (service.asynchronous) {
                serviceLocator.getConsumer(service.id).channelSet = KerneosLifeCycleManager.amfGravityChannelSet;
                serviceLocator.getProducer(service.id).channelSet = KerneosLifeCycleManager.amfGravityChannelSet;
            }
            else {
                serviceLocator.getRemoteObject(service.id).channelSet = KerneosLifeCycleManager.amfChannelSet;
            }
        }
    }

    /**
     * Unload the module
     */
    public function unload():void {
        // If the module implements the interface KerneosModule,
        // trigger the closeModule() method
        if (_child is KerneosModule) {
            (_child as KerneosModule).closeModule();
        }

        unregisterServices();
        unregisterClasses();

        // Remove module
        removeChild(_child);
        _child = null;

        _moduleInfo.unload();
        _moduleInfo = null;
    }

    private function unregisterClasses():void {

        // Unregister classes used by the services if the module is a Kerneos module type
        if (getKerneosModule()) {
            var dic:Dictionary = getKerneosModule().servicesClasses();
            if (dic != null) {
                for (var key:String in dic) {
                    GraniteClassRegistry.unregisterClasses(key);
                }
            }
        }
    }

    private function unregisterServices():void {
        var serviceLocator:ServiceLocator = ServiceLocator.getInstance();

        for each(var service:ServiceVO in (module as SWFModuleVO).services.service) {
            if (service.asynchronous) {
                serviceLocator.getConsumer(service.id).disconnect();
                serviceLocator.getProducer(service.id).disconnect();
            }
            else {
                serviceLocator.getRemoteObject(service.id).disconnect();
            }

            serviceLocator.removeServiceForId(service.id, service.asynchronous);
        }
    }

    // =========================================================================
    // Private methods
    // =========================================================================

    /**
     * When the module loading
     */
    private function onProgress(event:ModuleEvent):void {
        _progressBar.setProgress(event.bytesLoaded, event.bytesTotal);
    }

    /**
     * When the module has finished loading
     */
    private function onLoaderReady(event:ModuleEvent):void {
        _child = _moduleInfo.factory.create() as Module;

        registerClasses();

        if (_progressBar)
            removeChild(_progressBar);

        addChild(_child);

        this.setStyle("backgroundColor", 0xCCCCCC);
    }

    /**
     * If there was an error while loading the module
     */
    private function onLoaderError(event:ModuleEvent):void {
        Alert.show(event.errorText + '\n' + ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE,
                                                                                    'kerneos.windows.swf.loading-failed-dialog.body'),
                   ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE,
                                                           'kerneos.windows.swf.loading-failed-dialog.title',
                                                           [module.name]));
    }

}
}
