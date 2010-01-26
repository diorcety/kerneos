/**
 * JASMINe
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
package org.ow2.jasmine.kerneos.core.view.window
{
import flash.events.ProgressEvent;
import flash.system.ApplicationDomain;

import mx.controls.Alert;
import mx.events.ModuleEvent;
import mx.modules.ModuleLoader;
import mx.resources.ResourceManager;

import org.ow2.jasmine.kerneos.common.controls.KerneosProgressBar;
import org.ow2.jasmine.kerneos.core.managers.LanguagesManager;
import org.ow2.jasmine.kerneos.core.vo.SWFModuleVO;
    

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
    
    /**
    * The progress bar
    */
    private var _progressBar : KerneosProgressBar;
    
    
    // =========================================================================
    // Constructor & initialization
    // =========================================================================
    
    /**
    * Build a new Swf module hosting window
    */
    public function SwfModuleWindow(module:SWFModuleVO)
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
        
        // Setup the SWF module loader
        _loader = new ModuleLoader();
        _loader.url = (module as SWFModuleVO).file;
        _loader.percentWidth = 100;
        _loader.percentHeight = 100;
        _loader.addEventListener(ModuleEvent.READY,onLoaderReady);
        _loader.addEventListener(ModuleEvent.ERROR,onLoaderError);
        _loader.applicationDomain = ApplicationDomain.currentDomain;
        
        // Setup the progress bar
        _progressBar = new KerneosProgressBar();
        _progressBar.setStyle("trackHeight",20);
        _progressBar.setStyle("barColor",0x444444);
        _progressBar.setStyle("color",0xEFEFEF);
        _progressBar.setStyle("borderColor",0xFFFFFF);
        _progressBar.source = _loader;
        _progressBar.conversion = 1024;
        _progressBar.label = ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE,'kerneos.windows.swf.loading-bar.label') + " %3%% (%1/%2 kb)";
        addChild(_progressBar);
        
        // Start loading
        this.setStyle("backgroundColor",0x666666);
        _loader.loadModule();

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
    
    
    // =========================================================================
    // Private methods
    // =========================================================================
    
    /**
    * When the module has finished loading
    */
    private function onLoaderReady(event:ProgressEvent):void
    {
        removeChild(_progressBar);
        addChild(_loader);
        this.setStyle("backgroundColor",0xCCCCCC);
    }
    
    /**
    * If there was an error while loading the module
    */
    private function onLoaderError(event:ModuleEvent):void
    {
        Alert.show(event.errorText + '\n' + ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE,'kerneos.windows.swf.loading-failed-dialog.body'),ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE,'kerneos.windows.swf.loading-failed-dialog.title',[module.name]));
    }
    
}
}
