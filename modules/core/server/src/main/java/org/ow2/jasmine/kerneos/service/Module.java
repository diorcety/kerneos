/**
 * JASMINe
 * Copyright (C) 2008 Bull S.A.S.
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

import java.util.ArrayList;
import java.util.List;

/**
 * Description of a module.
 *
 * @author Guillaume Renault, Julien Nicoulaud
 */
public class Module {

    /**
     * The web page URL (for IFrame modules)
     */
    public String url = null;

    /**
    * The SWF file path (for SWF modules)
    */
    public String swfFile = null;

    /**
    * The module services (for SWF modules)
    */
    public List<Service> services = new ArrayList<Service>();

    /**
    * The displayed name of the module
    */
    public String name = null;

    /**
    * The description of the module
    */
    public String description = null;

    /**
    * The default small icon (16x16) path
    */
    public static String defaultSmallIcon = "resources/icons/module16.png";

    /**
    * The small icon (16x16) path
    */
    public String smallIcon = defaultSmallIcon;

    /**
    * The default big icon (64x64) path
    */
    public static String defaultBigIcon = "resources/icons/module64.png";

    /**
    * The big icon (64x64) path
    */
    public String bigIcon = defaultBigIcon;

    /**
    * Load the module on application startup
    */
    public Boolean loadOnStartup = false;

    /**
    * Load the module maximized
    */
    public Boolean loadMaximized = false;


    // Utils

    @Override
    public String toString() {
        String s = "\nModule '" + name + "' contains " + services.size() + " services :\n";

        for (Service service : services) {
            s = s + "\t id : \"" + service.getId() + "\" destination : " + service.getDestination();
        }

        return s;
    }

}
