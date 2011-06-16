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
 * $Id$
 */
package org.ow2.kerneos.core.business
{
import com.adobe.cairngorm.business.ServiceLocator;

import org.ow2.kerneos.common.business.AbsDelegateResponder;

/**
* Get the module list
* 
* @author Julien Nicoulaud
*/
public class GetModulesDelegate extends AbsDelegateResponder
                                       implements IGetModulesDelegate {
    
    /**
    * Get the module list
    */
    public function getModules():void
    {
            // find service
            var service : Object = ServiceLocator.getInstance(null).getRemoteObject("kerneosConfigService");
    
            // call service
            var call : Object = service.getModules();
    
            // add the responder as a listener for the answer of the java side
            call.addResponder(this.responder);
    }
}
}
