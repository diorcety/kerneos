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
package org.ow2.kerneos.login.model
{
import com.adobe.cairngorm.model.ModelLocator;

import org.ow2.kerneos.login.business.ILogInDelegate;
import org.ow2.kerneos.login.business.ILogOutDelegate;
import org.ow2.kerneos.login.business.LogInDelegate;
import org.ow2.kerneos.login.business.LogOutDelegate;

/**
 * @author Guillaume Renault
 */
[Bindable]
public class LogInModelLocator implements ModelLocator
{

    // private

    private var loggedInObj:Boolean = false;

    /**
    * Unique instance of this locator.
    */
    private static var model:LogInModelLocator = null;

    //[ArrayElementType(JasmineMessageEventVO)]

    // delegate unique instances

    private var logInDelegate:ILogInDelegate = null;

    private var logOutDelegate:ILogOutDelegate = null;

    // functions
    public function LogInModelLocator()
    {
        super();

        if (model != null) {
            throw new Error("Only one LogInModelLocator has to be set");
        }
    }

    public static function getInstance() : LogInModelLocator {
        if (LogInModelLocator.model == null) {
            LogInModelLocator.model = new LogInModelLocator();
        }

        return LogInModelLocator.model;
    }

    public function set loggedIn(log:Boolean):void {
        this.loggedInObj = log;
    }

    public function get loggedIn():Boolean {
        return this.loggedInObj;
    }


    // getters delegates

    public function getLogInDelegate():ILogInDelegate {
        if (this.logInDelegate == null) {
            this.logInDelegate = new LogInDelegate();
        }
        return this.logInDelegate;
    }

    public function getLogOutDelegate():ILogOutDelegate {
        if (this.logOutDelegate == null) {
            this.logOutDelegate = new LogOutDelegate();
        }
        return this.logOutDelegate;
    }
}
}
