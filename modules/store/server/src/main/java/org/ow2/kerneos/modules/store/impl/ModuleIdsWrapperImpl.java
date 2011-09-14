/**
 * Kerneos
 * Copyright (C) 2011 Bull S.A.S.
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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */

package org.ow2.kerneos.modules.store.impl;

import org.ow2.kerneosstore.api.ModuleIdsWrapper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

@XmlRootElement(name = "moduleids")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModuleIdsWrapperImpl implements ModuleIdsWrapper{

    @XmlElementWrapper(name = "idsCollection")
    @XmlElement
    private Collection<String> ids;

    public ModuleIdsWrapperImpl() {
    }

    public ModuleIdsWrapperImpl(ModuleIdsWrapper moduleIdsWrapper) {
        this.setIds(moduleIdsWrapper.getIds());
    }

    @Override
    public void addId(String id) {
        ids.add(id);
    }


    @Override
    public Collection<String> getIds() {
        return ids;
    }

    @Override
    public void setIds(Collection<String> ids) {
        this.ids = ids;
    }
}
