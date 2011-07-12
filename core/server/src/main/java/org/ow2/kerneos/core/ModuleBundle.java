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

import org.osgi.framework.Bundle;

import org.osgi.service.cm.Configuration;
import org.ow2.kerneos.core.config.generated.Module;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * An instance of Module.
 */
public class ModuleBundle {
    private String id;
    private Module module;
    private Bundle bundle;
    private List<Configuration> configurations = new LinkedList<Configuration>();

    /**
     * Empty constructor.
     */
    public ModuleBundle() {

    }

    /**
     * Construct a module instance using an id and a module.
     *
     * @param id     the id of the instance.
     * @param module the module of the instance.
     */
    public ModuleBundle(String id, Module module, Bundle bundle) {
        this.id = id;
        this.module = module;
        this.bundle = bundle;
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
    public Module getModule() {
        return module;
    }

    /**
     * Set the module of the instance.
     *
     * @param module the module of the instance.
     */
    public void setModule(Module module) {
        this.module = module;
    }

    /**
     * Get the bundle associated with this instance.
     *
     * @return the bundle.
     */
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * Add a configuration associated with the ApplicationBundle
     *
     * @param configuration the configuration
     */
    public synchronized void addConfiguration(Configuration configuration) {
        configurations.add(configuration);
    }

    /**
     * Remove all the configuration associated with the ApplicationBundle
     *
     * @throws java.io.IOException
     */
    public synchronized void dispose() throws IOException {
        for (Configuration configuration : configurations) {
            configuration.delete();
        }
        configurations.clear();
    }
}
