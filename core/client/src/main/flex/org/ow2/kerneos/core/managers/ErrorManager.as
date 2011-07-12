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
import flash.events.Event;

import mx.controls.Alert;

import mx.messaging.events.ChannelFaultEvent;
import mx.resources.ResourceManager;

import mx.rpc.events.FaultEvent;

public class ErrorManager {
    public function ErrorManager() {
    }

    public static function handleError(errorEvent:Event, manager:ErrorManager = null):Boolean {
        if (errorEvent.type == FaultEvent.FAULT) {
            var faultEvent:FaultEvent = errorEvent as FaultEvent;
            if (faultEvent.fault.faultCode == "Server.Security.InvalidCredentials") {
                Alert.show(ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.error.invalid-credentials'),
                        ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.error.invalid-credentials.title')
                );
                return true;
            }
        } else if (errorEvent.type == ChannelFaultEvent.FAULT) {
            var channelFaultEvent:ChannelFaultEvent = errorEvent as ChannelFaultEvent;
            Alert.show(ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.error.connexion-fault'),
                    ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.error.connexion-fault.title')
            );
            return true;
        }

        return false;
    }
}
}
