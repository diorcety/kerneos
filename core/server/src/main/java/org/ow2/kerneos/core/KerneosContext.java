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
import org.ow2.kerneos.login.KerneosSession;

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
     * Set the current Kerneos context.
     *
     * @param kerneosContext The context.
     */
    public static void setCurrentContext(KerneosContext kerneosContext) {
        kerneosContextThreadLocal.set(kerneosContext);
    }

    /**
     * Get the current Kerneos context.
     *
     * @return the current Kerneos context.
     */
    public static KerneosContext getCurrentContext() {
        return kerneosContextThreadLocal.get();
    }

    private KerneosSession session;
    private IApplicationBundle applicationBundle;
    private IModuleBundle moduleBundle;
    private Service service;
    private String method;

    public KerneosContext(KerneosSession session, IApplicationBundle applicationBundle,
                          IModuleBundle moduleBundle, Service service, String method) {
        this.session = session;
        this.applicationBundle = applicationBundle;
        this.moduleBundle = moduleBundle;
        this.service = service;
        this.method = method;
    }

    public IApplicationBundle getApplicationBundle() {
        return applicationBundle;
    }

    public IModuleBundle getModuleBundle() {
        return moduleBundle;
    }

    public Service getService() {
        return service;
    }

    public String getMethod() {
        return method;
    }

    public KerneosSession getSession() {
        return session;
    }
}
