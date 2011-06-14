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

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.core.IApplicationInstance;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


/**
 * The core of Kerneos.
 */
@Component
@Instantiate
public class KerneosCore {

    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosConfigurationService.class);

    private Map<String, ApplicationInstanceImpl> applicationInstanceMap = new HashMap<String, ApplicationInstanceImpl>();


    private Configuration granite, gravity, graniteSecurity, gravitySecurity;

    /**
     * Used for holding the different configurations associated with an Application instance.
     */
    private class ApplicationInstanceImpl {
        private Configuration gavityChannel, graniteChannel;

        /**
         * Create all the configuration associated with this instance.
         */
        public ApplicationInstanceImpl(IApplicationInstance applicationInstance) throws IOException {

            String applicationURL = applicationInstance.getConfiguration().getApplicationUrl();
            {
                Dictionary properties = new Hashtable();
                properties.put("id", KerneosConstants.GRAVITY_CHANNEL + applicationInstance.getId());
                properties.put("uri", applicationURL + KerneosConstants.GRAVITY_CHANNEL_URI);
                properties.put("context", KerneosConstants.KERNEOS_CONTEXT_NAME);
                properties.put("gravity", "true");
                gavityChannel = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Channel", null);
                gavityChannel.update(properties);
            }
            {
                Dictionary properties = new Hashtable();
                properties.put("id", KerneosConstants.GRANITE_CHANNEL + applicationInstance.getId());
                properties.put("uri", applicationURL + KerneosConstants.GRANITE_CHANNEL_URI);
                properties.put("context", KerneosConstants.KERNEOS_CONTEXT_NAME);
                properties.put("gravity", "false");
                graniteChannel = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Channel", null);
                graniteChannel.update(properties);
            }
        }

        /**
         * Dispose all the configuration associated with this instance.
         */
        public void dispose() throws IOException {
            gavityChannel.delete();
            graniteChannel.delete();
        }
    }

    @Requires
    private ConfigurationAdmin configurationAdmin;

    /**
     * Constructor used by iPojo.
     */
    private KerneosCore() {
    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        logger.debug("Start KerneosCore");

        // Gravity Configurations
        {
            Dictionary properties = new Hashtable();
            properties.put("service", KerneosConstants.GRAVITY_SERVICE);
            gravitySecurity = configurationAdmin.createFactoryConfiguration("org.ow2.kerneos.core.service.impl.granite.GraniteSecurityWrapper", null);
            gravitySecurity.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.GRAVITY_SERVICE);
            properties.put("messageTypes", "flex.messaging.messages.AsyncMessage");
            properties.put("defaultAdapter", EAConstants.ADAPTER_ID);
            gravity = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Service", null);
            gravity.update(properties);
        }

        // Granite Configurations
        {
            Dictionary properties = new Hashtable();
            properties.put("service", KerneosConstants.GRANITE_SERVICE);
            graniteSecurity = configurationAdmin.createFactoryConfiguration("org.ow2.kerneos.core.service.impl.granite.GraniteSecurityWrapper", null);
            graniteSecurity.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.GRANITE_SERVICE);
            granite = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Service", null);
            granite.update(properties);
        }
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        logger.debug("Stop KerneosCore");

        // Dispose configuration
        gravity.delete();
        gravitySecurity.delete();
        granite.delete();
        graniteSecurity.delete();
    }

    /**
     * Called when an Application instance is registered.
     *
     * @param applicationInstance the instance of an application
     */
    @Bind(aggregate = true, optional = true)
    private void bindApplicationInstance(final IApplicationInstance applicationInstance) throws IOException {
        ApplicationInstanceImpl applicationImpl = new ApplicationInstanceImpl(applicationInstance);

        synchronized (applicationInstanceMap) {
            applicationInstanceMap.put(applicationInstance.getId(), applicationImpl);
        }
    }

    /**
     * Called when an Application instance is unregistered.
     *
     * @param applicationInstance the instance of an application
     */
    @Unbind
    private void unbindApplicationInstance(final IApplicationInstance applicationInstance) throws IOException {
        synchronized (applicationInstanceMap) {
            ApplicationInstanceImpl applicationImpl = applicationInstanceMap.get(applicationInstance.getId());
            applicationImpl.dispose();
        }

    }
}
