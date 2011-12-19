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

package org.ow2.kerneos.flex;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.GraniteConstants;
import org.granite.osgi.service.GraniteDestination;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.common.service.KerneosSecurityService;
import org.ow2.kerneos.login.KerneosSession;
import org.ow2.kerneos.profile.config.generated.Profile;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * The security service used by Kerneos.
 */
@Component
@Instantiate
@Provides
public class SecurityService implements GraniteDestination {
    /**
     * The logger.
     */
    private static final Log LOGGER = LogFactory.getLog(SecurityService.class);

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private GraniteClassRegistry gcr;

    @Requires
    private KerneosSecurityService securityService;

    @Requires
    private ICore flexCore;

    private Configuration graniteDestination, gravityDestination, eaConfig;

    private SecurityService() {

    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        LOGGER.debug("Start FlexSecurityService");

        gcr.registerClasses(FlexConstants.KERNEOS_SERVICE_SECURITY, SecurityObjects.classes());
        gcr.registerClasses(FlexConstants.KERNEOS_SERVICE_ASYNC_SECURITY, SecurityObjects.classes());

        // Register the synchronous service used with SecurityService
        {
            Dictionary properties = new Hashtable();
            properties.put("id", FlexConstants.KERNEOS_SERVICE_SECURITY);
            properties.put("service", FlexConstants.GRANITE_SERVICE);
            graniteDestination = configurationAdmin.createFactoryConfiguration(GraniteConstants.DESTINATION, null);
            graniteDestination.update(properties);
        }

        // Register the asynchronous service used with SecurityService
        {
            Dictionary properties = new Hashtable();
            properties.put("id", FlexConstants.KERNEOS_SERVICE_ASYNC_SECURITY);
            properties.put("service", FlexConstants.GRAVITY_SERVICE);

            gravityDestination = configurationAdmin.createFactoryConfiguration(GraniteConstants.DESTINATION, null);
            gravityDestination.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("destination", FlexConstants.KERNEOS_SERVICE_ASYNC_SECURITY);

            eaConfig = configurationAdmin.createFactoryConfiguration(EAConstants.CONFIGURATION_ID, null);
            eaConfig.update(properties);
        }
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        LOGGER.debug("Stop FlexSecurityService");

        graniteDestination.delete();
        gravityDestination.delete();
        eaConfig.delete();

        gcr.unregisterClasses(FlexConstants.KERNEOS_SERVICE_SECURITY);
        gcr.unregisterClasses(FlexConstants.KERNEOS_SERVICE_ASYNC_SECURITY);
    }

    /**
     * Security service id
     */
    public String getId() {
        return FlexConstants.KERNEOS_SERVICE_SECURITY;
    }

    public boolean logIn(String username, String password) {
        FlexContext flexContext = FlexContext.getCurrent();
        return securityService.logIn(flexContext.getApplication(), username, password);
    }

    public boolean logOut() {
        FlexContext flexContext = FlexContext.getCurrent();
        return securityService.logOut(flexContext.getApplication());
    }

    public Profile getProfile() {
        return FlexContext.getCurrent().getApplication().getProfileManager().getProfile();
    }

    public KerneosSession getSession() {
        return KerneosSession.getCurrent();
    }
}
