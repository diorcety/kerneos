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
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Unbind;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import org.ow2.kerneos.core.IApplicationBundle;
import org.ow2.kerneos.core.IModuleBundle;
import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.KerneosContext;
import org.ow2.kerneos.core.config.generated.Authentication;
import org.ow2.kerneos.core.service.util.Base64;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The specific http context used by Kerneos.
 */
@Component
@Instantiate
@Provides(properties = @StaticServiceProperty(name = "ID", value = KerneosConstants.KERNEOS_CONTEXT_NAME, type = "string"))
public class KerneosHttpService implements HttpContext {

    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosHttpService.class);
    private static final String PREFIX = "bundle:/";
    private static final String PREFIX2 = "bundle://";


    private static ThreadLocal<HttpServletRequest> httpServletRequestThreadLocal = new ThreadLocal<HttpServletRequest>() {
        @Override
        protected HttpServletRequest initialValue() {
            return (null);
        }
    };

    @ServiceProperty(name = "ID")
    String id;

    /**
     * OSGi HTTPService.
     */
    @Requires
    private HttpService httpService;

    @Requires
    private IKerneosSecurityService kerneosSecurityService;

    private Map<String, IApplicationBundle> applicationBundleMap = new HashMap<String, IApplicationBundle>();
    private Map<String, IModuleBundle> moduleBundleMap = new HashMap<String, IModuleBundle>();

    /**
     * Called when an Application Instance is registered.
     *
     * @param applicationBundle The instance of application.
     */
    @Bind(aggregate = true, optional = true)
    private void bindApplicationBundle(final IApplicationBundle applicationBundle) throws NamespaceException {
        String applicationURL = applicationBundle.getApplication().getApplicationUrl();

        // Register Kerneos Application resources
        httpService.registerResources(applicationURL,
                applicationBundle.getBundle().getResource(KerneosConstants.KERNEOS_PATH).toString(),
                this);
        httpService.registerResources(applicationURL + "/" + KerneosConstants.KERNEOS_SWF_NAME,
                KerneosConstants.KERNEOS_SWF_NAME, this);

        synchronized (applicationBundleMap) {
            applicationBundleMap.put(applicationBundle.getId(), applicationBundle);
        }


        // Register Kerneos Module resources for this application
        for (IModuleBundle moduleBundle : moduleBundleMap.values()) {
            registerApplicationModule(applicationBundle, moduleBundle);
        }

        logger.info("Register \"" + applicationBundle.getId() + "\" resources: " + applicationURL);
    }

    /**
     * Called when an Application Instance is unregistered.
     *
     * @param applicationBundle The instance of application.
     */
    @Unbind
    private void unbindApplicationBundle(final IApplicationBundle applicationBundle) {
        String applicationURL = applicationBundle.getApplication().getApplicationUrl();

        // Unregister Kerneos resources
        logger.info("Unregister \"" + applicationBundle.getId() + "\" resources: " + applicationURL);

        httpService.unregister(applicationURL);
        httpService.unregister(applicationURL + "/" + KerneosConstants.KERNEOS_SWF_NAME);

        // UnRegister Kerneos Module resources for this application
        for (IModuleBundle moduleBundle : moduleBundleMap.values()) {
            unregisterApplicationModule(applicationBundle, moduleBundle);
        }

        synchronized (applicationBundleMap) {
            applicationBundleMap.remove(applicationBundle.getId());
        }
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

        // Register Kerneos Module resources for the applications
        for (IApplicationBundle applicationBundle : applicationBundleMap.values()) {
            registerApplicationModule(applicationBundle, moduleBundle);
        }
    }

    /**
     * Called when an Module Instance is unregistered.
     *
     * @param moduleBundle The instance of module.
     */
    @Unbind
    private void unbindModuleBundle(final IModuleBundle moduleBundle) {
        // UnRegister Kerneos Module resources for the applications
        for (IApplicationBundle applicationBundle : applicationBundleMap.values()) {
            unregisterApplicationModule(applicationBundle, moduleBundle);
        }

        synchronized (moduleBundleMap) {
            moduleBundleMap.remove(moduleBundle.getId());
        }
    }


    /**
     * Add a module to an application.
     *
     * @param applicationBundle The application.
     * @param moduleBundle      The module to add.
     */
    public void registerApplicationModule(IApplicationBundle applicationBundle, IModuleBundle moduleBundle) throws NamespaceException {
        httpService.registerResources(
                applicationBundle.getApplication().getApplicationUrl() + "/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleBundle.getId(),
                moduleBundle.getBundle().getResource(KerneosConstants.KERNEOS_PATH).toString(), this);
    }

    /**
     * Remove a module from an application.
     *
     * @param applicationBundle The application.
     * @param moduleBundle      The module to remove.
     */
    public void unregisterApplicationModule(IApplicationBundle applicationBundle, IModuleBundle moduleBundle) {
        httpService.unregister(
                applicationBundle.getApplication().getApplicationUrl() + "/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleBundle.getId());
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
        if (name.startsWith(PREFIX)) {
            try {
                // Fix Jetty bug
                if (!name.startsWith(PREFIX2)) {
                    return new URL("bundle://" + name.substring(PREFIX.length()));
                } else {
                    return new URL(name);
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
        
        kerneosSecurityService.updateContext(request);

        switch (kerneosSecurityService.authorize()) {
            case NO_ERROR:
                return true;

            default:
                if (KerneosContext.getCurrentContext().getApplicationBundle().getApplication().getAuthentication() == Authentication.WWW) {
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
