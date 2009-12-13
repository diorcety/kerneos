/**
 * JASMINe
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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.jasmine.kerneos.common.view
{

import flash.display.DisplayObject;

import mx.core.Application;
import mx.managers.PopUpManager;


/**
 * An object that holds informations about a server-side thrown exception.
 *
 * @author Julien Nicoulaud
 */
[Bindable]
public class ServerSideException
{

    // =========================================================================
    // Variables
    // =========================================================================

    /**
     * The name of the exception
     */
    public var name : String;

    /**
     * A short statement that describes the exception (optionnal)
     */
    public var description : String = null;

    /**
     * The full stack trace of the exception (optionnal)
     */
    public var stackTrace : String = null;



    // =========================================================================
    // Initialization
    // =========================================================================

    /**
     * Create a new ServerSideException
     */
    public function ServerSideException(name : String, description : String = null, stackTrace : String = null)
    {
        // Assign properties
        this.name = name;

        if (description != null)
        {
            this.description = description;
        }

        if (stackTrace != null)
        {
            this.stackTrace = stackTrace;
        }
    }



    // =========================================================================
    // Utils
    // =========================================================================

    /**
     * Wether the exception has a field "description"
     */
    public function hasDescription() : Boolean
    {
        return (description != null);
    }



    /**
     * Wether the exception has a stack trace
     */
    public function hasStackTrace() : Boolean
    {
        return (stackTrace != null);
    }



    // =========================================================================
    // Utils
    // =========================================================================

    /**
     * Display the exception in a window
     */
    public function show() : void
    {
        // Draw a window and display the message
        var window : ServerSideExceptionWindow = new ServerSideExceptionWindow();
        window.exception = this;
        PopUpManager.addPopUp(window, Application.application as DisplayObject, false);
        PopUpManager.centerPopUp(window);
    }



    /**
     * Show a given exception in a window
     */
    public static function show(exception : ServerSideException) : void
    {
        exception.show();
    }
}
}
