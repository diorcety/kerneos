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

package org.ow2.kerneos.core.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.osgi.GraniteConstants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import org.ow2.kerneos.core.ApplicationBundle;
import org.ow2.kerneos.core.ModuleBundle;
import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.KerneosContext;
import org.ow2.kerneos.core.config.generated.Authentication;
import org.ow2.kerneos.core.manager.KerneosLogin;
import org.ow2.kerneos.core.manager.KerneosProfile;
import org.ow2.kerneos.core.manager.KerneosRoles;
import org.ow2.kerneos.core.util.Base64;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * The specific http context used by Kerneos.
 */
@Component
@Provides
public class KerneosHttpService implements HttpContext {

    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosHttpService.class);

    @ServiceProperty(name = "ID")
    @Property(name = "ID", mandatory = true)
    private String application;


    /**
     * OSGi HTTPService.
     */
    @Requires
    private HttpService httpService;

    @Requires
    private IKerneosCore kerneosCore;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private IKerneosSecurityService kerneosSecurityService;

    @Requires(id = "login", proxy = false, nullable = false)
    private KerneosLogin kerneosLogin;

    @Requires(id = "roles", proxy = false, nullable = false)
    private KerneosRoles kerneosRoles;

    @Requires(id = "profile", proxy = false, nullable = false)
    private KerneosProfile kerneosProfile;

    private Configuration gavityChannel, graniteChannel;

    private ApplicationBundle applicationBundle;

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws NamespaceException, IOException {
        logger.debug("Start Kerneos Application: " + application);

        applicationBundle = kerneosCore.getApplicationBundle(application);

        // Register Granite/Gravity channels
        String applicationURL = applicationBundle.getApplication().getApplicationUrl();
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.GRAVITY_CHANNEL + applicationBundle.getId());
            properties.put("uri", applicationURL + KerneosConstants.GRAVITY_CHANNEL_URI);
            properties.put("context", application);
            properties.put("gravity", "true");
            gavityChannel = configurationAdmin.createFactoryConfiguration(GraniteConstants.CHANNEL, null);
            gavityChannel.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.GRANITE_CHANNEL + applicationBundle.getId());
            properties.put("uri", applicationURL + KerneosConstants.GRANITE_CHANNEL_URI);
            properties.put("context", application);
            properties.put("gravity", "false");
            graniteChannel = configurationAdmin.createFactoryConfiguration(GraniteConstants.CHANNEL, null);
            graniteChannel.update(properties);
        }

        // Register Kerneos Application resources
        httpService.registerResources(applicationURL, "", this);
        logger.info("Create Map \"" + application + "\" -> \"" + applicationURL + "\"");
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        logger.debug("Stop Kerneos Application: " + application);

        String applicationURL = applicationBundle.getApplication().getApplicationUrl();

        // Unregister Granite/Gravity channels
        gavityChannel.delete();
        graniteChannel.delete();

        // Unregister Kerneos Application resources
        httpService.unregister(applicationURL);
        logger.info("Destroy Map \"" + applicationBundle.getId() + "\" -> \"" + applicationURL + "\"");
    }

    /**
     * Return the Mine corresponding to the url.
     *
     * @param url is the string corresponding to the url request.
     * @return the Mine string.
     */
    public String getMimeType(final String url) {
        return null;
    }

    /**
     * Get the URL of the resource corresponding to a URL string.
     *
     * @param name url is the string corresponding to the url request.
     * @return the URL type which permits to acced to the resource.
     */
    public URL getResource(String name) {
        logger.debug(name);
        String path = KerneosContext.getCurrentContext().getPath();
        ModuleBundle moduleBundle = KerneosContext.getCurrentContext().getModuleBundle();
        ApplicationBundle applicationBundle = KerneosContext.getCurrentContext().getApplicationBundle();

        URL url = null;
        URLConnection connection = null;
        try {
            if (path != null) {
                if (moduleBundle != null) {
                    // Module files
                    url = moduleBundle.getBundle().getResource(KerneosConstants.KERNEOS_PATH + path);
                    if (url == null) {
                        logger.error("Cannot get url : " + KerneosConstants.KERNEOS_PATH + path);
                        return null;
                    }
                    connection = url.openConnection();
                } else {
                    if (applicationBundle != null) {
                        // Application files
                        if (path.equals("/")) {
                            // Find index file in indexes
                            for (String index : KerneosConstants.KERNEOS_INDEX_FILES) {
                                url = applicationBundle.getBundle().getResource(KerneosConstants.KERNEOS_PATH + index);
                                try {
                                    connection = url.openConnection();
                                    break;
                                } catch (Exception e) {
                                    logger.warn("Got exception on openConnection: " + e);
                                    url = null;
                                }
                            }
                        } else {
                            // Get bundle's version before Kerneos
                            url = applicationBundle.getBundle().getResource(KerneosConstants.KERNEOS_PATH + path);
                            try {
                                connection = url.openConnection();
                            } catch (Exception e) {
                                url = this.getClass().getClassLoader().getResource(KerneosConstants.KERNEOS_PATH + path);
                                connection = url.openConnection();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Got exception: " + e);
            return null;
        }

        // Return the size
        if (connection != null)
            KerneosContext.getCurrentContext().getResponse().setHeader("Content-Length", Integer.toString(connection.getContentLength()));

        if (url == null) {
            logger.warn("returning null url");
        }
        return url;
    }

    /**
     * Called for allow or deny an access.
     *
     * @param request  is the object containing the request information.
     * @param response is the object containing the response information.
     * @return return true if the user have the credential.
     * @throws IOException should never happen.
     */

    public boolean handleSecurity(final HttpServletRequest request,
                                  final HttpServletResponse response) throws IOException {
        // Disable Cache
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // Update context
        KerneosContext kerneosContext = KerneosContext.getCurrentContext();
        kerneosContext.setApplicationBundle(applicationBundle);
        kerneosContext.setLoginManager(kerneosLogin);
        kerneosContext.setProfileManager(kerneosProfile);
        kerneosContext.setRolesManager(kerneosRoles);

        kerneosCore.updateContext(request, response);

        switch (kerneosSecurityService.authorize()) {
            case NO_ERROR:
                return true;

            default:
                if (applicationBundle.getApplication().getAuthentication() == Authentication.WWW) {
                    // Show WWW Authentication box of the web browser
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null) {
                        // Check AUTH type
                        if (authHeader.toUpperCase().startsWith("BASIC ")) {

                            // Get Login/Password
                            String userpassword = authHeader.substring(6);
                            userpassword = new String(Base64.decode(userpassword), "ISO-8859-1");
                            String[] data = userpassword.split("\\:");
                            String user = (data.length >= 1) ? data[0] : null;
                            String password = (data.length >= 2) ? data[1] : null;

                            // Auth
                            if (kerneosSecurityService.logIn(user, password))
                                return true;
                        }
                    }

                    response.setHeader("WWW-Authenticate", "Basic");
                }
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentLength(0);
                response.flushBuffer();
                return false;
        }
    }
}
