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

package org.ow2.kerneos.core.manager;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.kerneos.profile.KerneosProfile;
import org.ow2.kerneos.profile.config.generated.Profile;
import org.ow2.kerneos.profile.config.generated.ProfilePolicy;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.io.IOException;

@Component
@Provides
public class DefaultKerneosProfile implements KerneosProfile {
    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(DefaultKerneosLogin.class);

    /**
     * Mandatory service property used by Kerneos core.
     */
    @Property(name = KerneosProfile.ID, mandatory = true)
    @ServiceProperty(name = KerneosProfile.ID)
    private String ID;

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        LOGGER.debug("Start DefaultKerneosProfile(" + ID + ")");
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        LOGGER.debug("Stop DefaultKerneosProfile(" + ID + ")");
    }

    public Profile getProfile() {
        Profile profile = new Profile();
        profile.setDefaultPolicy(ProfilePolicy.ALLOW);
        return profile;
    }
}
