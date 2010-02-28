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
 * $Id: ServicesVO.as 5737 2009-12-13 17:04:13Z nicoulaj $
 */

package org.ow2.jasmine.kerneos.core.vo
{
import com.adobe.cairngorm.vo.IValueObject;

import mx.collections.ArrayCollection;


/**
 * Shared libraries to load in the main Application Domain.
 *
 * @author Guillaume Renault
 * @see ServiceVO
 */
[RemoteClass(alias="org.ow2.jasmine.kerneos.config.generated.SharedLibraries")]
[Bindable]
public class SharedLibrariesVO implements IValueObject
{

    /**
     * The shared libraries to load.
     */
    [ArrayElementType('String')]
    private var m_sharedLibrary : ArrayCollection;

    public function set sharedLibrary(p_sharedLibrary : ArrayCollection) : void
    {
        this.m_sharedLibrary = p_sharedLibrary;
    }



    public function get sharedLibrary() : ArrayCollection
    {
        if (this.m_sharedLibrary == null)
        {
            this.m_sharedLibrary = new ArrayCollection();
        }
        return this.m_sharedLibrary;
    }

}
}
