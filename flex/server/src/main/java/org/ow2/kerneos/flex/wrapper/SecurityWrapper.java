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

package org.ow2.kerneos.flex.wrapper;

import flex.messaging.messages.Message;
import flex.messaging.messages.RemotingMessage;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.config.flex.Destination;
import org.granite.context.GraniteManager;
import org.granite.messaging.service.security.SecurityServiceException;
import org.granite.osgi.HttpGraniteContext;
import org.granite.osgi.service.GraniteSecurity;

import org.ow2.kerneos.common.service.KerneosSecurityService;
import org.ow2.kerneos.flex.FlexConstants;
import org.ow2.kerneos.flex.FlexContext;
import org.ow2.kerneos.flex.ICore;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

@Component
@Provides
@Instantiate
public final class SecurityWrapper implements GraniteSecurity {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(SecurityWrapper.class);

    @Requires
    private KerneosSecurityService kerneosSecurityService;

    @Requires
    private ICore flexCore;

    @Validate
    private void start() {
        logger.debug("Start GraniteSecurityWrapper: " + getService());
    }

    @Invalidate
    private void stop() {
        logger.debug("Stop GraniteSecurityWrapper: " + getService());
    }

    private SecurityWrapper() {

    }

    public String getService() {
        return FlexConstants.GRANITE_SERVICE;
    }

    public void login(String user, String password) throws SecurityServiceException {
        if (GraniteManager.getCurrentInstance() instanceof HttpGraniteContext) {

            FlexContext flexContext = FlexContext.getCurrent();
            boolean logged = kerneosSecurityService.logIn(flexContext.getApplication(), user, password);
            if (!logged) {
                throw SecurityServiceException.newInvalidCredentialsException();
            }
        }
    }

    public void authorize(Destination destination, Message message) throws SecurityServiceException {
        if (GraniteManager.getCurrentInstance() instanceof HttpGraniteContext) {

            // Update current context
            String method = (message instanceof RemotingMessage) ? ((RemotingMessage) message).getOperation() : null;
            flexCore.updateContext(destination.getId(), method);

            FlexContext flexContext = FlexContext.getCurrent();
            KerneosSecurityService.SecurityError error = kerneosSecurityService.isAuthorized(
                    flexContext.getApplication(), flexContext.getModule(), flexContext.getService(), 
                    flexContext.getMethod());
            switch (error) {
                case SESSION_EXPIRED:
                    throw SecurityServiceException.newSessionExpiredException();
                case ACCESS_DENIED:
                    throw SecurityServiceException.newAccessDeniedException();
                default:
            }
        }
    }

    public void logout() throws SecurityServiceException {
        if (GraniteManager.getCurrentInstance() instanceof HttpGraniteContext) {

            FlexContext flexContext = FlexContext.getCurrent();
            boolean loggedOut = kerneosSecurityService.logOut(flexContext.getApplication());
            if (!loggedOut) {
                throw SecurityServiceException.newNotLoggedInException();
            }
        }
    }
}
