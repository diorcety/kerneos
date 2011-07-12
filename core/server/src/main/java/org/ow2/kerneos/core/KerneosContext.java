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

import org.ow2.kerneos.core.config.generated.Service;
import org.ow2.kerneos.core.manager.KerneosLogin;
import org.ow2.kerneos.core.manager.KerneosProfile;
import org.ow2.kerneos.core.manager.KerneosRoles;
import org.ow2.kerneos.login.Session;

/**
 * The Object used the context for kerneos.
 */
public class KerneosContext {
    private static ThreadLocal<KerneosContext> kerneosContextThreadLocal = new ThreadLocal<KerneosContext>() {
        @Override
        protected KerneosContext initialValue() {
            return (null);
        }
    };

    /**
     * Get the current Kerneos context.
     *
     * @return the current Kerneos context.
     */
    public static KerneosContext getCurrentContext() {
        KerneosContext context = kerneosContextThreadLocal.get();
        if (context == null) {
            context = new KerneosContext();
            kerneosContextThreadLocal.set(context);
        }
        return kerneosContextThreadLocal.get();
    }

    // Session
    private Session session;

    // Request
    private ApplicationBundle applicationBundle;
    private ModuleBundle moduleBundle;
    private Service service;
    private String method;
    private String path;

    // Managers
    private KerneosLogin kerneosLogin;
    private KerneosProfile kerneosProfile;
    private KerneosRoles kerneosRoles;

    private KerneosContext() {
    }

    public ApplicationBundle getApplicationBundle() {
        return applicationBundle;
    }

    public void setApplicationBundle(ApplicationBundle applicationBundle) {
        this.applicationBundle = applicationBundle;
    }

    public ModuleBundle getModuleBundle() {
        return moduleBundle;
    }

    public void setModuleBundle(ModuleBundle moduleBundle) {
        this.moduleBundle = moduleBundle;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public KerneosLogin getLoginManager() {
        return kerneosLogin;
    }

    public void setLoginManager(KerneosLogin loginManager) {
        this.kerneosLogin = loginManager;
    }

    public KerneosProfile getProfileManager() {
        return kerneosProfile;
    }

    public void setProfileManager(KerneosProfile profileManager) {
        this.kerneosProfile = profileManager;
    }

    public KerneosRoles getRolesManager() {
        return kerneosRoles;
    }

    public void setRolesManager(KerneosRoles rolesManager) {
        this.kerneosRoles = rolesManager;
    }
}
