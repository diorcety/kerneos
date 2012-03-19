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
package org.ow2.kerneos.examples.modules.module2.business {

import com.adobe.cairngorm.business.ServiceLocator;

import org.ow2.kerneos.common.business.AbsDelegateResponder;
import org.ow2.kerneos.examples.modules.module2.vo.PostVO;

public class PostDelegate extends AbsDelegateResponder implements IPostDelegate {
    ////////////////////////////////////////////////////////////////////
    //                                                                //
    //             Function that does the requested operation         //
    //                                                                //
    ////////////////////////////////////////////////////////////////////

    public function post(post: PostVO):void {
        // find the service
        var service:Object = Module2.getInstance().getServices().getRemoteObject("module2-serviceSync");

        // Make the service call. The method called on service is the method name
        // of the java class bound with the remote object, with its parameters.
        var call:Object = service.post(post);

        // add responder to handle the callback
        call.addResponder(this.responder);
    }

}
}
