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
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.osgi.ConfigurationHelper;
import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.service.GraniteDestination;

import org.ow2.kerneos.core.IApplicationInstance;
import org.ow2.kerneos.core.IModuleInstance;
import org.ow2.kerneos.core.KerneosContext;
import org.ow2.kerneos.core.KerneosSession;
import org.ow2.kerneos.core.manager.DefaultKerneosLogin;
import org.ow2.kerneos.core.manager.DefaultKerneosProfile;
import org.ow2.kerneos.core.manager.KerneosLogin;
import org.ow2.kerneos.core.manager.KerneosProfile;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * The security service used by Kerneos.
 */
@Component
@Instantiate
@Provides
public class KerneosSercurityService implements IKerneosSecurityService, GraniteDestination {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosConfigurationService.class);

    private static final String KERNEOS_SESSION_KEY = "KERNEOS-SESSION";

    @Requires(optional = true, defaultimplementation = DefaultKerneosLogin.class)
    private KerneosLogin kerneosLogin;

    @Requires(optional = true, defaultimplementation = DefaultKerneosProfile.class)
    private KerneosProfile kerneosProfile;

    @Requires
    private ConfigurationHelper confHelper;

    @Requires
    private GraniteClassRegistry gcr;

    @Requires(optional = true, specification = "org.ow2.kerneos.core.IApplicationInstance")
    private Collection<IApplicationInstance> applicationInstances;

    @Requires(optional = true, specification = "org.ow2.kerneos.core.IModuleInstance")
    private Collection<IModuleInstance> moduleInstances;

    private ComponentInstance graniteDestination;

    @Validate
    private void start() {
        logger.debug("Start KerneosSecurityService");

        gcr.registerClasses(KerneosConstants.KERNEOS_SERVICE_SECURITY, new Class[]{KerneosSession.class});

        // Register the synchronous service used with KerneosSecurityService
        graniteDestination = confHelper.newGraniteDestination(KerneosConstants.KERNEOS_SERVICE_SECURITY, KerneosConstants.GRANITE_SERVICE);

    }

    @Invalidate
    private void stop() {
        logger.debug("Stop KerneosSecurityService");

        graniteDestination.dispose();

        gcr.unregisterClasses(KerneosConstants.KERNEOS_SERVICE_SECURITY);
    }

    /**
     * Update context following the request
     */
    public void updateContext() {
        HttpServletRequest request = KerneosHttpService.getCurrentHttpRequest();
        IApplicationInstance currentApplicationInstance = null;
        IModuleInstance currentModuleInstance = null;
        for (IApplicationInstance applicationInstance : applicationInstances) {
            if (request.getRequestURI().startsWith(applicationInstance.getConfiguration().getApplicationUrl())) {
                currentApplicationInstance = applicationInstance;
                break;
            }
        }

        if (currentApplicationInstance == null)
            throw new RuntimeException("Invalid Kerneos Application");


        for (IModuleInstance moduleInstance : moduleInstances) {
            if (request.getRequestURI().startsWith(currentApplicationInstance.getConfiguration().getApplicationUrl() + "/" + KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + moduleInstance.getId())) {
                currentModuleInstance = moduleInstance;
                break;
            }
        }

        KerneosSession kerneosSession;
        Object obj = request.getSession().getAttribute(KERNEOS_SESSION_KEY);
        if (obj == null || !(obj instanceof KerneosSession)) {
            kerneosSession = kerneosLogin.newSession();
            request.getSession().setAttribute(KERNEOS_SESSION_KEY, kerneosSession);
        } else {
            kerneosSession = (KerneosSession) obj;
        }

        KerneosContext.setCurrentContext(new KerneosContext(request, kerneosSession, currentApplicationInstance, currentModuleInstance));
    }

    /**
     * Get the user' session.
     *
     * @return KerneosSession.
     */
    public KerneosSession getSession() {
        return new KerneosSession(KerneosContext.getCurrentContext().getSession());
    }

    /**
     * Login to Kerneos.
     *
     * @param username The username used for login.
     * @param password The password used for login.
     * @return True if the login is successful.
     */
    public boolean logIn(String username, String password) {
        IApplicationInstance applicationInstance = KerneosContext.getCurrentContext().getApplicationInstance();
        switch (applicationInstance.getConfiguration().getAuthentication()) {
            case NONE:
                break;

            default:
                kerneosLogin.login(applicationInstance.getId(), username, password);
                return KerneosContext.getCurrentContext().getSession().isLogged();
        }
        return true;
    }

    /**
     * Check the authorisation associated to the request.
     *
     * @param destination The destination(service) requested.
     * @return The status associated to the authorisation.
     */
    public SecurityError authorize(String destination) {
        IApplicationInstance applicationInstance = KerneosContext.getCurrentContext().getApplicationInstance();
        switch (applicationInstance.getConfiguration().getAuthentication()) {
            case NONE:
                break;

            case FLEX:
                if (!KerneosContext.getCurrentContext().getSession().isLogged()) {
                    for (String service : KerneosConstants.KERNEOS_SERVICES) {
                        if (service.equalsIgnoreCase(destination))
                            return SecurityError.NO_ERROR;
                    }
                }

            default:
                if (!KerneosContext.getCurrentContext().getSession().isLogged()) {
                    return SecurityError.SESSION_EXPIRED;
                }
                break;
        }

        String module = (KerneosContext.getCurrentContext().getModuleInstance() != null) ? KerneosContext.getCurrentContext().getModuleInstance().getId() : null;
        if (kerneosProfile.haveAccess(KerneosContext.getCurrentContext().getApplicationInstance().getId(), module, destination)) {
            return SecurityError.NO_ERROR;
        } else {
            return SecurityError.INVALID_CREDENTIALS;
        }
    }

    /**
     * Logout from Kerneos.
     *
     * @return True if the logout is successful.
     */
    public boolean logOut() {
        IApplicationInstance applicationInstance = KerneosContext.getCurrentContext().getApplicationInstance();
        switch (applicationInstance.getConfiguration().getAuthentication()) {
            case NONE:
                break;

            default:
                kerneosLogin.logout();
                return !KerneosContext.getCurrentContext().getSession().isLogged();
        }
        return true;
    }

    public String getId() {
        return KerneosConstants.KERNEOS_SERVICE_SECURITY;
    }

}
