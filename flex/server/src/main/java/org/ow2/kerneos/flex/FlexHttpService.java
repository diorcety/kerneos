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
import org.apache.felix.ipojo.annotations.Invalidate;
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

import org.ow2.kerneos.common.KerneosConstants;
import org.ow2.kerneos.common.service.KerneosApplication;
import org.ow2.kerneos.common.service.KerneosModule;
import org.ow2.kerneos.common.service.KerneosSecurityService;
import org.ow2.kerneos.common.config.generated.Authentication;
import org.ow2.kerneos.flex.util.Base64;
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
public class FlexHttpService implements HttpContext {
    public static final String APPLICATION = "APPLICATION";
    /**
     * The logger.
     */
    private static final Log LOGGER = LogFactory.getLog(FlexHttpService.class);

    private static final String KERNEOS_SESSION_KEY = "KERNEOS-SESSION-";

    private static final String AUTORIZATION_HEADER = "Authorization";
    private static final String AUTORIZATION_TYPE = "BASIC ";
    private static final String AUTORIZATION_CHARSET = "ISO-8859-1";

    @ServiceProperty(name = "ID")
    private String id;

    /**
     * OSGi HTTPService.
     */
    @Requires
    private HttpService httpService;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires
    private KerneosSecurityService kerneosSecurityService;

    @Requires(id = APPLICATION)
    private KerneosApplication application;

    @Requires
    private ICore core;

    private Configuration gavityChannel, graniteChannel;
    private String applicationURL;

    private FlexHttpService() {

    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws NamespaceException, IOException {
        LOGGER.debug("Start Flex Application: " + application);

        id = application.getId();

        // Register Granite/Gravity channels
        applicationURL = application.getConfiguration().getApplicationUrl();
        {
            Dictionary properties = new Hashtable();
            properties.put("id", FlexConstants.GRAVITY_CHANNEL + application.getId());
            properties.put("uri", applicationURL + FlexConstants.GRAVITY_CHANNEL_URI);
            properties.put("context", id);
            properties.put("gravity", "true");
            gavityChannel = configurationAdmin.createFactoryConfiguration(GraniteConstants.CHANNEL, null);
            gavityChannel.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", FlexConstants.GRANITE_CHANNEL + application.getId());
            properties.put("uri", applicationURL + FlexConstants.GRANITE_CHANNEL_URI);
            properties.put("context", id);
            properties.put("gravity", "false");
            graniteChannel = configurationAdmin.createFactoryConfiguration(GraniteConstants.CHANNEL, null);
            graniteChannel.update(properties);
        }

        LOGGER.info("Create Map \"" + application + "\" -> \"" + applicationURL + "\"");
        // Register Kerneos Application resources
        httpService.registerResources(applicationURL, "", this);
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        LOGGER.debug("Stop Flex Application: " + application);

        // Unregister Granite/Gravity channels
        gavityChannel.delete();
        graniteChannel.delete();

        // Unregister Kerneos Application resources
        httpService.unregister(applicationURL);
        LOGGER.info("Destroy Map \"" + id + "\" -> \"" + applicationURL + "\"");
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
        String path = FlexContext.getCurrent().getPath();
        KerneosModule moduleBundle = FlexContext.getCurrent().getModule();
        KerneosApplication applicationBundle = FlexContext.getCurrent().getApplication();

        URL url = null;
        URLConnection connection = null;
        try {
            if (path != null) {
                if (moduleBundle != null) {
                    // Module files
                    url = moduleBundle.getBundle().getResource(KerneosConstants.KERNEOS_PATH + path);
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
                                    url = null;
                                }
                            }
                        } else {
                            // Get bundle's version before Kerneos
                            url = applicationBundle.getBundle().getResource(KerneosConstants.KERNEOS_PATH + path);
                            try {
                                connection = url.openConnection();
                            } catch (Exception e) {
                                url = this.getClass().getClassLoader().getResource(KerneosConstants.KERNEOS_PATH +
                                        path);
                                connection = url.openConnection();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }

        // Return the size
        if (connection != null) {
            FlexContext.getCurrent().getResponse().setHeader("Content-Length",
                    Integer.toString(connection.getContentLength()));
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

        FlexContext flexContext = FlexContext.getCurrent();
        flexContext.setApplication(application);
        core.updateContext(request, response);

        KerneosSecurityService.SecurityError error = kerneosSecurityService.isAuthorized(application,
                flexContext.getModule(), flexContext.getService(), flexContext.getMethod());
        switch (error) {
            case NO_ERROR:
                return true;

            default:
                if (application.getConfiguration().getAuthentication() == Authentication.WWW) {
                    // Show WWW Authentication box of the web browser
                    String authHeader = request.getHeader(AUTORIZATION_HEADER);
                    if (authHeader != null) {
                        // Check AUTH type
                        if (authHeader.toUpperCase().startsWith(AUTORIZATION_TYPE)) {

                            // Get Login/Password
                            String userpassword = authHeader.substring(AUTORIZATION_TYPE.length());
                            userpassword = new String(Base64.decode(userpassword), AUTORIZATION_CHARSET);
                            String[] data = userpassword.split("\\:");
                            String user = (data.length >= 1) ? data[0] : null;
                            String password = (data.length >= 2) ? data[1] : null;

                            // Auth
                            if (kerneosSecurityService.logIn(application, user, password)) {
                                return true;
                            }
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
