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
 * @author Guillaume Renault
 */
public class Module {

    private String url = null;

    private String swfFile = null;

    private boolean loaded = false;

    private String name = null;

    private String description = null;

    private List<Service> services = null;

    public static String defaultSmallIcon = "resources/icons/module16.png";
    public String smallIcon = defaultSmallIcon;

    public static String defaultBigIcon = "resources/icons/module64.png";
    public String bigIcon = defaultBigIcon;

    public Module() {
        services = new ArrayList<Service>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getSwfFile() {
        return swfFile;
    }

    public void setSwfFile(final String swfFile) {
        this.swfFile = swfFile;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(final boolean loaded) {
        this.loaded = loaded;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(final List<Service> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        String s = "\nModule '" + name + "' contains " + services.size() + " services :\n";

        for (Service service : services) {
            s = s + "\t id : \"" + service.getId() + "\" destination : " + service.getDestination();
        }

        return s;
    }

}
