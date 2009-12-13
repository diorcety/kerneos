/**
 * JASMINe
 * Copyright (C) 2009 Bull S.A.S.
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
 * $Id$
 */

package org.ow2.jasmine.kerneos.core.vo
{
import com.adobe.cairngorm.vo.IValueObject;

import mx.collections.ArrayCollection;


/**
 * Services.
 *
 * @author Guillaume Renault
 * @see ServiceVO
 */
[RemoteClass(alias="org.ow2.jasmine.kerneos.config.generated.Services")]
[Bindable]
public class ServicesVO implements IValueObject
{
    
    /**
     * The services.
     */
    [ArrayElementType('org.ow2.jasmine.kerneos.core.vo.ServiceVO')]
    private var m_service : ArrayCollection;
    
    
    
    public function set service(p_service : ArrayCollection) : void
    {
        this.m_service = p_service;
    }
    
    
    
    public function get service() : ArrayCollection
    {
        if (this.m_service == null)
        {
            this.m_service = new ArrayCollection();
        }
        return this.m_service;
    }

}
}