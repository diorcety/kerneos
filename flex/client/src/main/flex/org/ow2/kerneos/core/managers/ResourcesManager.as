/**
 * Kerneos
 * Copyright (C) 2011 Bull S.A.S.
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
import mx.core.FlexGlobals;
import mx.resources.ResourceManager;

import org.ow2.kerneos.core.model.KerneosModelLocator;

public class ResourcesManager {
    public function ResourcesManager() {
    }

    public static function init():void {

    }

    public static function loadLocale():void {
        // Retrieve the user saved setting.
        var userActiveLanguage:String = SharedObjectManager.getActiveLanguage();

        // If the user has a memorized setting, restore it.
        if (userActiveLanguage != null) {
            ResourceManager.getInstance().localeChain = [userActiveLanguage];
        }

        // Else apply the default setting specified in kerneos-config.
        else {
            ResourceManager.getInstance().localeChain = [KerneosModelLocator.getInstance().application.defaultLocale];
        }
    }

    public static function setLocale(locale:String):void {
        // Change the active language to the selected one..
        ResourceManager.getInstance().localeChain = [locale];
    }

    public static function getLocale():String {
        // Return current locale
        return ResourceManager.getInstance().localeChain[0];
    }

    public static function saveLocale():void {
        // Store the user's choice.
        SharedObjectManager.setActiveLanguage(ResourceManager.getInstance().localeChain[0]);
    }
}
}
