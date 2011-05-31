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

package org.ow2.kerneos.service.impl;

import flex.messaging.messages.Message;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.granite.config.flex.Destination;
import org.granite.context.GraniteManager;
import org.granite.messaging.service.security.SecurityServiceException;
import org.granite.osgi.HttpGraniteContext;
import org.granite.osgi.service.GraniteSecurity;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

@Component
@Provides
public class GraniteKerneosSecurity implements GraniteSecurity {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(GraniteKerneosSecurity.class);

    @Property(name = "SERVICE")
    String service;

    @Requires
    IKerneosSecurityService kerneosSecurityService;

    @Validate
    private void start() {
        logger.debug("Start GraniteKerneosSecurity: " + service);
    }

    @Invalidate
    private void stop() {
        logger.debug("Stop GraniteKerneosSecurity: " + service);
    }

    public String getService() {
        return service;
    }

    public void login(String user, String password) throws SecurityServiceException {
        if (GraniteManager.getCurrentInstance() instanceof HttpGraniteContext) {
            // Set current HttpRequest
            HttpGraniteContext httpGraniteContext = (HttpGraniteContext) GraniteManager.getCurrentInstance();
            KerneosHttpContext.setCurrentHttpRequest(httpGraniteContext.getRequest());

            boolean logged = kerneosSecurityService.login(user, password);
            if (!logged)
                throw SecurityServiceException.newInvalidCredentialsException();
        }
    }

    public void authorize(Destination destination, Message message) throws SecurityServiceException {
        if (GraniteManager.getCurrentInstance() instanceof HttpGraniteContext) {
            // Set current HttpRequest
            HttpGraniteContext httpGraniteContext = (HttpGraniteContext) GraniteManager.getCurrentInstance();
            KerneosHttpContext.setCurrentHttpRequest(httpGraniteContext.getRequest());

            switch (kerneosSecurityService.authorize(destination, message)) {
                case SESSION_EXPIRED:
                    throw SecurityServiceException.newSessionExpiredException();
                case INVALID_CREDENTIALS:
                    throw SecurityServiceException.newInvalidCredentialsException();
                default:
            }
        }
    }

    public void logout() throws SecurityServiceException {
        if (GraniteManager.getCurrentInstance() instanceof HttpGraniteContext) {

            // Set current HttpRequest
            HttpGraniteContext httpGraniteContext = (HttpGraniteContext) GraniteManager.getCurrentInstance();
            KerneosHttpContext.setCurrentHttpRequest(httpGraniteContext.getRequest());

            boolean logged_out = kerneosSecurityService.logout();
            if (!logged_out)
                throw SecurityServiceException.newNotLoggedInException();
        }
    }
}