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

package org.ow2.kerneos.common;

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
     * Manifest entry for a Kerneos entities
     */
    public static final String KERNEOS_MODULE_MANIFEST = "Kerneos-Module";
    public static final String KERNEOS_APPLICATION_MANIFEST = "Kerneos-Application";
    public static final String KERNEOS_PATH = "/KERNEOS";
    public static final String KERNEOS_MODULE_PREFIX = "modules";
    public static final String KERNEOS_MODULE_URL = "/" + KERNEOS_MODULE_PREFIX + "/";

    /*
     * The path to the Kerneos files
     */
    public static final String KERNEOS_APPLICATION_FILE = "META-INF/kerneos-application.xml";
    public static final String KERNEOS_MODULE_FILE = "META-INF/kerneos-module.xml";
    public static final String[] KERNEOS_INDEX_FILES = {"/index.html", "/index.htm"};


    /**
     * Kerneos Event Admin
     */
    public static final String KERNEOS_TOPIC_DATA = "data";
    public static final String KERNEOS_TOPIC = "org/kerneos";
    public static final String KERNEOS_MODULES_TOPIC = KERNEOS_TOPIC + "/modules";
    public static final String KERNEOS_APPLICATIONS_TOPIC = KERNEOS_TOPIC + "/applications";
    public static final String KERNEOS_MODULE_TOPIC = KERNEOS_TOPIC + "/module";
    public static final String KERNEOS_APPLICATION_TOPIC = KERNEOS_TOPIC + "/application";
    public static final String KERNEOS_PROFILE_SUFFIX = "/profile";
    public static final String KERNEOS_LOGIN_SUFFIX = "/login";
    public static final String KERNEOS_ROLES_SUFFIX = "/roles";
}
