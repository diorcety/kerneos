/**
 * JASMINe
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

package org.ow2.jasmine.kerneos.service.impl;

public class KerneosConstants {


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
    public final static String GRANITE_SERVICE = "granite-service";
    public final static String GRANITE_CHANNEL = "my-graniteamf";
    public final static String GRANITE_CHANNEL_URI = "/graniteamf/amf";

    /*
        Gravity Configuration
     */
    public final static String GRAVITY_DESTINATION = "kerneos-gravity";
    public final static String GRAVITY_SERVICE = "messaging-service";
    public final static String GRAVITY_CHANNEL = "my-gravityamf-kerneos";
    public final static String GRAVITY_CHANNEL_URI = "/gravity/amf";
    public final static String GRAVITY_ADAPTER = "kerneos-gravity-adapter";
}
