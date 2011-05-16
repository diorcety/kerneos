/**
 * Kerneos
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
 * $Id$
 */

package org.ow2.jasmine.kerneos.core.vo
{
import com.adobe.cairngorm.vo.IValueObject;

import mx.collections.ArrayCollection;
import flash.utils.getQualifiedClassName;


/**
 * The modules list.
 *
 * @author Guillaume Renault
 * @author Julien Nicoulaud
 * @see ModuleVO
 */
[RemoteClass(alias="org.ow2.jasmine.kerneos.config.generated.Modules")]
[Bindable]
public class ModulesVO implements IValueObject
{

    /**
     * The modules list holder variable.
     */
    [ArrayElementType('org.ow2.jasmine.kerneos.core.vo.ModuleVo')]
    private var _modulesList : ArrayCollection = new ArrayCollection();



    /**
     * Set the list of modules.
     */
    public function set modulesList(value : ArrayCollection) : void
    {
        // (Re)initialize the collection
        _modulesList = new ArrayCollection();

        // We need to cast each member of the received list
        for each (var module : Object in value)
        {
            // Each member is wrapped in a JAXBElement object and accessible
            // with JAXBElement.getValue()
            this._modulesList.addItem(module as ModuleVO);
        }
    }



    /**
     * The modules list.
     */
    public function get modulesList() : ArrayCollection
    {
        return this._modulesList;
    }

}
}
