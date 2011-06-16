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
import org.ow2.kerneos.core.model.KerneosState;
import org.ow2.kerneos.core.view.DesktopView;
import org.ow2.kerneos.core.vo.ApplicationInstanceVO;
import org.ow2.kerneos.core.vo.ApplicationVO;
import org.ow2.kerneos.core.vo.AuthenticationVO;
import org.ow2.kerneos.core.vo.FolderVO;
import org.ow2.kerneos.core.vo.IFrameModuleVO;
import org.ow2.kerneos.core.vo.KerneosNotification;
import org.ow2.kerneos.core.vo.LinkVO;
import org.ow2.kerneos.core.vo.ModuleEventVO;
import org.ow2.kerneos.core.vo.ModuleInstanceVO;
import org.ow2.kerneos.core.vo.ModuleVO;
import org.ow2.kerneos.core.vo.ModuleWithWindowVO;
import org.ow2.kerneos.core.vo.PromptBeforeCloseVO;
import org.ow2.kerneos.core.vo.SWFModuleVO;
import org.ow2.kerneos.core.vo.ServiceVO;
import org.ow2.kerneos.login.model.LoginModelLocator;
import org.ow2.kerneos.login.model.LoginState;


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
        // Retrieve the application model
        var model:KerneosModelLocator = KerneosModelLocator.getInstance();

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

        // Set the kerneosSecurityService.
        var serviceLocator: ServiceLocator = ServiceLocator.getInstance(null);
        serviceLocator.setServiceForId("kerneosSecurityService", "kerneos-security", false);
        GraniteClassRegistry.registerClasses("security", []);

        var kerneosConfigurationClasses = [ApplicationInstanceVO, ApplicationVO, AuthenticationVO, FolderVO,
            IFrameModuleVO, KerneosNotification, LinkVO, ModuleEventVO, ModuleInstanceVO, ModuleVO, ModuleWithWindowVO,
            PromptBeforeCloseVO, ServiceVO, SWFModuleVO];

        // Set the kerneosConfigService.
        serviceLocator.setServiceForId("kerneosConfigService", "kerneos-configuration", false);
        GraniteClassRegistry.registerClasses("kerneos-configuration", kerneosConfigurationClasses);

        // Set the kerneosAsyncConfigService.
        serviceLocator.setServiceForId("kerneosAsyncConfigService", "kerneos-async-configuration", true);
        GraniteClassRegistry.registerClasses("kerneos-async-configuration", kerneosConfigurationClasses);

        serviceLocator.getRemoteObject("kerneosSecurityService").channelSet = amfChannelSet;
        serviceLocator.getRemoteObject("kerneosConfigService").channelSet = amfChannelSet;
        serviceLocator.getConsumer("kerneosAsyncConfigService").channelSet = amfGravityChannelSet;
    }


    /**
     * Launch the command to load the application configuration.
     */
    public static function loadApplication():void {
        try {
            var event_module:KerneosConfigEvent = new KerneosConfigEvent(KerneosConfigEvent.GET_APPLICATION);
            CairngormEventDispatcher.getInstance(null).dispatchEvent(event_module);
        }
        catch (e:Error) {
            trace("An error occurred while loading Kerneos config file: " + e.message);
        }
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
            return (!KerneosModelLocator.getInstance().applicationInstance.configuration.showConfirmCloseDialog);
        }

        return true;
    }


    /**
     * Logout from the application.
     */
    public static function logout(event:Event = null):void {
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
