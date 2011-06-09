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
package org.ow2.kerneos.login.model {
import com.adobe.cairngorm.model.ModelLocator;

import mx.utils.UIDUtil;

import org.ow2.kerneos.login.business.AuthDelegate;

import org.ow2.kerneos.login.business.IAuthDelegate;

import org.ow2.kerneos.login.business.ILogInDelegate;
import org.ow2.kerneos.login.business.ILogOutDelegate;
import org.ow2.kerneos.login.business.LogInDelegate;
import org.ow2.kerneos.login.business.LogOutDelegate;
import org.ow2.kerneos.login.vo.KerneosSessionVO;

/**
 * @author Guillaume Renault
 */
public class LoginModelLocator implements ModelLocator {

    /**
     * The unique ID of this component.
     *
     * @internal
     *   Used to prevent a Cairngorm issue: when a command event is dispatched,
     * every controller that registered this event type receives it, even if
     * located in another module. To prevent this from happening and triggering
     * multiple severe unexpected concurrence bugs, each event dispatched is
     * postfixed with this unique ID.
     */
    public var componentID : String = UIDUtil.createUID();

    /**
     * The state of the login.
     */
    [Bindable]
    public var state:String = null;

    [Bindable]
    public var session:KerneosSessionVO = null;

    /**
     * Unique instance of this locator.
     */
    private static var model:LoginModelLocator = null;

    //[ArrayElementType(JasmineMessageEventVO)]

    // delegate unique instances

    private var logInDelegate:ILogInDelegate = null;

    private var logOutDelegate:ILogOutDelegate = null;

    private var authDelegate:IAuthDelegate = null;

    // functions
    public function LoginModelLocator() {
        super();

        if (model != null) {
            throw new Error("Only one LoginModelLocator has to be set");
        }
    }

    public static function getInstance():LoginModelLocator {
        if (LoginModelLocator.model == null) {
            LoginModelLocator.model = new LoginModelLocator();
        }

        return LoginModelLocator.model;
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

    public function getAuthDelegate():IAuthDelegate {
        if (this.authDelegate == null) {
            this.authDelegate = new AuthDelegate();
        }
        return this.authDelegate;
    }
}
}
