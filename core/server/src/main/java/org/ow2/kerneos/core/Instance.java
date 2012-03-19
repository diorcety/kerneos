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

import org.apache.felix.ipojo.ComponentInstance;

import org.osgi.service.cm.Configuration;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;


/**
 * An instance of Application.
 */
public class Instance {
    private ComponentInstance componentInstance;
    private Collection<Configuration> configurations = new LinkedList<Configuration>();

    public void setComponentInstance(ComponentInstance componentInstance) {
        this.componentInstance = componentInstance;
    }

    public ComponentInstance getComponentInstance() {
        return componentInstance;
    }

    public void addConfiguration(Configuration configuration) {
        configurations.add(configuration);
    }

    public void start() {
        componentInstance.start();
    }

    /**
     * Remove all the configuration associated with the ApplicationBundle
     *
     * @throws IOException
     */
    public synchronized void dispose() throws IOException {
        componentInstance.dispose();
        for (Configuration configuration : configurations) {
            configuration.delete();
        }
        configurations.clear();
    }

    /**
     * Stop instance
     *
     * @throws IOException
     */
    public synchronized void stop() {
        componentInstance.stop();
    }
}
