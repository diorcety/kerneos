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
package org.ow2.kerneos.core.model {

/**
 * An enumerated type representing the states of Kerneos.
 *
 * @author Julien Nicoulaud
 */
[Bindable]
public class KerneosState {

    /**
     * Initial state.
     */
    public static var INIT:String = "init";

    /**
     * When the application is loading.
     */
    public static var LOADING:String = "loading";

    /**
     * When the config is loaded.
     */
    public static var APPLICATION_LOADED:String = "applicationLoaded";

    /**
     * When the user have to login.
     */
    public static var LOGIN:String = "login";

    /**
     * When the application is loaded.
     */
    public static var PROFILE:String = "profile";

    /**
     * When the desktop is displayed.
     */
    public static var DESKTOP:String = "desktop";

    /**
     * When the connexion to the server has been lost.
     */
    public static var DISCONNECTED:String = "disconnected";

}
}
