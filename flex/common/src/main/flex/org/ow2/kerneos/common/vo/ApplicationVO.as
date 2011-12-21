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
 * $Id$
 */

package org.ow2.kerneos.common.vo {

import com.adobe.cairngorm.vo.IValueObject;

import mx.collections.ArrayCollection;


/**
 * A data object that describes a configuration for Kerneos.
 *
 * @author Julien Nicoulaud
 */

[Bindable]
public class ApplicationVO implements IValueObject {
    // =========================================================================
    // Properties
    // =========================================================================

    // Settings

    /**
     * The name of the project (totally random example: "JASMINe").
     */
    public var applicationProject:String = "JASMINe";

    /**
     * The name of the application.
     */
    public var applicationName:String = "Kerneos";

    /**
     * The url of the application
     */
    public var applicationUrl:String = "/kerneos";

    /**
     * The path to the application logo (64x64 image).
     */
    public var applicationLogo:String = null;

    /**
     * Allow to show the taskbar "Minimize all" button.
     */
    public var showMinimizeAllButton:Boolean = true;

    /**
     * Allow to show the taskbar "cascade" button.
     */
    public var showCascadeButton:Boolean = true;

    /**
     * Allow to show the taskbar "tile" button.
     */
    public var showTileButton:Boolean = true;

    /**
     * Allow to show notification popups.
     */
    public var showNotificationPopups:Boolean = true;

    /**
     * Allow to show notification popups, even on windows that have the focus.
     */
    public var showPopupsFromActiveWindow:Boolean = false;

    /**
     * Enable notifications logging.
     */
    public var enableNotificationsLog:Boolean = true;

    /**
     * Show a "confirm close" dialog when closing the browser window.
     */
    public var showConfirmCloseDialog:Boolean = true;

    /**
     * Set the default language used in Kerneos when loading.
     */
    public var defaultLocale:String;


    /**
     * Set defined languages
     */
    [ArrayElementType('org.ow2.kerneos.common.vo.LanguageVO')]
    public var languages:ArrayCollection = null;

    /**
     * Set the default language used in Kerneos when loading.
     */
    public var authentication:AuthenticationVO;

    /**
     * Stub
     */
    public var managers:Object;

    /**
     * Constructor
     */
    function ApplicationVO() {

    }
}
}
