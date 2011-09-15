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

package org.ow2.kerneos.core.impl;

import org.osgi.framework.Bundle;
import org.ow2.kerneos.core.ApplicationBundle;
import org.ow2.kerneos.core.ModuleBundle;
import org.ow2.kerneos.core.config.generated.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface IKerneosCore {

    public void addApplicationBundle(final ApplicationBundle applicationBundle) throws Exception;

    public ApplicationBundle removeApplicationBundle(final String applicationId) throws Exception;

    public void addModuleBundle(final ModuleBundle moduleBundle) throws Exception;

    public ModuleBundle removeModuleBundle(final String moduleId) throws Exception;

    public ModuleBundle getModuleBundle(Bundle bundle);

    public ModuleBundle getModuleBundle(String moduleId);

    public Service getService(String serviceId);

    public Map<String, ModuleBundle> getModuleBundles();

    public ApplicationBundle getApplicationBundle(Bundle bundle);

    public ApplicationBundle getApplicationBundle(String applicationId);

    public Map<String, ApplicationBundle> getApplicationBundles();

    void updateContext(HttpServletRequest request, HttpServletResponse response);

    void updateContext(String destination, String method);
}
