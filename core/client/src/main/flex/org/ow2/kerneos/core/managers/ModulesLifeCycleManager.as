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
import mx.controls.Alert;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.events.CloseEvent;
import mx.messaging.events.ChannelEvent;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageEvent;
import mx.resources.ResourceManager;
import mx.utils.UIDUtil;

import org.granite.gravity.Consumer;
import org.ow2.kerneos.common.event.KerneosNotificationEvent;
import org.ow2.kerneos.common.util.IconUtility;
import org.ow2.kerneos.core.event.KerneosConfigEvent;
import org.ow2.kerneos.core.model.KerneosModelLocator;
import org.ow2.kerneos.core.model.KerneosState;
import org.ow2.kerneos.core.view.DesktopView;
import org.ow2.kerneos.core.view.notification.NotificationPopUp;
import org.ow2.kerneos.core.view.window.FolderWindow;
import org.ow2.kerneos.core.view.window.IFrameModuleWindow;
import org.ow2.kerneos.core.view.window.MinimizedModuleWindow;
import org.ow2.kerneos.core.view.window.ModuleWindow;
import org.ow2.kerneos.core.view.window.SwfModuleWindow;
import org.ow2.kerneos.core.vo.FolderVO;
import org.ow2.kerneos.core.vo.IFrameModuleVO;
import org.ow2.kerneos.core.vo.KerneosNotification;
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
     * true if the there is a problem with channels or communications
     */
    private static var inFaultState:Boolean = false;

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
            CairngormEventDispatcher.getInstance(desktop).dispatchEvent(event_module);
        }
        catch (e:Error) {
            trace("An error occurred while loading module list: " + e.message);
        }
    }

    /**
     * Unload all application modules
     */
    public static function unloadModules():void {
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

        //Pop-up module arrival
        if (notify && KerneosModelLocator.getInstance().moduleFilterFunction(module)) {
            notifiedModuleArrivalDeparture('kerneos.lifecyclemanager.notification.module.load', module);
        }

        // If "load on startup", load it
        if (module is ModuleWithWindowVO && (module as ModuleWithWindowVO).loadOnStartup) {
            desktop.callLater(startModule, [module]);
        }
    }

    /**
     * Uninstall module
     */
    public static function uninstallModule(module:ModuleVO, notify:Boolean = false):void {

        // Stop module
        stopModule(module);

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

        //Pop-up module departure
        if (notify && KerneosModelLocator.getInstance().moduleFilterFunction(module)) {
            notifiedModuleArrivalDeparture('kerneos.lifecyclemanager.notification.module.unload', module);
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
    public static function startModule(module:ModuleVO):void {
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
                window = new SwfModuleWindow(module as SWFModuleVO);

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
            var minimizedModuleWindow:MinimizedModuleWindow = new MinimizedModuleWindow(window);
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
     * Stop a module.
     */
    public static function stopModule(module:ModuleVO):void {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Unload module and close it window
        var allWindows:Array = (desktop.windowContainer.windowManager.windowList as Array).concat();

        for each (var window:MDIWindow in allWindows) {
            if (window is ModuleWindow && (window as ModuleWindow).module.name == module.name) {
                stopModuleByWindow(window as ModuleWindow);
                desktop.windowContainer.windowManager.remove(window);
            }
        }
    }

    /**
     * Stop a module by its window.
     */
    public static function stopModuleByWindow(window:ModuleWindow):void {
        // Check that desktop is not null
        checkDesktopNotNull();

        // Clear window associated with the module
        window.module.window = null;

        // Remove the tasbar button
        desktop.minimizedWindowsButtonsContainer.removeChild(window.minimizedModuleWindow);

        if (window is SwfModuleWindow) {
            // Remove Notification listener
            window.removeEventListener(KerneosNotificationEvent.KERNEOS_NOTIFICATION,
                    NotificationsManager.handleNotificationEvent);

            // Unload module
            (window as SwfModuleWindow).unload();

            // Unload the module
            ModulesLifeCycleManager.desktop.setFocus();
        }
        else if (window is IFrameModuleWindow) {
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
    public static function bringToFront(module:ModuleVO):void {
        // Check that desktop is not null
        checkDesktopNotNull();

        // look for the window are give it the focus
        var allWindows:Array = (desktop.windowContainer.windowManager.windowList as Array).concat();

        for each (var window:MDIWindow in allWindows) {
            if (window is ModuleWindow && (window as ModuleWindow).module.name == module.name) {
                (window as ModuleWindow).bringToFront();
                return;
            }
        }
    }

    /**
     * Subscribe a gravity consumer to the kerneos topic
     */
    public static function subscribe():void {
        consumer = ServiceLocator.getInstance(null).getConsumer("kerneosAsyncConfigService");
        consumer.addEventListener(ChannelFaultEvent.FAULT, onFault);
        consumer.addEventListener(MessageEvent.MESSAGE, onModuleEventMessage);
        consumer.addEventListener(ChannelEvent.CONNECT, onChannelConnection);
        consumer.addEventListener(ChannelEvent.DISCONNECT, onChannelDisconnect);

        consumer.subscribe();
    }

    public static function unsubscribe():void {
        consumer.removeEventListener(ChannelFaultEvent.FAULT, onFault);
        consumer.removeEventListener(MessageEvent.MESSAGE, onModuleEventMessage);
        consumer.removeEventListener(ChannelEvent.CONNECT, onChannelConnection);
        consumer.removeEventListener(ChannelEvent.DISCONNECT, onChannelDisconnect);

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
        if (KerneosModelLocator.getInstance().state != KerneosState.DESKTOP &&
                KerneosModelLocator.getInstance().state != KerneosState.DISCONNECTED) {
            Alert.show(ResourceManager.getInstance().getString(
                    LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.startup.channel-problem'));
        } else {
            if (!inFaultState) {
                inFaultState = true;

                KerneosModelLocator.getInstance().state = KerneosState.DISCONNECTED;

                Alert.show(ResourceManager.getInstance().getString(
                        LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.desktop.channel-problem.missing-dependency'),
                        ResourceManager.getInstance().getString(
                                LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.desktop.channel-problem.missing-dependency.title'),
                        Alert.OK | Alert.NO, null,
                        channelDisconnectListener, null, Alert.OK);

            }

        }
    }

    private static function onChannelDisconnect(event:Event):void {
        if (!inFaultState) {
            Alert.show(ResourceManager.getInstance().getString(
                    LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.desktop.channel-problem.missing-dependency'),
                    ResourceManager.getInstance().getString(
                            LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.desktop.channel-problem.missing-dependency.title'),
                    Alert.OK | Alert.NO, null,
                    channelDisconnectListener, null, Alert.OK);
        }

        inFaultState = true;
    }

    private static function onChannelConnection(event:Event):void {
        if (inFaultState) {
            //TODO Have to get back to normal state
            FlexGlobals.topLevelApplication.enabled = true;
        }

        inFaultState = false;

        if (KerneosModelLocator.getInstance().state == KerneosState.DISCONNECTED) {
            KerneosModelLocator.getInstance().state = KerneosState.DESKTOP;
        }
    }

    private static function channelDisconnectListener(eventObj:CloseEvent):void {
        // Check to see if the OK button was pressed.
        if (eventObj.detail == Alert.OK) {
            FlexGlobals.topLevelApplication.enabled = false;
        } else {
            KerneosLifeCycleManager.reloadPage();
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

    /**
     * Shows un pop-up in the main view
     * @param messageCode The locale message defined into properties file
     * @param parameters The parameters to be replaced in the message
     */
    private static function notifiedModuleArrivalDeparture(messageCode:String, module:ModuleVO):void {
        // Build and display the popup
        var notifPopUp:NotificationPopUp = new NotificationPopUp();
        notifPopUp.message = ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE,
                messageCode, [module.name]);
        notifPopUp.level = KerneosNotification.INFO;
        notifPopUp.setStyle("bottom", 0);
        notifPopUp.setStyle("right", 0);
        notifPopUp.module = module;

        NotificationsManager.desktop.windowContainer.addChild(notifPopUp);
    }
}
}
