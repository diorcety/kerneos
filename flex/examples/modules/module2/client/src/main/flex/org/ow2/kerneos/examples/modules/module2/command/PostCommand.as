/**
 * Kerneos
 * Copyright (C) 2009-2011 Bull S.A.S.
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
 */
package org.ow2.kerneos.examples.modules.module2.command {
import com.adobe.cairngorm.commands.ICommand;
import com.adobe.cairngorm.control.CairngormEvent;

import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;

import org.ow2.kerneos.common.event.ServerSideExceptionEvent;
import org.ow2.kerneos.common.managers.ErrorManager;
import org.ow2.kerneos.common.view.ServerSideException;

import org.ow2.kerneos.examples.modules.module2.business.*;
import org.ow2.kerneos.examples.modules.module2.event.PostEvent;
import org.ow2.kerneos.examples.modules.module2.vo.PostVO;

/**
 * The command class from the cairngorm model.
 */
public class PostCommand implements ICommand, IResponder {
    /**
     * Retrieve the delegate and use it to make the call.
     */
    public function execute(event:CairngormEvent):void {
        ////////////////////////////////////////////////
        //                                            //
        //             Handle the execution           //
        //                                            //
        ////////////////////////////////////////////////

        var delegate:IPostDelegate = Module2.getInstance().getModel().getMyDelegate();
        delegate.responder = this;

        if (event is PostEvent) {
            var postEvent:PostEvent = event as PostEvent;
            var post:PostVO = new PostVO();
            post.pseudo = postEvent.pseudo;
            post.message = postEvent.message;
            delegate.post(post);
        }
    }

    /**
     * Handle the result of the server call.
     */
    public function result(data:Object):void {
    }

    /**
     * Raise an alert when something is wrong.
     */
    public function fault(event:Object):void {

        ////////////////////////////////////////
        //                                    //
        //             Handle fault           //
        //                                    //
        ////////////////////////////////////////
        if (!ErrorManager.handleError(event, ServerSideExceptionEvent.SERVER_SIDE_EXCEPTION, Module2.getInstance().getDispatcher())) {
            // Retrieve the fault event
            var faultEvent:FaultEvent = FaultEvent(event);

            // Tell the view and let it handle this
            var serverSideExceptionEvent:ServerSideExceptionEvent =
                    new ServerSideExceptionEvent(
                            ServerSideExceptionEvent.SERVER_SIDE_EXCEPTION,
                            new ServerSideException("Error while Executing the action",
                                    "The operation could not be performed."
                                            + "\n" + faultEvent.fault.faultCode
                                            + "\n" + faultEvent.fault.faultString,
                                    faultEvent.fault.getStackTrace()));

            // Dispatch the event using the cairngorm event dispatcher
            Module2.getInstance().getDispatcher().dispatchEvent(serverSideExceptionEvent);
        }
    }
}
}
