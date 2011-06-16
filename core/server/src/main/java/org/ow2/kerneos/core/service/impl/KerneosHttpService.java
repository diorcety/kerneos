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

import org.ow2.kerneos.core.IApplicationInstance;
import org.ow2.kerneos.core.IModuleInstance;
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

    private Map<String, IApplicationInstance> applicationInstanceMap = new HashMap<String, IApplicationInstance>();
    private Map<String, IModuleInstance> moduleInstanceMap = new HashMap<String, IModuleInstance>();

    /**
     * Called when an Application Instance is registered.
     *
     * @param applicationInstance The instance of application.
     */
    @Bind(aggregate = true, optional = true)
    private void bindApplicationInstance(final IApplicationInstance applicationInstance) throws NamespaceException {
        String applicationURL = applicationInstance.getConfiguration().getApplicationUrl();

        // Register Kerneos Application resources
        httpService.registerResources(applicationURL,
                applicationInstance.getBundle().getResource(KerneosConstants.KERNEOS_PATH).toString(),
                this);
        httpService.registerResources(applicationURL + "/" + KerneosConstants.KERNEOS_SWF_NAME,
                KerneosConstants.KERNEOS_SWF_NAME, this);

        synchronized (applicationInstanceMap) {
            applicationInstanceMap.put(applicationInstance.getId(), applicationInstance);
        }


        // Register Kerneos Module resources for this application
        for (IModuleInstance moduleInstance : moduleInstanceMap.values()) {
            registerApplicationModule(applicationInstance, moduleInstance);
        }

        logger.info("Register \"" + applicationInstance.getId() + "\" resources: " + applicationURL);
    }

    /**
     * Called when an Application Instance is unregistered.
     *
     * @param applicationInstance The instance of application.
     */
    @Unbind
    private void unbindApplicationInstance(final IApplicationInstance applicationInstance) {
        String applicationURL = applicationInstance.getConfiguration().getApplicationUrl();

        // Unregister Kerneos resources
        logger.info("Unregister \"" + applicationInstance.getId() + "\" resources: " + applicationURL);

        httpService.unregister(applicationURL);
        httpService.unregister(applicationURL + "/" + KerneosConstants.KERNEOS_SWF_NAME);

        // UnRegister Kerneos Module resources for this application
        for (IModuleInstance moduleInstance : moduleInstanceMap.values()) {
            unregisterApplicationModule(applicationInstance, moduleInstance);
        }

        synchronized (applicationInstanceMap) {
            applicationInstanceMap.remove(applicationInstance.getId());
        }
    }

    /**
     * Called when an Module Instance is registered.
     *
     * @param moduleInstance The instance of module.
     * @throws NamespaceException Never thrown.
     */
    @Bind(aggregate = true, optional = true)
    private void bindModuleInstance(final IModuleInstance moduleInstance) throws NamespaceException {
        synchronized (moduleInstanceMap) {
            moduleInstanceMap.put(moduleInstance.getId(), moduleInstance);
        }

        // Register Kerneos Module resources for the applications
        for (IApplicationInstance applicationInstance : applicationInstanceMap.values()) {
            registerApplicationModule(applicationInstance, moduleInstance);
        }
    }

    /**
     * Called when an Module Instance is unregistered.
     *
     * @param moduleInstance The instance of module.
     */
    @Unbind
    private void unbindModuleInstance(final IModuleInstance moduleInstance) {
        // UnRegister Kerneos Module resources for the applications
        for (IApplicationInstance applicationInstance : applicationInstanceMap.values()) {
            unregisterApplicationModule(applicationInstance, moduleInstance);
        }

        synchronized (moduleInstanceMap) {
            moduleInstanceMap.remove(moduleInstance.getId());
        }
    }


    /**
     * Add a module to an application.
     *
     * @param applicationInstance The application.
     * @param moduleInstance      The module to add.
     */
    public void registerApplicationModule(IApplicationInstance applicationInstance, IModuleInstance moduleInstance) throws NamespaceException {
        httpService.registerResources(
                applicationInstance.getConfiguration().getApplicationUrl() + "/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleInstance.getId(),
                moduleInstance.getBundle().getResource(KerneosConstants.KERNEOS_PATH).toString(), this);
    }

    /**
     * Remove a module from an application.
     *
     * @param applicationInstance The application.
     * @param moduleInstance      The module to remove.
     */
    public void unregisterApplicationModule(IApplicationInstance applicationInstance, IModuleInstance moduleInstance) {
        httpService.unregister(
                applicationInstance.getConfiguration().getApplicationUrl() + "/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleInstance.getId());
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
        kerneosSecurityService.updateContext(request, null);

        switch (kerneosSecurityService.authorize()) {
            case NO_ERROR:
                return true;

            default:
                if (KerneosContext.getCurrentContext().getApplicationInstance().getConfiguration().getAuthentication() == Authentication.WWW) {
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
