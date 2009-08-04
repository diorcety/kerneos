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

/**
 * A configuration of Kerneos
 *
 * @author Julien Nicoulaud
 */
public class KerneosConfig {

    // Settings

    /**
     * The name of the project (totally random example: "JASMINe")
     */
    public String consoleProject = "JASMINe";

    /**
     * The name of the console
     */
    public String consoleName = "Kerneos";

    /**
    * Allow to show the taskbar "Minimize all" icon
    */
    public Boolean showMinimizeAllIcon = true;

    /**
    * Allow to show the taskbar "cascade" icon
    */
    public Boolean showCascadeIcon = true;

    /**
    * Allow to show the taskbar "tile" icon
    */
    public Boolean showTileIcon = true;

    /**
    * Allow to show notification popups
    */
    public Boolean showNotificationPopUps = true;

    /**
    * Allow to show notification popups, even on windows that have the focus
    */
    public Boolean showPopupsWhenFocused = false;

    /**
    * Enable notifications logging
    */
    public Boolean enableNotificationsLog = true;

    /**
    * Show a "confirm close" dialog when closing the browser window
    */
    public Boolean showConfirmCloseDialog = true;


    // Modules

    /**
     * The modules
     */
    public List<Module> modules = new ArrayList<Module>();

}
