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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.jasmine.kerneos.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A group of modules displayed as a folder in Kerneos.
 *
 * @author Julien Nicoulaud
 */
@XmlRootElement(name = "folder", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
public class Folder extends ModuleWithWindow {

    /**
     * The modules.
     */
    @XmlElementWrapper(name = "modules", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    @XmlAnyElement(lax = true)
    @XmlElementRefs({
        @XmlElementRef(type = SWFModule.class, name = "swf-module", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE),
        @XmlElementRef(type = IFrameModule.class, name = "iframe-module", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE),
        @XmlElementRef(type = Link.class, name = "link", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE),
        @XmlElementRef(type = Folder.class, name = "folder", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    })
    public List<Module> modules = new ArrayList<Module>();


    /**
     * Output a String preview of the object.
     */
    @Override
    public String toString() {
        String res = new String();
        res += "[Folder] " + super.toString();
        res += "\n\t\t[Modules]";
        for (Module module : modules) {
            res += "\n\t\t\t" + module.toString();
        }

        return res;
    }

}
