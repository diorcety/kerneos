/**
 * JASMINe
 * Copyright (C) 2008 Bull S.A.S.
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
package org.ow2.jasmine.kerneos.login.command
{
import com.adobe.cairngorm.commands.ICommand;
import com.adobe.cairngorm.control.CairngormEvent;

import mx.controls.Alert;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

import org.ow2.jasmine.kerneos.login.business.ILogOutDelegate;
import org.ow2.jasmine.kerneos.login.event.LogInEvent;
import org.ow2.jasmine.kerneos.login.model.LogInModelLocator;

/**
 * @author Guillaume Renault
 */
public class LogOutCommand implements ICommand, IResponder
{
    /**
    * Send the event to the java side, using the business layer of the pattern
    */
    public function execute( event:CairngormEvent ):void {
        var delegate:ILogOutDelegate = LogInModelLocator.getInstance().getLogOutDelegate();
        delegate.responder = this;
        delegate.logOut();
    }

    /**
    * Get the result of the java side. this method is called on each event from
    * Java.
    */
    public function result( event : Object ):void {

        var model:LogInModelLocator = LogInModelLocator.getInstance();

        var loggedOut:Boolean = (event as ResultEvent).result as Boolean;

        if (loggedOut == true) {
            model.loggedIn = false;
        }
    }

    /**
    * Handle fault messages
    */
    public function fault( event : Object ) : void {
        var faultEvent : FaultEvent = FaultEvent( event );
        Alert.show( "Log out : Unhandled error","Error" );
    }


}
}
