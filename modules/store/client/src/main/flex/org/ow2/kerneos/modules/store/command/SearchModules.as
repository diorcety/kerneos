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
 */
package org.ow2.kerneos.modules.store.command
{
import com.adobe.cairngorm.commands.ICommand;
import com.adobe.cairngorm.control.CairngormEvent;
import com.adobe.cairngorm.control.CairngormEventDispatcher;

import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

import org.ow2.kerneos.modules.store.event.SearchModulesEvent;
import org.ow2.kerneos.modules.store.vo.ModuleVO;

// Server Exceptions imports
import org.ow2.kerneos.common.event.ServerSideExceptionEvent;
import org.ow2.kerneos.common.view.ServerSideException;

import org.ow2.kerneos.modules.store.business.*;
import org.ow2.kerneos.modules.store.model.ModuleModelLocator;

import mx.collections.ArrayCollection;

/**
 * The command class from the cairngorm model.
 */
[Event(name="serverSideException", type="org.ow2.kerneos.common.event.ServerSideExceptionEvent")]
public class SearchModules implements ICommand, IResponder
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


        // - Get the delegate
        // - Register the responder
        // - Make the call
        var delegate:IModuleDelegate = ModuleModelLocator.getInstance().getMyDelegate();
        delegate.responder = this;
        var filter : String = (event as SearchModulesEvent).filter;
        var field : String = (event as SearchModulesEvent).field;
        var order : String = (event as SearchModulesEvent).order;

        var itemByPage : Object;
        if ((event as SearchModulesEvent).itemByPage < 0) {
          itemByPage = null;
        } else {
          itemByPage = (event as SearchModulesEvent).itemByPage;
        }

        var page : Object;
        if ((event as SearchModulesEvent).page < 0) {
          page = null;
        } else {
          page = (event as SearchModulesEvent).page;
        }

        delegate.searchModules(filter, field, order, itemByPage, page);
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


        // Handle the result of the call. Usely, the model is updated.
        var moduleModel:ModuleModelLocator = ModuleModelLocator.getInstance();

        [ArrayElementType('org.ow2.kerneos.modules.store.vo.ModuleVO')]
        var result:ArrayCollection = ArrayCollection((data as ResultEvent).result);

        moduleModel.listeModules = result;
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


        // The following code generates a formated panel that contains
        // the fault. However, librairies from jasmine-eos should be included
        // to get the common and util classes

        // Code :

        // Retrieve the fault event
        var faultEvent : FaultEvent = FaultEvent(info);

        // Tell the view and let it handle this
        var serverSideExceptionEvent : ServerSideExceptionEvent =
                new ServerSideExceptionEvent(
                        "serverSideException",
                        new ServerSideException("Error while Executing the action",
                                "The operation could not be performed."
                                        + "\n" + faultEvent.fault.faultCode
                                        + "\n" + faultEvent.fault.faultString,
                                faultEvent.fault.getStackTrace()));

        // Dispatch the event using the cairngorm event dispatcher
        CairngormEventDispatcher.getInstance().dispatchEvent(serverSideExceptionEvent);



    }

}
}
