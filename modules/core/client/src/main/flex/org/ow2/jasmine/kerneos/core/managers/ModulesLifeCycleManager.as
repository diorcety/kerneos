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
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.jasmine.kerneos.core.managers
{

import com.adobe.cairngorm.business.ServiceLocator;
import com.adobe.cairngorm.control.CairngormEventDispatcher;
import com.google.code.flexiframe.IFrame;

import flash.events.Event;
import flash.net.URLRequest;
import flash.net.navigateToURL;
import flash.system.System;
import flash.utils.Dictionary;

import flexlib.mdi.containers.MDIWindow;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.messaging.ChannelSet;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageEvent;
import mx.utils.UIDUtil;

import org.granite.gravity.Consumer;
import org.ow2.jasmine.kerneos.common.event.KerneosNotificationEvent;
import org.ow2.jasmine.kerneos.common.util.IconUtility;
import org.ow2.jasmine.kerneos.core.event.KerneosConfigEvent;
import org.ow2.jasmine.kerneos.core.model.KerneosModelLocator;
import org.ow2.jasmine.kerneos.core.view.DesktopView;
import org.ow2.jasmine.kerneos.core.view.window.FolderWindow;
import org.ow2.jasmine.kerneos.core.view.window.IFrameModuleWindow;
import org.ow2.jasmine.kerneos.core.view.window.MinimizedModuleWindow;
import org.ow2.jasmine.kerneos.core.view.window.ModuleWindow;
import org.ow2.jasmine.kerneos.core.view.window.SwfModuleWindow;
import org.ow2.jasmine.kerneos.core.vo.FolderVO;
import org.ow2.jasmine.kerneos.core.vo.IFrameModuleVO;
import org.ow2.jasmine.kerneos.core.vo.LinkVO;
import org.ow2.jasmine.kerneos.core.vo.ModuleEventVO;
import org.ow2.jasmine.kerneos.core.vo.ModuleVO;
import org.ow2.jasmine.kerneos.core.vo.ModuleWithWindowVO;
import org.ow2.jasmine.kerneos.core.vo.SWFModuleVO;
import org.ow2.jasmine.kerneos.core.vo.ServiceVO;
import org.ow2.jasmine.kerneos.common.util.StringUtils;


/**
 * Manages the modules life cycle: setup, start, stop, give focus to a module...
 *
 * Also manage the shared libraries module loaders. Everything is kept in this class,
 * even if this kind of module is never unloaded. It may be helpful in case of some
 * monitoring features, like the number of shared libraries loaded, and how many
 * modules are using them.
 *
 * @author Julien Nicoulaud
 * @author Guillaume Renault
 */
public class ModulesLifeCycleManager
{

    // =========================================================================
    // Properties
    // =========================================================================

    /**
     * The desktop view on which operations are done.
     *
     * Must be set before calling the static functions.
     */
    [Bindable]
    public static var desktop : DesktopView = null;

    /**
     * The IFrame  objects
     */
    public static var frames : Dictionary = new Dictionary();

    /**
     * The gravity consumer for asynchronous OSGi communication
     */
    private static var consumer:Consumer = null;

    // =========================================================================
    // Public static methods
    // =========================================================================

    /**
     * Setup the modules.
     */
    public static function installModules(e : Event = null) : void
    {
        // Setup the modules services and icons
        installModulesCollection(KerneosModelLocator.getInstance().modules.modulesList);
    }

    /**
     * Launch the command to load the application configuration.
     */
    public static function getModules() : void
    {
        try
        {
            var event_module : KerneosConfigEvent = new KerneosConfigEvent(KerneosConfigEvent.GET_MODULES);
            CairngormEventDispatcher.getInstance().dispatchEvent(event_module);
        }
        catch (e : Error)
        {
            trace("An error occurred while loading module list: " + e.message);
        }
    }

    /**
     * Launch a module.
     */
    public static function startModule(module : ModuleVO) : void
    {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Show a busy cursor
        desktop.cursorManager.setBusyCursor();

        // If this is a module with its own window
        if (module is ModuleWithWindowVO)
        {
            // Update the module status
            module.loaded = true;

            // Declare a new window
            var window : ModuleWindow;

            // If this is a swf module
            if (module is SWFModuleVO)
            {
                // Create a window
                window = new SwfModuleWindow(module as SWFModuleVO);

                // Load Module
                (window as SwfModuleWindow).load();

                // Add Notification listener
                window.addEventListener(KerneosNotificationEvent.KERNEOS_NOTIFICATION, NotificationsManager.handleNotificationEvent, false, 0, true);
            }

            // Else if this is an IFrame module
            else if (module is IFrameModuleVO)
            {
                // Create an IFrame (with a unique Id)
                frames[module.name] = new IFrame("KerneosIFrame" + UIDUtil.createUID().split("-").join(""));
                var frame : IFrame = frames[module.name] as IFrame;
                window = new IFrameModuleWindow(module as IFrameModuleVO, frame);
            }

            // Else if this is a folder
            else if (module is FolderVO)
            {
                window = new FolderWindow(module as FolderVO);
            }

            // Create the button in the taskbar
            var minimizedModuleWindow : MinimizedModuleWindow = new MinimizedModuleWindow(window);
            desktop.minimizedWindowsButtonsContainer.addChild(minimizedModuleWindow);
            window.minimizedModuleWindow = minimizedModuleWindow;

            // Add it to the windows manager
            desktop.windowContainer.windowManager.add(window);
        }

        // Else if this is a simple link
        else if (module is LinkVO)
        {
            // Open it
            navigateToURL(new URLRequest((module as LinkVO).url), "_blank");
        }

        // Remove the busy cursor
        desktop.cursorManager.removeBusyCursor();
    }



    /**
     * Unload a module by its window.
     */
    public static function stopModuleByWindow(window : ModuleWindow) : void
    {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Remove the tasbar button
        desktop.minimizedWindowsButtonsContainer.removeChild(window.minimizedModuleWindow)

        if (window is SwfModuleWindow)
        {
            // Unload module
            (window as SwfModuleWindow).unload();

            // Unload the module
            KerneosLifeCycleManager.desktop.setFocus();
        }
        else if (window is IFrameModuleWindow)
        {
            // Delete the IFrame
            (window as IFrameModuleWindow).removeIFrame();
        }

        // Update the module state
        window.module.loaded = false;

        // Force garbage collection
        System.gc();
    }



    /**
     * Bring a module window to front
     */
    public static function bringToFront(module : ModuleVO) : void
    {
        // Check that desktop is not null
        checkDesktopNotNull();

        // look for the window are give it the focus
        var allWindows : Array = (desktop.windowContainer.windowManager.windowList as Array).concat();

        for each (var window : MDIWindow in allWindows)
        {
            if (window is ModuleWindow && (window as ModuleWindow).module.name == module.name)
            {
                (window as ModuleWindow).bringToFront();
                return;
            }
        }
    }



    /**
     * Unload all the active modules.
     */
    public static function unloadAllModules(e : Event = null) : void
    {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Unload all modules and close windows
        var allWindows : Array = (desktop.windowContainer.windowManager.windowList as Array).concat();

        for each (var window : MDIWindow in allWindows)
        {
            if (window is ModuleWindow)
            {
                stopModuleByWindow(window as ModuleWindow);
                desktop.windowContainer.windowManager.remove(window);
            }
        }
    }

    public static function unloadModule(module : ModuleVO) : void
    {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Unload module and close it window
        var allWindows : Array = (desktop.windowContainer.windowManager.windowList as Array).concat();

        for each (var window : MDIWindow in allWindows)
        {
            if (window is ModuleWindow && (window as ModuleWindow).module.name == module.name)
            {
                stopModuleByWindow(window as ModuleWindow);
                desktop.windowContainer.windowManager.remove(window);
            }
        }
    }

    /**
     * Load the icon at the url and put it in the icon cache.
     */
    public static function cacheIcon(url : String) : void
    {
        IconUtility.getClass(FlexGlobals.topLevelApplication as UIComponent, url);
    }

     /**
     * Erase the icon from the icon cache.
     */
    public static function deleteCacheIcon(url : String) : void
    {
        IconUtility.deleteSource(url);
    }

    /**
     * Subscribe a gravity consumer to the kerneos topic
     */
    public static function subscribe() : void
    {
        consumer = new Consumer();
        consumer.channelSet = KerneosLifeCycleManager.amfGravityChannelSet;
        consumer.destination = "kerneos-gravity";
        consumer.topic = "kerneos/config";
        consumer.addEventListener(ChannelFaultEvent.FAULT, onFault);
        consumer.addEventListener(MessageEvent.MESSAGE, onModuleEventMessage);
        consumer.subscribe();
    }

    // =========================================================================
    // Private methods
    // =========================================================================

    /**
     * Check that the desktop view is referenced.
     */
    private static function checkDesktopNotNull(e : Event = null) : void
    {
        if (desktop == null)
        {
            throw new Error('the "desktop" property must be assigned before calling the modules' + ' life cycle manager methods.');
        }
    }



    /**
     * Setup the modules.
     */
    private static function installModulesCollection(modules : ArrayCollection) : void
    {
        var serviceLocator : ServiceLocator = ServiceLocator.getInstance();
        var serviceIds : ArrayCollection = new ArrayCollection();

        // For each module
        for each (var module : ModuleVO in modules)
        {
            installModule(module);
        }

    }

    /**
     * Delete the modules setup
     */
    private static function uninstallModulesCollection(modules : ArrayCollection) : void
    {
        var serviceLocator : ServiceLocator = ServiceLocator.getInstance();
        var serviceIds : ArrayCollection = new ArrayCollection();

        // For each module
        for each (var module : ModuleVO in modules)
        {
            uninstallModule(module);
        }
    }

    /**
     * Setup one module
     */
    private static function installModule(module : ModuleVO) : void
    {

        var serviceLocator : ServiceLocator = ServiceLocator.getInstance();
        var serviceIds : ArrayCollection = new ArrayCollection();

        // Cache the icons
        if (module.smallIcon != null)
        {
            cacheIcon(module.smallIcon);
        }

        if (module.bigIcon != null)
        {
            cacheIcon(module.bigIcon);
        }

        // Initialize each SWF module services
        if (module is SWFModuleVO && ((module as SWFModuleVO).services != null))
        {
            var services : ArrayCollection = (module as SWFModuleVO).services.service;

            for (var l : int = 0; l < services.length; l++)
            {
                var service : ServiceVO = services.getItemAt(l) as ServiceVO;
                serviceLocator.setServiceForId(service.id, service.destination);
                serviceIds.addItem(service.id);
            }
        }

        // Call the function recursively for folders
        else if (module is FolderVO && ((module as FolderVO).modules != null) )
        {
            installModulesCollection((module as FolderVO).modules.modulesList);
        }

        // Overload all AMF channels
        for each (var id : String in serviceIds)
        {
            serviceLocator.getRemoteObject(id).channelSet = KerneosLifeCycleManager.amfChannelSet;
        }
    }

    /**
     * Delete module setup
     */
    private static function uninstallModule(module : ModuleVO) : void
    {
        var serviceLocator : ServiceLocator = ServiceLocator.getInstance();

        // Cache the icons
        if (module.smallIcon != null)
        {
            deleteCacheIcon(module.smallIcon);
        }

        if (module.bigIcon != null)
        {
            deleteCacheIcon(module.bigIcon);
        }

        // Initialize each SWF module services
        if (module is SWFModuleVO && ((module as SWFModuleVO).services != null))
        {
            var services : ArrayCollection = (module as SWFModuleVO).services.service;

            for (var l : int = 0; l < services.length; l++)
            {
                var service : ServiceVO = services.getItemAt(l) as ServiceVO;
                serviceLocator.removeServiceForId(service.id);
            }
        }

        // Call the function recursively for folders
        else if (module is FolderVO && ((module as FolderVO).modules != null) )
        {
            uninstallModulesCollection((module as FolderVO).modules.modulesList);
        }
    }



    /**
     * Start the modules that have the "loadOnStartup" option.
     */
    private static function doLoadOnStartupModules(module : ModuleVO) : void
    {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Call the function recursively for folders
        if (module is FolderVO)
        {
            for each(var submodule: ModuleVO in (module as FolderVO).modules.modulesList)
                doLoadOnStartupModules(submodule);
        }

        // If "load on startup", load it
        if (module is ModuleWithWindowVO && (module as ModuleWithWindowVO).loadOnStartup)
        {
            desktop.callLater(startModule, [module]);
        }
    }

    /**
     * Message showed when there is a communication problem from gravity consumer
     */
    private static function onFault(event:Event) : void
    {
        Alert.show(event.toString());
    }

    /**
     * Receive the message from gravity consumer and load or unload the modules
     * @param event ModuleEventVO
     */
    private static function onModuleEventMessage(event:MessageEvent) : void {
        var moduleEvent:ModuleEventVO = event.message.body as ModuleEventVO;
        if (moduleEvent) {
            var model:KerneosModelLocator = KerneosModelLocator.getInstance();
            if (moduleEvent.eventType == ModuleEventVO.LOAD && moduleEvent.module) {
                model.modules.modulesList.addItem(moduleEvent.module as ModuleVO);
                installModule(moduleEvent.module);
            } else {
                unloadModule(moduleEvent.module);
                uninstallModule(moduleEvent.module);

                var moduleLoop:ModuleVO;
                var moduleToDelete:ModuleVO;
                //TODO compare by module ID
                for each(moduleLoop in model.modules.modulesList) {
                    if (moduleLoop.name == moduleEvent.module.name) {
                        moduleToDelete = moduleLoop;
                    }
                }

                var moduleIndex:int = model.modules.modulesList.getItemIndex(moduleToDelete);
                model.modules.modulesList.removeItemAt(moduleIndex);
            }
        }
    }
}
}
