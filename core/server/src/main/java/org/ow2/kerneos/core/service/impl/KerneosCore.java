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

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.granite.osgi.ConfigurationHelper;
import org.osgi.service.http.NamespaceException;

import org.ow2.kerneos.core.IApplicationInstance;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

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

    private ComponentInstance granite, gravity, graniteSecurity, gravitySecurity;

    /**
     * Used for holding the different configurations associated with an Application instance.
     */
    private class ApplicationInstanceImpl {
        private ComponentInstance gavityChannel, graniteChannel;

        /**
         * Create all  the configuration associated with this instance.
         */
        public ApplicationInstanceImpl(IApplicationInstance applicationInstance) throws MissingHandlerException, ConfigurationException, UnacceptableConfiguration, NamespaceException {

            String applicationURL = applicationInstance.getConfiguration().getApplicationUrl();

            gavityChannel = confHelper.newGravityChannel(KerneosConstants.GRAVITY_CHANNEL + applicationInstance.getId(),
                    applicationURL + KerneosConstants.GRAVITY_CHANNEL_URI);
            graniteChannel = confHelper.newGraniteChannel(KerneosConstants.GRANITE_CHANNEL + applicationInstance.getId(),
                    applicationURL + KerneosConstants.GRANITE_CHANNEL_URI);
        }

        /**
         * Dispose all the configuration associated with this instance.
         */
        public void dispose() {
            gavityChannel.dispose();
            graniteChannel.dispose();
        }
    }

    @Requires
    ConfigurationHelper confHelper;

    @Requires(from = "org.ow2.kerneos.core.service.impl.granite.GraniteSecurityWrapper")
    private Factory securityFactory;

    /**
     * Constructor used by iPojo.
     */
    private KerneosCore() {
    }

    /**
     * Called when all the component dependencies are met.
     *
     * @throws MissingHandlerException   issue during GraniteDS configuration.
     * @throws ConfigurationException    issue during GraniteDS configuration.
     * @throws UnacceptableConfiguration issue during GraniteDS configuration.
     * @throws NamespaceException        an invalid application url.
     */
    @Validate
    private void start() throws MissingHandlerException, ConfigurationException,
            UnacceptableConfiguration, NamespaceException {
        logger.debug("Start KerneosCore");

        // Gravity Configuration Instances
        {
            Dictionary properties = new Hashtable();
            properties.put("SERVICE", KerneosConstants.GRAVITY_SERVICE);
            gravitySecurity = securityFactory.createComponentInstance(properties);
        }
        gravity = confHelper.newGravityService(KerneosConstants.GRAVITY_SERVICE, EAConstants.ADAPTER_ID);


        // Granite Configuration Instances
        {
            Dictionary properties = new Hashtable();
            properties.put("SERVICE", KerneosConstants.GRANITE_SERVICE);
            graniteSecurity = securityFactory.createComponentInstance(properties);
        }
        granite = confHelper.newGraniteService(KerneosConstants.GRANITE_SERVICE);
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() {
        logger.debug("Stop KerneosCore");

        // Dispose configuration
        gravity.dispose();
        gravitySecurity.dispose();
        granite.dispose();
        graniteSecurity.dispose();
    }

    @Bind(aggregate = true, optional = true)
    private void bindApplicationInstance(final IApplicationInstance applicationInstance) throws MissingHandlerException, NamespaceException, ConfigurationException, UnacceptableConfiguration {
        ApplicationInstanceImpl applicationImpl = new ApplicationInstanceImpl(applicationInstance);

        synchronized (applicationInstanceMap) {
            applicationInstanceMap.put(applicationInstance.getId(), applicationImpl);
        }
    }

    @Unbind
    private void unbindApplicationInstance(final IApplicationInstance applicationInstance) {
        synchronized (applicationInstanceMap) {
            ApplicationInstanceImpl applicationImpl = applicationInstanceMap.get(applicationInstance.getId());
            applicationImpl.dispose();
        }

    }
}
