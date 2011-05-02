/**
 * Kerneos
 * Copyright (C) 2009 Bull S.A.S.
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

package org.ow2.jasmine.kerneos.service.impl;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import org.ow2.jasmine.kerneos.config.generated.KerneosConfig;
import org.ow2.jasmine.kerneos.config.generated.ObjectFactory;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

@Component
@Instantiate
@Provides
public final class KerneosCore implements IKerneosCore {

    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosConfigService.class);

    /**
     * OSGi HTTPService
     */
    @Requires
    private HttpService httpService;

    /**
     * Current HTTP Context
     */
    private HttpContext httpContext;


    /**
     * The JAXB context for rules packages serialization/deserialization. Must
     * be declared with all the potentially involved classes.
     */
    private JAXBContext jaxbContext;

    /**
     * KerneosConfig
     */
    private KerneosConfig kerneosConfig;


    /**
     * Configuration component instances
     */
    private ComponentInstance gravityService, gravityDestination, gravityAdapter, gravityChannel;
    private ComponentInstance graniteService, graniteChannel;

    @Requires(from = "org.granite.config.flex.Service")
    private Factory serviceFactory;

    @Requires(from = "org.granite.config.flex.Channel")
    private Factory channelFactory;

    @Requires(from = "org.granite.config.flex.Destination")
    private Factory destinationFactory;

    @Requires(from = "org.granite.config.flex.Adapter")
    private Factory adapterFactory;


    private KerneosCore(final BundleContext bundleContext) throws Exception {
        jaxbContext = JAXBContext.newInstance(
                ObjectFactory.class.getPackage().getName(),
                ObjectFactory.class.getClassLoader());
        loadKerneosConfig(bundleContext);

    }

    @Validate
    private void start() throws MissingHandlerException, ConfigurationException,
            UnacceptableConfiguration, NamespaceException {
        logger.debug("Start KerneosCore");
        httpContext = new KerneosHttpContext();

        // Register Kerneos resources
        httpService.registerResources(kerneosConfig.getBaseUrl(), KerneosConstants.KERNEOS_PATH, httpContext);
        logger.info("Register Kerneos' resources: " + kerneosConfig.getBaseUrl());

        // Gravity Configuration Instances
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", KerneosConstants.GRAVITY_ADAPTER);
            gravityAdapter = adapterFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", KerneosConstants.GRAVITY_SERVICE);
            properties.put("MESSAGETYPES", "flex.messaging.messages.AsyncMessage");
            properties.put("DEFAULT_ADAPTER", KerneosConstants.GRAVITY_ADAPTER);
            gravityService = serviceFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", KerneosConstants.GRAVITY_CHANNEL);
            properties.put("CLASS", "org.granite.gravity.channels.GravityChannel");
            properties.put("ENDPOINT_URI", kerneosConfig.getBaseUrl() + KerneosConstants.GRAVITY_CHANNEL_URI);
            gravityChannel = channelFactory.createComponentInstance(properties);
        }
        {
            Collection<String> channels = new LinkedList<String>();
            channels.add(KerneosConstants.GRAVITY_CHANNEL);
            Dictionary properties = new Hashtable();
            properties.put("ID", KerneosConstants.GRAVITY_DESTINATION);
            properties.put("SERVICE", KerneosConstants.GRAVITY_SERVICE);
            properties.put("CHANNELS", channels);
            gravityDestination = destinationFactory.createComponentInstance(properties);
        }

        // Granite Configuration Instances
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", KerneosConstants.GRANITE_SERVICE);
            graniteService = serviceFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", KerneosConstants.GRANITE_CHANNEL);
            properties.put("ENDPOINT_URI", kerneosConfig.getBaseUrl() + KerneosConstants.GRANITE_CHANNEL_URI);
            graniteChannel = channelFactory.createComponentInstance(properties);
        }
    }

    @Invalidate
    private void stop() {
        logger.debug("Stop KerneosCore");

        // Unregister kerneos resources
        httpService.unregister(kerneosConfig.getBaseUrl());
        logger.info("Unregister Kerneos' resources: " + kerneosConfig.getBaseUrl());

        // Dispose gravity configuration instances
        gravityDestination.dispose();
        gravityService.dispose();
        gravityAdapter.dispose();
        gravityChannel.dispose();

        // Dispose granite configuration instances
        graniteService.dispose();
        graniteChannel.dispose();
    }

    /**
     * Load the Kerneos config file and build the configuration object
     */
    private void loadKerneosConfig(final BundleContext bundleContext) throws Exception {

        // Retrieve the kerneos config file
        URL url = bundleContext.getBundle().getResource(KerneosConstants.KERNEOS_CONFIG_FILE);

        if (url != null) {

            // Load the file
            logger.info("Loading file : {0}", url.getFile());
            InputStream resource = url.openStream();

            // Unmarshall it
            try {
                // Create an unmarshaller
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                // Deserialize the configuration file
                kerneosConfig = (KerneosConfig) unmarshaller.unmarshal(resource);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                resource.close();
            }
        } else {
            throw new Exception("No configuration file available at " + KerneosConstants.KERNEOS_CONFIG_FILE);
        }
    }


    public  void register(final String alias, final String name) throws NamespaceException {
        httpService.registerResources(kerneosConfig.getBaseUrl() + "/" + alias, name, httpContext);
    }

    public void unregister(final String alias) {
        httpService.unregister(kerneosConfig.getBaseUrl() + "/" + alias);
    }

    public KerneosConfig getKerneosConfig() {
        return kerneosConfig;
    }
}
