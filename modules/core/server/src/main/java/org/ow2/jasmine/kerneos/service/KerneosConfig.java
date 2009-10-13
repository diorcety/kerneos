/**
 * JASMINe
 * Copyright (C) 2009 Bull S.A.S.
 * Contact: jasmine@ow2.org
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
package org.ow2.jasmine.kerneos.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A configuration of Kerneos
 *
 * @author Julien Nicoulaud
 */
@XmlRootElement(name = "kerneos-config", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
public class KerneosConfig {

    /**
     * The namespace for Kerneos XML configuration files.
     */
    public static final String KERNEOS_CONFIG_NAMESPACE = "org.ow2.jasmine.kerneos:kerneos-config";


    // Settings

    /**
     * The name of the project (totally random example: "JASMINe").
     */
    @XmlElement(name = "application-project", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public String applicationProject = "JASMINe";

    /**
     * The name of the application.
     */
    @XmlElement(name = "application-name", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public String applicationName = "Kerneos";

    /**
     * The path to the application logo (64x64 image).
     */
    @XmlElement(name = "application-logo", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public String applicationLogo = null;

    /**
    * Allow to show the taskbar "Minimize all" button.
    */
    @XmlElement(name = "show-minimize-all-button", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean showMinimizeAllButton = true;

    /**
    * Allow to show the taskbar "cascade" button.
    */
    @XmlElement(name = "show-cascade-button", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean showCascadeButton = true;

    /**
    * Allow to show the taskbar "tile" button.
    */
    @XmlElement(name = "show-tile-button", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean showTileButton = true;

    /**
    * Allow to show notification popups.
    */
    @XmlElement(name = "show-notification-popups", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean showNotificationPopUps = true;

    /**
    * Allow to show notification popups, even from the window that has the focus.
    */
    @XmlElement(name = "show-popups-from-active-window", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean showPopupsFromActiveWindow = false;

    /**
    * Enable notifications logging.
    */
    @XmlElement(name = "enable-notifications-log", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean enableNotificationsLog = true;

    /**
    * Show a "confirm close" dialog when closing the browser window.
    */
    @XmlElement(name = "show-confirm-close-dialog", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean showConfirmCloseDialog = true;

    /**
     * Set the default language used in Kerneos when loading.
     */
    @XmlElement(name = "default-language", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public String defaultLanguage = "en_US";

    // Modules

    /**
     * The modules
     */
    @XmlElementWrapper(name = "modules", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    @XmlAnyElement(lax = true)
    @XmlElementRefs({
        @XmlElementRef(type = SWFModule.class, name = "swf-module", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE),
        @XmlElementRef(type = IFrameModule.class, name = "iframe-module", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    })
    public List<Module> modules = new ArrayList<Module>();


    /**
     * Output a String preview of the object.
     */
    @Override
    public String toString() {
        String res = new String();
        res += "[KerneosConfig: " + applicationProject + " " + applicationName + "]";
        res += "\n\t[Options]";
        res += "\n\t\t* applicationLogo: " + applicationLogo;
        res += "\n\t\t* showMinimizeAllButton: " + showMinimizeAllButton;
        res += "\n\t\t* showCascadeButton: " + showCascadeButton;
        res += "\n\t\t* showTileButton: " + showTileButton;
        res += "\n\t\t* showNotificationPopUps: " + showNotificationPopUps;
        res += "\n\t\t* showPopupsFromActiveWindow: " + showPopupsFromActiveWindow;
        res += "\n\t\t* enableNotificationsLog: " + enableNotificationsLog;
        res += "\n\t\t* showConfirmCloseDialog: " + showConfirmCloseDialog;
        res += "\n\t\t* defaultLanguage: " + defaultLanguage;
        res += "\n\t[Modules]";
        for (Module module : modules) {
            res += "\n\t\t" + module.toString();
        }

        return res;
    }
}
