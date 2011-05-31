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

package org.ow2.kerneos.core;

import org.ow2.kerneos.core.config.generated.Module;

/**
 * An instance of Module.
 */
public class ModuleInstance {
    private String id;
    private Module configuration;

    /**
     * Empty constructor.
     */
    public ModuleInstance() {

    }

    /**
     * Copy constructor.
     *
     * @param moduleInstance the instance of module to copy.
     */
    public ModuleInstance(ModuleInstance moduleInstance) {
        this.id = moduleInstance.getId();
        this.configuration = moduleInstance.getConfiguration();
    }

    /**
     * Construct a module instance using an id and a module.
     *
     * @param id     the id of the instance.
     * @param configuration the module of the instance.
     */
    public ModuleInstance(String id, Module configuration) {
        this.id = id;
        this.configuration = configuration;
    }

    /**
     * Get the id of the instance.
     *
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id of the instance.
     *
     * @param id the id of the instance.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the module of the instance.
     *
     * @return the module.
     */
    public Module getConfiguration() {
        return configuration;
    }

    /**
     * Set the module of the instance.
     *
     * @param configuration the module of the instance.
     */
    public void setConfiguration(Module configuration) {
        this.configuration = configuration;
    }
}
