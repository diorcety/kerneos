/**
 * Kerneos
 * Copyright (C) 2008 Bull S.A.S.
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

package org.ow2.kerneos.login.command {
import com.adobe.cairngorm.commands.ICommand;
import com.adobe.cairngorm.control.CairngormEvent;
import com.adobe.cairngorm.control.CairngormEventDispatcher;

import mx.controls.Alert;
import mx.resources.ResourceManager;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

import org.ow2.kerneos.common.event.ServerSideExceptionEvent;
import org.ow2.kerneos.common.view.ServerSideException;

import org.ow2.kerneos.login.business.ILogInDelegate;
import org.ow2.kerneos.login.event.LogInEvent;
import org.ow2.kerneos.login.manager.LanguagesManager;
import org.ow2.kerneos.login.model.LoginModelLocator;
import org.ow2.kerneos.login.model.LoginState;


/**
 * @author Guillaume Renault
 */
public class LogInCommand implements ICommand, IResponder {
    /**
     * Send the event to the java side, using the business layer of the pattern
     */
    public function execute(event:CairngormEvent):void {
        var delegate:ILogInDelegate = LoginModelLocator.getInstance().getLogInDelegate();
        delegate.responder = this;
        delegate.logIn((event as LogInEvent).user, (event as LogInEvent).password);
    }

    /**
     * Get the result of the java side. this method is called on each event from
     * Java.
     */
    public function result(event:Object):void {
        var model:LoginModelLocator = LoginModelLocator.getInstance();
        var logged:Boolean = (event as ResultEvent).result as Boolean;

        if (!logged) {
            Alert.show(ResourceManager.getInstance().getString(LanguagesManager.LOCALE_RESOURCE_BUNDLE, 'kerneos.login.failed'), "Error");
        }
        else {
            model.state = LoginState.AUTH;
        }
    }

    /**
     * Handle fault messages
     */
    public function fault(event:Object):void {

        // Retrieve the fault event
        var faultEvent:FaultEvent = FaultEvent(event);

        // Retrieve the model
        var model:LoginModelLocator = LoginModelLocator.getInstance();

        // Tell the view and let it handle this
        var serverSideExceptionEvent:ServerSideExceptionEvent =
                new ServerSideExceptionEvent(
                        ServerSideExceptionEvent.SERVER_SIDE_EXCEPTION + model.componentID,
                        new ServerSideException("Error while loading the configuration",
                                "The application configuration file could not be read successfully."
                                        + "\n" + faultEvent.fault.faultString,
                                faultEvent.fault.getStackTrace()));
        CairngormEventDispatcher.getInstance(this).dispatchEvent(serverSideExceptionEvent);
    }
}
}
