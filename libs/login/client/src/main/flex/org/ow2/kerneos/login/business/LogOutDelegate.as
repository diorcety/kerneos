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
package org.ow2.kerneos.login.business {
import com.adobe.cairngorm.business.ServiceLocator;

import mx.controls.Alert;

import org.ow2.kerneos.common.business.AbsDelegateResponder;

/**
 * @author guillaume Renault
 */
public class LogOutDelegate extends AbsDelegateResponder implements ILogOutDelegate {

    /**
     * Call a procedure on the service registered. A user log out is performed.
     */
    public function logOut():void {

        // find service
        var service:Object = ServiceLocator.getInstance().getRemoteObject("kerneosSecurityService");

        // call service
        var call:Object = service.logOut();

        // add the responder as a listener for the answer of the java side
        call.addResponder(this.responder);
    }


}
}
