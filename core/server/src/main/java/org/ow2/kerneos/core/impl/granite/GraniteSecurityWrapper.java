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

package org.ow2.kerneos.core.impl.granite;

import flex.messaging.messages.Message;
import flex.messaging.messages.RemotingMessage;
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
import org.ow2.kerneos.core.impl.IKerneosCore;
import org.ow2.kerneos.core.impl.IKerneosSecurityService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

@Component
@Provides
public class GraniteSecurityWrapper implements GraniteSecurity {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(GraniteSecurityWrapper.class);

    @Property(name = "service")
    String service;

    @Requires
    IKerneosSecurityService kerneosSecurityService;

    @Requires
    IKerneosCore kerneosCore;

    @Validate
    private void start() {
        logger.debug("Start GraniteSecurityWrapper: " + service);
    }

    @Invalidate
    private void stop() {
        logger.debug("Stop GraniteSecurityWrapper: " + service);
    }

    private GraniteSecurityWrapper() {

    }

    public String getService() {
        return service;
    }

    public void login(String user, String password) throws SecurityServiceException {
        if (GraniteManager.getCurrentInstance() instanceof HttpGraniteContext) {

            boolean logged = kerneosSecurityService.logIn(user, password);
            if (!logged)
                throw SecurityServiceException.newInvalidCredentialsException();
        }
    }

    public void authorize(Destination destination, Message message) throws SecurityServiceException {
        if (GraniteManager.getCurrentInstance() instanceof HttpGraniteContext) {

            // Update current context
            String method = (message instanceof RemotingMessage) ? ((RemotingMessage) message).getOperation() : null;
            kerneosCore.updateContext(destination.getId(), method);

            switch (kerneosSecurityService.authorize()) {
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

            boolean logged_out = kerneosSecurityService.logOut();
            if (!logged_out)
                throw SecurityServiceException.newNotLoggedInException();
        }
    }
}
