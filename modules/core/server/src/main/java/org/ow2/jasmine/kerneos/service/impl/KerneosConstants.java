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

package org.ow2.jasmine.kerneos.service.impl;

import java.util.HashMap;

public final class KerneosConstants {

    /**
     *
     */
    private KerneosConstants() {

    }

    /**
     * Suffix used for the factory of an service
     */
    public static final String FACTORY_SUFFIX = "_FaCtOry";

    /**
     * Manifest entry for a Kerneos Module
     */
    public static final String KERNEOS_MODULE_MANIFEST = "Kerneos-Module";
    public static final String KERNEOS_PATH = "/KERNEOS";

    /**
     * The path to the Kerneos files
     */
    public static final String KERNEOS_CONFIG_FILE = "META-INF/kerneos-config.xml";
    public static final String KERNEOS_MODULE_FILE = "META-INF/kerneos-module.xml";

    /*
        Topics
     */
    public static final String KERNEOS_TOPIC = "kerneos";
    public static final String KERNEOS_CONFIG_TOPIC = KERNEOS_TOPIC + "/config";

    /*
        Granite Configuration
     */
    public static final String GRANITE_SERVICE = "granite-service";
    public static final String GRANITE_CHANNEL = "my-graniteamf-kerneos";
    public static final String GRANITE_CHANNEL_URI = "/granite/amf";

    /*
        Gravity Configuration
     */
    public static final String GRAVITY_SERVICE = "messaging-service";
    public static final String GRAVITY_CHANNEL = "my-gravityamf-kerneos";
    public static final String GRAVITY_CHANNEL_URI = "/gravity/amf";

}
