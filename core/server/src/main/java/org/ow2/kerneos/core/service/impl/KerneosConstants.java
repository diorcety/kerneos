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

package org.ow2.kerneos.core.service.impl;

/**
 * Contains the different constants used by Kerneos.
 */
public final class KerneosConstants {

    /**
     *
     */
    private KerneosConstants() {

    }

    /*
     * Suffix used for the factory of an service
     */
    public static final String FACTORY_SUFFIX = "_FaCtOry";

    /*
     * The name of the Kerneos HttpContext
     */
    public static final String KERNEOS_CONTEXT_NAME = "KerneosContext";

    /*
     * Manifest entry for a Kerneos entities
     */
    public static final String KERNEOS_MODULE_MANIFEST = "Kerneos-Module";
    public static final String KERNEOS_APPLICATION_MANIFEST = "Kerneos-Application";
    public static final String KERNEOS_PATH = "/KERNEOS";
    public static final String KERNEOS_SWF_NAME = "kerneos.swf";

    /*
     * The path to the Kerneos files
     */
    public static final String KERNEOS_APPLICATION_FILE = "META-INF/kerneos-application.xml";
    public static final String KERNEOS_MODULE_FILE = "META-INF/kerneos-module.xml";

    /*
     * Modules related constants
     */
    public static final String KERNEOS_MODULE_PREFIX = "modules";

    /*
     *  Topics
     */
    public static final String KERNEOS_TOPIC = "kerneos";
    public static final String KERNEOS_CONFIG_TOPIC = KERNEOS_TOPIC + "/config";

    /*
     *  Granite Configuration
     */
    public static final String GRANITE_SERVICE = "granite-service";
    public static final String GRANITE_CHANNEL = "kerneos-graniteamf-";
    public static final String GRANITE_CHANNEL_URI = "/granite/amf";

    /*
     *  Gravity Configuration
     */
    public static final String GRAVITY_SERVICE = "messaging-service";
    public static final String GRAVITY_CHANNEL = "kerneos-gravityamf-";
    public static final String GRAVITY_CHANNEL_URI = "/gravity/amf";


    /*
     * Kerneos Services
     */
    public static final String KERNEOS_SERVICE_CONFIGURATION = "kerneos-configuration";
    public static final String KERNEOS_SERVICE_ASYNC_CONFIGURATION = "kerneos-async-configuration";
    public static final String KERNEOS_SERVICE_SECURITY = "kerneos-security";

    public static final String[] KERNEOS_SERVICES = new String[]{
            KERNEOS_SERVICE_CONFIGURATION,
            KERNEOS_SERVICE_ASYNC_CONFIGURATION,
            KERNEOS_SERVICE_SECURITY};
}
