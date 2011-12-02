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
package org.ow2.kerneos.common.managers {
import com.adobe.cairngorm.control.CairngormEventDispatcher;

import flash.events.Event;

import mx.messaging.events.ChannelFaultEvent;
import mx.resources.ResourceManager;
import mx.rpc.events.FaultEvent;

import org.ow2.kerneos.common.event.ServerSideExceptionEvent;
import org.ow2.kerneos.common.view.ServerSideException;

public class ErrorManager {
    public function ErrorManager() {
    }

    public static function handleError(error:Object, eventType:String = null, dispatcher:CairngormEventDispatcher = null):Boolean {
        var serverSideExceptionEvent:ServerSideExceptionEvent;
        if (eventType == null)
            eventType = ServerSideExceptionEvent.SERVER_SIDE_EXCEPTION;

        if (error is Event) {
            var errorEvent:Event = error as Event;
            if (errorEvent is FaultEvent) {
                var faultEvent:FaultEvent = errorEvent as FaultEvent;
                if (faultEvent.fault.faultCode == "Server.Security.AccessDenied") {
                    serverSideExceptionEvent = new ServerSideExceptionEvent(eventType,
                            new ServerSideException(ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.error.access-denied.title'),
                                    ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.error.access-denied')));
                } else if (faultEvent.fault.faultCode == "Server.Security.SessionExpired") {
                    serverSideExceptionEvent = new ServerSideExceptionEvent(eventType,
                            new ServerSideException(ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.error.session-expired.title'),
                                    ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.error.session-expired')));
                } else if (faultEvent.fault.rootCause != null) {
                    return handleError(faultEvent.fault.rootCause, eventType);
                }
            } else if (errorEvent is ChannelFaultEvent) {
                var channelFaultEvent:ChannelFaultEvent = errorEvent as ChannelFaultEvent;
                serverSideExceptionEvent = new ServerSideExceptionEvent(eventType,
                        new ServerSideException(ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.error.connexion-fault.title'),
                                ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.error.connexion-fault')));
            }
        }

        if (serverSideExceptionEvent != null) {
            if (dispatcher == null)
                CairngormEventDispatcher.getInstance().dispatchEvent(serverSideExceptionEvent);
            else
                dispatcher.dispatchEvent(serverSideExceptionEvent);
            return true;
        } else {
            return false;
        }
    }
}
}
