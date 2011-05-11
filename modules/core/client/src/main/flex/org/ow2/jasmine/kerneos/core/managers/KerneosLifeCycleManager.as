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
package org.ow2.jasmine.kerneos.core.managers {
import com.adobe.cairngorm.business.ServiceLocator;
import com.adobe.cairngorm.control.CairngormEventDispatcher;

import flash.events.Event;
import flash.net.URLLoader;
import flash.net.URLRequest;
import flash.net.navigateToURL;

import mx.core.FlexGlobals;
import mx.messaging.ChannelSet;
import mx.messaging.channels.AMFChannel;
import mx.utils.URLUtil;

import org.granite.channels.GraniteChannel;
import org.granite.gravity.channels.GravityChannel;

import org.granite.util.GraniteClassRegistry;
import org.ow2.jasmine.kerneos.common.util.StringUtils;
import org.ow2.jasmine.kerneos.core.event.KerneosConfigEvent;
import org.ow2.jasmine.kerneos.core.model.KerneosModelLocator;
import org.ow2.jasmine.kerneos.core.model.KerneosState;
import org.ow2.jasmine.kerneos.core.view.DesktopView;
import org.ow2.jasmine.kerneos.core.vo.IFrameModuleVO;
import org.ow2.jasmine.kerneos.core.vo.KerneosConfigVO;
import org.ow2.jasmine.kerneos.core.vo.ModuleEventVO;
import org.ow2.jasmine.kerneos.core.vo.ModuleVO;
import org.ow2.jasmine.kerneos.core.vo.ModulesVO;
import org.ow2.jasmine.kerneos.core.vo.PromptBeforeCloseVO;
import org.ow2.jasmine.kerneos.core.vo.SWFModuleVO;
import org.ow2.jasmine.kerneos.core.vo.ServiceVO;
import org.ow2.jasmine.kerneos.core.vo.ServicesVO;
import org.ow2.jasmine.kerneos.core.vo.SharedLibrariesVO;


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
    [Bindable]
    public static var desktop:DesktopView = null;

    /**
     * Wether the logout() function has been called.
     */
    private static var loggingOut:Boolean = false;

    /**
     * AMF Channel set.
     */
    [Bindable]
    public static var amfChannelSet:ChannelSet = null;

    public static var amfGravityChannelSet:ChannelSet = null;


    // =========================================================================
    // Public methods
    // =========================================================================

    /**
     * Setup the AMF channel and Kerneos services.
     */
    public static function setupKerneosServices(e:Event = null):void {
        // Retrieve the application model
        var model:KerneosModelLocator = KerneosModelLocator.getInstance();

        // Init client-server communications channels properties
        var urlServer:String = URLUtil.getServerNameWithPort(FlexGlobals.topLevelApplication.systemManager.stage.loaderInfo.url);
        var context:String = StringUtils.parseURLContext(FlexGlobals.topLevelApplication.systemManager.stage.loaderInfo.url);


        // Granite ChannelSet
        amfChannelSet = new ChannelSet();
        var amfChannel:GraniteChannel = new GraniteChannel("my-graniteamf-kerneos", "http://" + urlServer + "/" + context + "/granite/amf");
        amfChannelSet.addChannel(amfChannel);

        // Gravity ChannelSet
        amfGravityChannelSet = new ChannelSet();
        var amfGravityChannel:GravityChannel = new GravityChannel("my-gravityamf-kerneos", "http://" + urlServer + "/" + context + "/gravity/amf");
        amfGravityChannelSet.addChannel(amfGravityChannel);

        // Set the kerneosConfigService. Done this way because of the @remoteDestination on the JAVA service
        ServiceLocator.getInstance().setServiceForId("kerneosConfigService", "kerneosConfig");

        // ServiceLocator.getInstance().getRemoteObject("logInService").channelSet = amfChannelSet;
        ServiceLocator.getInstance().getRemoteObject("kerneosConfigService").channelSet = amfChannelSet;

    }


    /**
     * Launch the command to load the application configuration.
     */
    public static function getKerneosConfig():void {
        try {
            var event_module:KerneosConfigEvent = new KerneosConfigEvent(KerneosConfigEvent.GET_KERNEOS_CONFIG);
            CairngormEventDispatcher.getInstance().dispatchEvent(event_module);
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

            return (loggingOut || !KerneosModelLocator.getInstance().config.showConfirmCloseDialog);
        }
        return true;
    }


    /**
     * Logout from the application.
     */
    public static function logout(event:Event = null):void {
        // Call the logout servlet
        var req:URLRequest = new URLRequest("./LogoutServlet");
        var loader:URLLoader = new URLLoader();
        loader.addEventListener(Event.COMPLETE, function():void {
            // Mark this as logging out
            loggingOut = true;

            // Reload the page
            reloadPage();
        });
        loader.load(req);
    }


    /**
     * Reload the page.
     */
    public static function reloadPage():void {
        navigateToURL(new URLRequest("javascript:location.reload();"), "_self");
    }

}
}
