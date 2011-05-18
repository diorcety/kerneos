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

package org.ow2.kerneos.service.impl;

import org.osgi.framework.Bundle;
import org.ow2.kerneos.config.generated.Application;
import org.ow2.kerneos.config.generated.Module;
import org.ow2.kerneos.service.ApplicationInstance;
import org.ow2.kerneos.service.ModuleInstance;

import java.util.Collection;
import java.util.Map;

/**
 * Interface of the Kerneos' core.
 */
public interface IKerneosCore {

    /**
     * Register a module.
     */
    public ModuleInstance registerModule(String name, Module module, Bundle bundle) throws Exception;

    /**
     * Unregister a module.
     */
    public ModuleInstance unregisterModule(String name) throws Exception;

    /**
     * Get Module list.
     */
    public Collection<ModuleInstance> getModules();

    /**
     * Register a application.
     */
    public ApplicationInstance registerApplication(String name, Application application, Bundle bundle) throws Exception;

    /**
     * Unregister a application.
     */
    public ApplicationInstance unregisterApplication(String name) throws Exception;

    /**
     * Get Application list.
     */
    public Collection<ApplicationInstance> getApplications();
}
