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
package org.ow2.kerneos.core.managers {

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
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageEvent;
import mx.resources.ResourceManager;
import mx.rpc.events.FaultEvent;
import mx.utils.UIDUtil;

import org.granite.gravity.Consumer;
import org.ow2.kerneos.common.event.KerneosNotificationEvent;
import org.ow2.kerneos.common.event.ServerSideExceptionEvent;
import org.ow2.kerneos.common.managers.ErrorManager;
import org.ow2.kerneos.common.managers.LanguagesManager;
import org.ow2.kerneos.common.view.ServerSideException;
import org.ow2.kerneos.core.event.KerneosConfigEvent;
import org.ow2.kerneos.core.model.KerneosModelLocator;
import org.ow2.kerneos.core.view.DesktopView;
import org.ow2.kerneos.core.view.window.FolderWindow;
import org.ow2.kerneos.core.view.window.IFrameModuleWindow;
import org.ow2.kerneos.core.view.window.MinimizedModuleWindow;
import org.ow2.kerneos.core.view.window.ModuleWindow;
import org.ow2.kerneos.core.view.window.SwfModuleWindow;
import org.ow2.kerneos.core.vo.FolderVO;
import org.ow2.kerneos.core.vo.IFrameModuleVO;
import org.ow2.kerneos.core.vo.LinkVO;
import org.ow2.kerneos.core.vo.ModuleEventVO;
import org.ow2.kerneos.core.vo.ModuleVO;
import org.ow2.kerneos.core.vo.ModuleWithWindowVO;
import org.ow2.kerneos.core.vo.SWFModuleVO;


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
public class ModulesLifeCycleManager {

    // =========================================================================
    // Properties
    // =========================================================================

    /**
     * The desktop view on which operations are done.
     *
     * Must be set before calling the static functions.
     */
    private static var desktop:DesktopView = null;

    /**
     * The IFrame  objects
     */
    private static var frames:Dictionary = new Dictionary();

    /**
     * The gravity consumer for asynchronous OSGi communication
     */
    private static var consumer:Consumer = null;


    // =========================================================================
    // Public static methods
    // =========================================================================

    public static function init(desktop:DesktopView):void {
        ModulesLifeCycleManager.desktop = desktop;
    }

    /**
     * Launch the command to load the application modules.
     */
    public static function loadModules():void {
        try {
            var event_module:KerneosConfigEvent = new KerneosConfigEvent(KerneosConfigEvent.GET_MODULES);
            CairngormEventDispatcher.getInstance().dispatchEvent(event_module);
        }
        catch (e:Error) {
            trace("An error occurred while loading module list: " + e.message);
        }
    }

    /**
     * Unload all application modules
     */
    public static function unloadModules():void {
        // Uninstall all the modules
        while (KerneosModelLocator.getInstance().modules.length) {
            uninstallModule(KerneosModelLocator.getInstance().modules.getItemAt(0) as ModuleVO);
        }
    }

    /**
     * Get a module by his id.
     */
    public static function getModule(id:String):ModuleVO {
        for each (var module:ModuleVO in KerneosModelLocator.getInstance().modules) {
            if (module.id == id)
                return module;
        }
        return null;
    }

    /**
     * Setup a module
     */
    public static function installModule(module:ModuleVO, notify:Boolean = false):void {

        // Call the function recursively for folders
        if (module is FolderVO && ((module as FolderVO).modules != null)) {
            installModules((module as FolderVO).modules);
        }

        // Add to the module list.
        KerneosModelLocator.getInstance().modules.addItem(module);

        // If "load on startup", load it
        if (module is ModuleWithWindowVO && (module as ModuleWithWindowVO).loadOnStartup) {
            desktop.callLater(openModule, [module]);
        }
    }

    /**
     * Uninstall module
     */
    public static function uninstallModule(module:ModuleVO, notify:Boolean = false):void {

        // Remove from the module list.
        var model:KerneosModelLocator = KerneosModelLocator.getInstance();
        for (var index:int = 0; index < model.modules.length; index++) {
            var mod:ModuleVO = model.modules[index] as ModuleVO;
            if (mod.name == module.name) {
                model.modules.removeItemAt(index);
                break;
            }
        }

        // Call the function recursively for folders
        if (module is FolderVO && ((module as FolderVO).modules != null)) {
            uninstallModules((module as FolderVO).modules);
        }
    }

    /**
     * Setup the modules.
     */
    public static function installModules(modules:ArrayCollection):void {
        // For each module
        for each (var module:ModuleVO in modules) {
            installModule(module);
        }
    }

    /**
     * Delete the modules setup
     */
    public static function uninstallModules(modules:ArrayCollection):void {
        // For each module
        for each (var module:ModuleVO in modules) {
            uninstallModule(module);
        }
    }

    /**
     * Start a module.
     */
    public static function openModule(module:ModuleVO):void {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Show a busy cursor
        desktop.cursorManager.setBusyCursor();

        // If this is a module with its own window
        if (module is ModuleWithWindowVO) {
            // Update the module status
            module.loaded = true;

            // Declare a new window
            var window:ModuleWindow;

            // If this is a swf module
            if (module is SWFModuleVO) {
                // Create a window
                window = new SwfModuleWindow();
                window.module = module as SWFModuleVO;

                // Load Module
                (window as SwfModuleWindow).load();

                // Add Notification listener
                window.addEventListener(KerneosNotificationEvent.KERNEOS_NOTIFICATION,
                        NotificationsManager.handleNotificationEvent);
            }

            // Else if this is an IFrame module
            else if (module is IFrameModuleVO) {
                // Create an IFrame (with a unique Id)
                frames[module.name] = new IFrame("KerneosIFrame" + UIDUtil.createUID().split("-").join(""));
                var frame:IFrame = frames[module.name] as IFrame;
                window = new IFrameModuleWindow(module as IFrameModuleVO, frame);
            }

            // Else if this is a folder
            else if (module is FolderVO) {
                window = new FolderWindow(module as FolderVO);
            }

            // Set the window associated with the module
            (module as ModuleWithWindowVO).window = window;

            // Create the button in the taskbar
            var minimizedModuleWindow:MinimizedModuleWindow = new MinimizedModuleWindow();
            minimizedModuleWindow.window = window;
            desktop.minimizedWindowsButtonsContainer.addChild(minimizedModuleWindow);
            window.minimizedModuleWindow = minimizedModuleWindow;

            // Add it to the windows manager
            desktop.windowContainer.windowManager.add(window);
        }

        // Else if this is a simple link
        else if (module is LinkVO) {
            // Open it
            navigateToURL(new URLRequest((module as LinkVO).url), "_blank");
        }

        // Remove the busy cursor
        desktop.cursorManager.removeBusyCursor();
    }

    /**
     * Close a module.
     */
    public static function closeModule(module:ModuleVO):void {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Unload module and close it window
        var allWindows:Array = (desktop.windowContainer.windowManager.windowList as Array).concat();

        for each (var window:MDIWindow in allWindows) {
            if (window is ModuleWindow && (window as ModuleWindow).usingModule(module)) {
                closeModuleByWindow(window as ModuleWindow);
            }
        }
    }

    /**
     * Close a module by its window.
     */
    public static function closeModuleByWindow(window:ModuleWindow):void {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Set focus on desktop
        desktop.setFocus();

        // Remove the tasbar button
        desktop.minimizedWindowsButtonsContainer.removeChild(window.minimizedModuleWindow);
        window.minimizedModuleWindow.window = null;

        // Remove from window manager
        desktop.windowContainer.windowManager.remove(window);
        window.minimizedModuleWindow = null;

        // Stop the module
        stopModuleByWindow(window);
    }

    /**
     * Stop a module.
     */
    public static function stopModule(module:ModuleVO, cause:String = null):void {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Unload module and close it window
        var allWindows:Array = (desktop.windowContainer.windowManager.windowList as Array).concat();

        for each (var window:MDIWindow in allWindows) {
            if (window is ModuleWindow && (window as ModuleWindow).usingModule(module)) {
                stopModuleByWindow(window as ModuleWindow, cause);
            }
        }
    }

    /**
     * Stop a module by its window.
     */
    public static function stopModuleByWindow(window:ModuleWindow, cause:String = null):void {
        // Check if the window is current window of the module
        if (window.module.window == window) {
            if (window is SwfModuleWindow) {
                // Remove Notification listener
                window.removeEventListener(KerneosNotificationEvent.KERNEOS_NOTIFICATION,
                        NotificationsManager.handleNotificationEvent);

                // Unload module
                (window as SwfModuleWindow).unload(cause);
            }
            else if (window is IFrameModuleWindow) {
                // Delete the IFrame
                (window as IFrameModuleWindow).removeIFrame();
            }

            // Clear window associated with the module
            window.module.window = null;
            window.module.loaded = false;

            // Force garbage collection
            System.gc();
        }
    }

    /**
     * Bring a module window to front
     */
    public static function bringToFront(module:ModuleVO):void {
        // Check that desktop is not null
        checkDesktopNotNull();

        // look for the window are give it the focus
        var allWindows:Array = (desktop.windowContainer.windowManager.windowList as Array).concat();

        for each (var window:MDIWindow in allWindows) {
            if (window is ModuleWindow && (window as ModuleWindow).usingModule(module)) {
                (window as ModuleWindow).bringToFront();
                return;
            }
        }
    }

    /**
     * Subscribe a gravity consumer to the kerneos topic
     */
    public static function subscribe():void {
        consumer = ServiceLocator.getInstance().getConsumer("kerneosAsyncConfigService");
        consumer.addEventListener(ChannelFaultEvent.FAULT, onFault);
        consumer.addEventListener(MessageEvent.MESSAGE, onModuleEventMessage);

        consumer.subscribe();
    }

    public static function unsubscribe():void {
        consumer.removeEventListener(ChannelFaultEvent.FAULT, onFault);
        consumer.removeEventListener(MessageEvent.MESSAGE, onModuleEventMessage);

        consumer.unsubscribe();
    }

    // =========================================================================
    // Private methods
    // =========================================================================

    /**
     * Check that the desktop view is referenced.
     */
    private static function checkDesktopNotNull(e:Event = null):void {
        if (desktop == null) {
            throw new Error('the "desktop" property must be assigned before calling the modules' + ' life cycle manager methods.');
        }
    }

    /**
     * Message showed when there is a communication problem from gravity consumer
     */
    private static function onFault(event:Event):void {
        if (!ErrorManager.handleError(event)) {
            if (event is FaultEvent) {
                // Retrieve the fault event
                var faultEvent:FaultEvent = FaultEvent(event);

                var serverSideExceptionEvent:ServerSideExceptionEvent = new ServerSideExceptionEvent(
                        ServerSideExceptionEvent.SERVER_SIDE_EXCEPTION,
                        new ServerSideException(ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, "kerneos.error.moduleEvent.title"),
                                ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, "kerneos.error.moduleEvent", [faultEvent.fault.faultString]),
                                faultEvent.fault.getStackTrace()));
                CairngormEventDispatcher.getInstance().dispatchEvent(serverSideExceptionEvent);
            }
        }
    }

    /**
     * Receive the message from gravity consumer and load or unload the modules
     * @param event ModuleEventVO
     */
    private static function onModuleEventMessage(event:MessageEvent):void {
        var moduleEvent:ModuleEventVO = event.message.body as ModuleEventVO;

        //if the event is a ModuleEvent type and it has a moduleVO
        if (moduleEvent && moduleEvent.module) {
            var model:KerneosModelLocator = KerneosModelLocator.getInstance();

            //if the action is LOAD, the module is installed otherwise it is uninstalled
            var module:ModuleVO = moduleEvent.module;
            if (moduleEvent.eventType == ModuleEventVO.LOAD) {
                installModule(module, true);
            } else {
                uninstallModule(module, true);
            }
        }
    }
}
}
