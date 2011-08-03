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
package org.ow2.kerneos.modules.store.command
{
import com.adobe.cairngorm.commands.ICommand;
import com.adobe.cairngorm.control.CairngormEvent;
import com.adobe.cairngorm.control.CairngormEventDispatcher;

import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

import org.ow2.kerneos.modules.store.event.ChangeStoreStateEvent;

import org.ow2.kerneos.modules.store.vo.StoreInfo;

// Server Exceptions imports
import org.ow2.kerneos.common.event.ServerSideExceptionEvent;
import org.ow2.kerneos.common.view.ServerSideException;

import org.ow2.kerneos.modules.store.business.*;
import org.ow2.kerneos.modules.store.event.ModuleEvent;
import org.ow2.kerneos.modules.store.model.ModuleModelLocator;

/**
  * The command class from the cairngorm model.
  */
[Event(name="serverSideException", type="org.ow2.kerneos.common.event.ServerSideExceptionEvent")]
public class ChangeStoreState implements ICommand, IResponder
{
    /**
     * Retrieve the delegate and use it to make the call.
     */
    public function execute(event:CairngormEvent):void
    {
        ////////////////////////////////////////////////
        //                                            //
        //             Handle the execution           //
        //                                            //
        ////////////////////////////////////////////////

        ModuleModelLocator.getInstance().storeState = (event as ChangeStoreStateEvent).state;

    }

    /**
     * Handle the result of the server call.
     */
    public function result(data:Object):void
    {
        ////////////////////////////////////////////////
        //                                            //
        //             Handle the result              //
        //                                            //
        ////////////////////////////////////////////////
    }

    /**
     * Raise an alert when something is wrong.
     */
    public function fault(info:Object):void
    {
        ////////////////////////////////////////
        //                                    //
        //             Handle fault           //
        //                                    //
        ////////////////////////////////////////
    }

}
}
