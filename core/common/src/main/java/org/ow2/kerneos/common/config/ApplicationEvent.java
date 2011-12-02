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

package org.ow2.kerneos.common.config;

import org.ow2.kerneos.common.config.generated.Application;
import org.ow2.kerneos.common.config.generated.Module;

import java.io.Serializable;

/**
 * Application event used for communicate the arrival/departure of an application.
 */
public class ApplicationEvent implements Serializable {
    /**
     * Application Object
     */
    private Application application;

    /**
     * Application's event type (load or unload).
     */
    private String eventType = LOAD;

    /**
     * Load value.
     */
    public static final String LOAD = "load";

    /**
     * Unload value.
     */
    public static final String UNLOAD = "unload";

    /**
     * Create an empty module event.
     */
    public ApplicationEvent() {

    }

    /**
     * Create a application event.
     *
     * @param application is an instance of application associated with the event.
     * @param eventType   is the type of event associated with the module.
     */
    public ApplicationEvent(final Application application, final String eventType) {
        this.application = application;
        this.eventType = eventType;
    }

    /**
     * Get the type of event.
     *
     * @return the eventType.
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Get the  instance of application associated with the event.
     *
     * @return the instance of application
     */
    public Application getModule() {
        return application;
    }

    /**
     * Set the instance of application associated with the event.
     *
     * @param application is an instance of application associated with the event.
     */
    public void setApplication(Application application) {
        this.application = application;
    }

    /**
     * Set the type of event.
     *
     * @param type is the type of event.
     */
    public void setEventType(String type) {
        this.eventType = type;
    }
}
