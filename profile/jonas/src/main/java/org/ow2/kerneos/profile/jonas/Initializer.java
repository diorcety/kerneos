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

package org.ow2.kerneos.profile.jonas;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.util.plan.deploy.deployable.api.factory.XmlFileDeployableFactory;

import java.io.IOException;

@Component
@Instantiate
public class Initializer {

    private XmlFileDeployableFactory factory;

    private boolean started = false;

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        if (factory != null) {
            factory.registerFileDeployable(KerneosProfileDeployable.class, KerneosProfileDeployable.NAMESPACE);
        }
        started = true;
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        started = false;
        if (factory != null) {
            factory.unregisterFileDeployable(KerneosProfileDeployable.NAMESPACE);
        }
    }

    /**
     * Bind method to register the deployable class.
     *
     * @param factory the XML deployable factory to deploy to.
     */
    @Bind
    public void bindXmlFileDeployableFactory(final XmlFileDeployableFactory factory) {
        this.factory = factory;
        if (started) {
            factory.registerFileDeployable(KerneosProfileDeployable.class, KerneosProfileDeployable.NAMESPACE);
        }
    }

    /**
     * Unbind method to unregister the deployable class.
     *
     * @param factory the XML deployable factory to undeploy from.
     */
    @Unbind
    public void unbindXmlFileDeployableFactory(final XmlFileDeployableFactory factory) {
        this.factory = null;
        if (started) {
            factory.unregisterFileDeployable(KerneosProfileDeployable.NAMESPACE);
        }
    }

}
