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
package org.ow2.kerneos.core.managers {
import com.adobe.cairngorm.business.ServiceLocator;
import com.adobe.cairngorm.control.CairngormEventDispatcher;

import flash.events.Event;
import flash.net.URLRequest;
import flash.net.navigateToURL;

import mx.core.FlexGlobals;
import mx.messaging.ChannelSet;
import mx.utils.URLUtil;

import org.granite.channels.GraniteOSGiChannel;
import org.granite.gravity.channels.GravityOSGiChannel;
import org.granite.util.GraniteClassRegistry;

import org.ow2.kerneos.common.util.StringUtils;
import org.ow2.kerneos.core.event.KerneosConfigEvent;
import org.ow2.kerneos.core.model.KerneosModelLocator;
import org.ow2.kerneos.core.model.ModulesState;
import org.ow2.kerneos.core.model.KerneosState;
import org.ow2.kerneos.core.view.DesktopView;
import org.ow2.kerneos.common.vo.ApplicationVO;
import org.ow2.kerneos.common.vo.AuthenticationVO;
import org.ow2.kerneos.core.vo.ConfigVOObjects;
import org.ow2.kerneos.login.model.LoginModelLocator;
import org.ow2.kerneos.login.model.LoginState;
import org.ow2.kerneos.core.vo.SecurityVOObjects;
import org.ow2.kerneos.profile.model.ProfileModelLocator;
import org.ow2.kerneos.profile.model.ProfileState;


/**
 * Manages the application life cycle.
 *
 * @author Julien Nicoulaud
 */
public class KerneosLifeCycleManager {

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
     * AMF Channel set.
     */
    public static var amfChannelSet:ChannelSet = null;

    /**
     * AMF Gravity Channel set
     */
    public static var amfGravityChannelSet:ChannelSet = null;


    // =========================================================================
    // Public methods
    // =========================================================================

    public static function init(desktop:DesktopView):void {
        KerneosLifeCycleManager.desktop = desktop;
    }

    /**
     * Setup the AMF channel and Kerneos services.
     */
    public static function setupKerneosServices(e:Event = null):void {

        // Init client-server communications channels properties
        var urlServer:String = URLUtil.getServerNameWithPort(FlexGlobals.topLevelApplication.systemManager.stage.loaderInfo.url);
        var context:String = StringUtils.parseURLContext(FlexGlobals.topLevelApplication.systemManager.stage.loaderInfo.url);
        var application:String = FlexGlobals.topLevelApplication.systemManager.stage.loaderInfo.parameters.application;

        // Granite ChannelSet
        amfChannelSet = new ChannelSet();
        var amfChannel:GraniteOSGiChannel = new GraniteOSGiChannel("kerneos-graniteamf-" + application,
                "http://" + urlServer + "/" + context + "/granite/amf");
        amfChannelSet.addChannel(amfChannel);

        // Gravity ChannelSet
        amfGravityChannelSet = new ChannelSet();
        var amfGravityChannel:GravityOSGiChannel = new GravityOSGiChannel("kerneos-gravityamf-" + application,
                "http://" + urlServer + "/" + context + "/gravity/amf");
        amfGravityChannelSet.addChannel(amfGravityChannel);

        var serviceLocator:ServiceLocator = ServiceLocator.getInstance();

        // Set the kerneosConfigService.
        serviceLocator.setServiceForId("kerneosConfigService", "kerneos-configuration", false);
        GraniteClassRegistry.registerClasses("kerneos-configuration", ConfigVOObjects.values());

        // Set the kerneosAsyncConfigService.
        serviceLocator.setServiceForId("kerneosAsyncConfigService", "kerneos-async-configuration", true);
        GraniteClassRegistry.registerClasses("kerneos-async-configuration", ConfigVOObjects.values());

        // Set the kerneosSecurityService.
        serviceLocator.setServiceForId("kerneosSecurityService", "kerneos-security", false);
        GraniteClassRegistry.registerClasses("kerneos-security", SecurityVOObjects.values());

        // Set the kerneosAsyncSecurityService.
        serviceLocator.setServiceForId("kerneosAsyncProfileService", "kerneos-async-security", true);
        GraniteClassRegistry.registerClasses("kerneos-async-security", SecurityVOObjects.values());

        // Configure the services
        serviceLocator.getRemoteObject("kerneosConfigService").channelSet = amfChannelSet;

        serviceLocator.getConsumer("kerneosAsyncConfigService").channelSet = amfGravityChannelSet;
        serviceLocator.getConsumer("kerneosAsyncConfigService").topic = "kerneos/config";

        serviceLocator.getRemoteObject("kerneosSecurityService").channelSet = amfChannelSet;

        serviceLocator.getConsumer("kerneosAsyncProfileService").channelSet = amfGravityChannelSet;
        serviceLocator.getConsumer("kerneosAsyncProfileService").topic = "kerneos/profile/" + application;
    }


    /**
     * Launch the command to load the application.
     */
    public static function loadApplication():void {
        try {
            var event_module:KerneosConfigEvent = new KerneosConfigEvent(KerneosConfigEvent.GET_APPLICATION);
            CairngormEventDispatcher.getInstance().dispatchEvent(event_module);
        }
        catch (e:Error) {
            trace("An error occurred while loading Kerneos config file: " + e.message);
        }
    }

    /**
     * Set the application instance
     */
    public static function setApplication(application:ApplicationVO):void {
        KerneosModelLocator.getInstance().application = application;
    }


    /**
     * Return true if the application can be closed without prompting.
     *
     * @internal Check module per module if it declares itself as ready to be closed
     */
    public static function canBeClosed():Boolean {
        if (KerneosModelLocator.getInstance().state != KerneosState.LOADING) {
            // Save windows setup
            if (desktop != null) {
                desktop.saveAllWindowsSetup();
            }

            // Save the user settings
            SharedObjectManager.save();
        }

        if (KerneosModelLocator.getInstance().state != KerneosState.LOADING &&
                KerneosModelLocator.getInstance().state != KerneosState.LOGIN) {
            return (!KerneosModelLocator.getInstance().application.showConfirmCloseDialog);
        }

        return true;
    }

    /**
     * New State handling
     */
    public static function stateChanged():void {
        var applicationState:String = KerneosModelLocator.getInstance().state;
        if (applicationState != KerneosState.LOADING && applicationState != KerneosState.INIT && applicationState != KerneosState.ERROR) {
            var loginState:String = LoginModelLocator.getInstance().state;
            if (loginState == LoginState.IDLE) {
                if (!KerneosModelLocator.getInstance().application.authentication.equals(AuthenticationVO.FLEX)) {
                    LoginModelLocator.getInstance().state = LoginState.LOGGED;
                } else {
                    LoginModelLocator.getInstance().state = LoginState.AUTH;
                }
            } else if (loginState == LoginState.LOGGED) {
                var profileState:String = ProfileModelLocator.getInstance().state;
                if (profileState == ProfileState.IDLE) {
                    ProfileModelLocator.getInstance().state = ProfileState.LOAD;
                }
                else if (profileState == ProfileState.LOADED) {
                    var modulesState:String = KerneosModelLocator.getInstance().modulesState;
                    if (modulesState == ModulesState.IDLE) {
                        KerneosModelLocator.getInstance().modulesState = ModulesState.LOAD;
                    }
                    else if (modulesState == ModulesState.LOADED) {
                        KerneosModelLocator.getInstance().state = KerneosState.DESKTOP;
                    }
                    else {
                        KerneosModelLocator.getInstance().state = KerneosState.MODULES;
                    }
                }
                else {
                    KerneosModelLocator.getInstance().state = KerneosState.PROFILE;
                }
            } else {
                KerneosModelLocator.getInstance().state = KerneosState.LOGIN;
            }
        }
    }

    /**
     * Logout from the application.
     */
    public static function logout(event:Event = null):void {
        KerneosModelLocator.getInstance().modulesState = ModulesState.UNLOAD;
        ProfileModelLocator.getInstance().state = ProfileState.UNLOAD;
        LoginModelLocator.getInstance().state = LoginState.LOGOUT;
    }


    /**
     * Reload the page.
     */
    public static function reloadPage():void {
        navigateToURL(new URLRequest("javascript:location.reload();"), "_self");
    }
}
}
