/**
 * Kerneos
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

import org.ow2.jasmine.kerneos.config.generated.Module;

public class ModuleEvent {

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


    public ModuleEvent(final Module module, final String eventType) {
        this.module = module;
        this.eventType = eventType;
    }

    /**
     * @return the eventType
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * @return the module
     */
    public Module getModule() {
        return module;
    }
}
