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

import org.ow2.kerneos.core.config.generated.Application;


/**
 * An instance of Application.
 */
public class ApplicationBundle implements IApplicationBundle {
    protected String id;
    protected Application application;
    protected transient Bundle bundle;

    /**
     * Empty constructor.
     */
    public ApplicationBundle() {
    }

    /**
     * Construct a module instance using an id and a application.
     *
     * @param id            the id of the instance.
     * @param application the application of the instance.
     */
    public ApplicationBundle(String id, Application application, Bundle bundle) {
        this.id = id;
        this.application = application;
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
     * Get the application of the instance.
     *
     * @return the application.
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Set the application of the instance.
     *
     * @param application the application of the instance.
     */
    public void setApplication(Application application) {
        this.application = application;
    }

    /**
     * Get the bundle associated with this instance.
     *
     * @return the bundle.
     */
    public Bundle getBundle() {
        return bundle;
    }
}
