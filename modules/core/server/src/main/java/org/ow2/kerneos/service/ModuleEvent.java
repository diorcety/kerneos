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

package org.ow2.kerneos.service;

import org.ow2.kerneos.config.generated.Module;

import java.io.Serializable;

/**
 * Module event used for communicate the arrival/departure of a module.
 */
public class ModuleEvent implements Serializable {
    /**
     * Module Object
     */
    private Module module;

    /**
     * Module's event type (load or unload).
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
    public ModuleEvent() {

    }

    /**
     * Create a module event.
     * @param module is the module associated with the event.
     * @param eventType is the type of event associated with the module.
     */
    public ModuleEvent(final Module module, final String eventType) {
        this.module = module;
        this.eventType = eventType;
    }

    /**
     * Get the type of event.
     * @return the eventType.
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Get the module associated with the event.
     * @return the module
     */
    public Module getModule() {
        return module;
    }

    /**
     * Set the module associated with the event.
     * @param module is the module associated with the event.
     */
    public void setModule(Module module) {
        this.module = module;
    }

    /**
     * Set the type of event.
     * @param type is the type of event.
     */
    public void setEventType(String type) {
        this.eventType = type;
    }
}
