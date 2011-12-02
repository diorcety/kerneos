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

import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

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
import org.osgi.service.event.EventAdmin;

import org.ow2.kerneos.common.service.KerneosModule;
import org.ow2.kerneos.common.config.generated.Application;
import org.ow2.kerneos.common.config.generated.Module;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * This is the component which handles the arrival/departure of the Kerneos' modules.
 */
@Component
@Instantiate
@Provides
public final class ConfigurationService implements GraniteDestination {
    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(ConfigurationService.class);

    @Requires
    private EventAdmin eventAdmin;

    @Requires
    private GraniteClassRegistry gcr;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private ICore flexCore;

    private Configuration eaConfig, graniteDestination, gravityDestination;

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        LOGGER.debug("Start Flex Tracker");

        // Register the classes used with "kerneosConfig" service
        gcr.registerClasses(FlexConstants.KERNEOS_SERVICE_CONFIGURATION, ConfigObjects.list());

        // Register the classes used with event admin
        gcr.registerClasses(FlexConstants.KERNEOS_SERVICE_ASYNC_CONFIGURATION, ConfigObjects.list());

        // Register the asynchronous service used with Tracker
        {
            Dictionary properties = new Hashtable();
            properties.put("id", FlexConstants.KERNEOS_SERVICE_ASYNC_CONFIGURATION);
            properties.put("service", FlexConstants.GRAVITY_SERVICE);

            gravityDestination = configurationAdmin.createFactoryConfiguration(GraniteConstants.DESTINATION, null);
            gravityDestination.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("destination", FlexConstants.KERNEOS_SERVICE_ASYNC_CONFIGURATION);

            eaConfig = configurationAdmin.createFactoryConfiguration(EAConstants.CONFIGURATION_ID, null);
            eaConfig.update(properties);
        }

        // Register the synchronous service used with Tracker
        {
            Dictionary properties = new Hashtable();
            properties.put("id", FlexConstants.KERNEOS_SERVICE_CONFIGURATION);
            properties.put("service", FlexConstants.GRANITE_SERVICE);

            graniteDestination = configurationAdmin.createFactoryConfiguration(GraniteConstants.DESTINATION, null);
            graniteDestination.update(properties);
        }
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        LOGGER.debug("Stop FlexConstants Tracker");

        // Remove the configurations
        eaConfig.delete();
        gravityDestination.delete();
        graniteDestination.delete();

        // Unregister the different used classes
        gcr.unregisterClasses(FlexConstants.KERNEOS_SERVICE_ASYNC_CONFIGURATION);
        gcr.unregisterClasses(FlexConstants.KERNEOS_SERVICE_CONFIGURATION);
    }

    /**
     * Get the an application.
     *
     * @return the Kerneos application.
     */
    public Application getApplication() {
        return FlexContext.getCurrent().getApplication().getConfiguration();
    }

    /**
     * Get the module list.
     *
     * @return the module list.
     */
    public Collection<Module> getModules() {
        List<Module> retModules = new LinkedList<Module>();
        for (KerneosModule module : flexCore.getModules()) {
            retModules.add(module.getConfiguration());
        }
        return retModules;
    }

    /**
     * Get the service id.
     *
     * @return the service id.
     */
    public String getId() {
        return FlexConstants.KERNEOS_SERVICE_CONFIGURATION;
    }
}
