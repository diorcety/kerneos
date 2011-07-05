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
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import org.ow2.kerneos.core.IApplicationBundle;
import org.ow2.kerneos.core.IModuleBundle;
import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.KerneosContext;
import org.ow2.kerneos.core.config.generated.Authentication;
import org.ow2.kerneos.core.manager.DefaultKerneosLogin;
import org.ow2.kerneos.core.manager.DefaultKerneosProfile;
import org.ow2.kerneos.core.manager.DefaultKerneosRoles;
import org.ow2.kerneos.core.manager.KerneosLogin;
import org.ow2.kerneos.core.manager.KerneosProfile;
import org.ow2.kerneos.core.manager.KerneosRoles;
import org.ow2.kerneos.core.service.util.Base64;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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
    private static final String PREFIX = "bundle:/";
    private static final String PREFIX2 = "bundle://";
    private static final String PREFIX_EQUINOX = "bundleresource:/";
    private static final String PREFIX_EQUINOX2 = "bundleresource://";

    @ServiceProperty(name = "ID")
    String contextID;

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

    @Requires(id = "login", optional = true, defaultimplementation = DefaultKerneosLogin.class, proxy = false)
    private KerneosLogin kerneosLogin;

    @Requires(id = "roles", optional = true, defaultimplementation = DefaultKerneosRoles.class, proxy = false)
    private KerneosRoles kerneosRoles;

    @Requires(id = "profile", optional = true, defaultimplementation = DefaultKerneosProfile.class, proxy = false)
    private KerneosProfile kerneosProfile;

    @Requires(id = "application")
    private IApplicationBundle applicationBundle;

    private Configuration gavityChannel, graniteChannel;

    private Map<String, IModuleBundle> moduleBundleMap = new HashMap<String, IModuleBundle>();

    @Validate
    private void start() throws NamespaceException, IOException {
        contextID = applicationBundle.getId();

        logger.debug("Start Kerneos Application: " + applicationBundle.getId());

        String applicationURL = applicationBundle.getApplication().getApplicationUrl();
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.GRAVITY_CHANNEL + applicationBundle.getId());
            properties.put("uri", applicationURL + KerneosConstants.GRAVITY_CHANNEL_URI);
            properties.put("context", contextID);
            properties.put("gravity", "true");
            gavityChannel = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Channel", null);
            gavityChannel.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", KerneosConstants.GRANITE_CHANNEL + applicationBundle.getId());
            properties.put("uri", applicationURL + KerneosConstants.GRANITE_CHANNEL_URI);
            properties.put("context", contextID);
            properties.put("gravity", "false");
            graniteChannel = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Channel", null);
            graniteChannel.update(properties);
        }

        String name = applicationBundle.getBundle().getResource(KerneosConstants.KERNEOS_PATH).toString();

         //The name parameter must not end with slash ('/')
        if ((name.charAt(name.length()-1) == '/') || (name.charAt(name.length()-1) == '\\')) {
          name = name.substring(0, name.length()-1);
        }

        logger.info("Register Resources - Url: " + applicationURL + " - Alias: " + name);

        // Register Kerneos Application resources
        httpService.registerResources(applicationURL,
                name,this);

        httpService.registerResources(applicationURL + "/" + KerneosConstants.KERNEOS_SWF_NAME,
                KerneosConstants.KERNEOS_SWF_NAME, this);

        logger.info("Create Map \"" + applicationBundle.getId() + "\" -> \"" + applicationURL + "\"");

        logger.info("###########################################################################");
        logger.info("Your application main URL is register as : " + applicationURL);
        logger.info("Example URL : http://localhost:8080" + applicationURL + "/index.html");
        logger.info("or http://localhost:8080/bridge" + applicationURL + "/index.html if you are in Tomcat");
        logger.info("###########################################################################");
    }

    @Invalidate
    private void stop() throws IOException {
        logger.debug("Stop Kerneos Application: " + applicationBundle.getId());

        gavityChannel.delete();
        graniteChannel.delete();

        String applicationURL = applicationBundle.getApplication().getApplicationUrl();

        logger.info("Destroy Map \"" + applicationBundle.getId() + "\" -> \"" + applicationURL + "\"");

        httpService.unregister(applicationURL);
        httpService.unregister(applicationURL + "/" + KerneosConstants.KERNEOS_SWF_NAME);
    }

    /**
     * Called when an Module Instance is registered.
     *
     * @param moduleBundle The instance of module.
     * @throws NamespaceException Never thrown.
     */
    @Bind(aggregate = true, optional = true)
    private void bindModuleBundle(final IModuleBundle moduleBundle) throws NamespaceException {
        synchronized (moduleBundleMap) {
            moduleBundleMap.put(moduleBundle.getId(), moduleBundle);
        }

        String name = moduleBundle.getBundle().getResource(KerneosConstants.KERNEOS_PATH).toString();

         //The name parameter must not end with slash ('/')
        if ((name.charAt(name.length()-1) == '/') || (name.charAt(name.length()-1) == '\\')) {
          name = name.substring(0, name.length()-1);
        }

        logger.info("Register Module Resources - Url: " + applicationBundle.getApplication().getApplicationUrl() + "/" +
                KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleBundle.getId() + " - Alias: " + name);

        httpService.registerResources(applicationBundle.getApplication().getApplicationUrl() + "/" +
                KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleBundle.getId(), name, this);
    }

    /**
     * Called when an Module Instance is unregistered.
     *
     * @param moduleBundle The instance of module.
     */
    @Unbind
    private void unbindModuleBundle(final IModuleBundle moduleBundle) {
        synchronized (moduleBundleMap) {
            moduleBundleMap.remove(moduleBundle.getId());
        }

        httpService.unregister(applicationBundle.getApplication().getApplicationUrl() + "/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleBundle.getId());
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
    public URL getResource(final String name) {
        if (name.startsWith(PREFIX) ||
                name.startsWith(PREFIX_EQUINOX)) {
            try {
                // Fix apache.felix.http.jetty bug for felix and equinox
                if ((name.startsWith(PREFIX) && !name.startsWith(PREFIX2))) {
                    String resourceUrl = PREFIX2 + name.substring(PREFIX.length());
                    return new URL(resourceUrl);
                } else {
                    if (name.startsWith(PREFIX_EQUINOX) && !name.startsWith(PREFIX_EQUINOX2)) {
                       return new URL(PREFIX_EQUINOX2 + name.substring(PREFIX_EQUINOX.length()));
                    } else {
                        return new URL(name);
                    }
                }
            } catch (MalformedURLException e) {
                return null;
            }
        } else {
            if (name.startsWith(KerneosConstants.KERNEOS_SWF_NAME)) {
                return this.getClass().getClassLoader().getResource(KerneosConstants.KERNEOS_SWF_NAME);
            }
        }
        return null;
    }

    /**
     * Called for allow or deny an access.
     *
     * @param request  is the object containing the request information.
     * @param response is the object containing the response information.
     * @return return true if the user have the credential.
     * @throws IOException neven happen.
     */
    public boolean handleSecurity(final HttpServletRequest request,
                                  final HttpServletResponse response) throws IOException {
        //Disable Cache
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        KerneosContext kerneosContext = KerneosContext.getCurrentContext();
        kerneosContext.setApplicationBundle(applicationBundle);
        kerneosContext.setLoginManager(kerneosLogin);
        kerneosContext.setProfileManager(kerneosProfile);
        kerneosContext.setRolesManager(kerneosRoles);

        kerneosCore.updateContext(request);

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
