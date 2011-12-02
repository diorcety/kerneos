package org.ow2.kerneos.core; /**
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

import junit.framework.TestCase;

import org.ow2.kerneos.common.config.generated.Authentication;
import org.ow2.kerneos.common.service.KerneosSecurityService;
import org.ow2.kerneos.profile.KerneosProfile;
import org.ow2.kerneos.login.KerneosSession;
import org.ow2.kerneos.profile.config.generated.ProfilePolicy;

import javax.xml.bind.JAXBContext;
import java.util.Collection;
import java.util.LinkedList;

public class SecurityTest extends TestCase {

    private JAXBContext jaxbContext;

    public SecurityTest() {
    }

    /**
     * Test the default policy management.
     * INVALID_CREDENTIALS: Not set roles && Denied roles.
     * NO_ERROR: Allowed roles.
     */
    public void testDefaultPolicy() throws Exception {
        TestApplication application = new TestApplication("test");
        KerneosProfile profile = new TestProfileManager("rules1.xml");
        SecurityService securityService = new SecurityService();

        assertNotNull("Invalid Profile", profile.getProfile());

        // Init profile
        application.setProfileManager(profile);

        // Init session
        KerneosSession session = new KerneosSession();
        Collection<String> roles = new LinkedList<String>();
        roles.add("test");
        session.setUsername("Test");
        session.setRoles(roles);
        KerneosSession.setCurrent(session);

        // Init context
        application.getConfiguration().setAuthentication(Authentication.INHERIT);

        TestModule module = new TestModule("test");

        profile.getProfile().setDefaultPolicy(ProfilePolicy.ALLOW);

        assertEquals("Default allow policy (unset)", securityService.isAuthorized(application, module, null, null), KerneosSecurityService.SecurityError.NO_ERROR);

        profile.getProfile().setDefaultPolicy(ProfilePolicy.DENY);

        assertEquals("Default deny policy (unset)", securityService.isAuthorized(application, module, null, null), KerneosSecurityService.SecurityError.ACCESS_DENIED);

        roles.clear();
        roles.add("jonas");
        assertEquals("Default allow rule", securityService.isAuthorized(application, module, null, null), KerneosSecurityService.SecurityError.NO_ERROR);

        roles.clear();
        roles.add("principal1");
        assertEquals("Default deny rule", securityService.isAuthorized(application, module, null, null), KerneosSecurityService.SecurityError.ACCESS_DENIED);
    }

    /**
     * Multiple matching rules management.
     * ALLOW override the other values (multiple rules or multiple roles).
     */
    public void testMultipleRule() throws Exception {
        TestApplication application = new TestApplication("test");
        KerneosProfile profile = new TestProfileManager("rules2.xml");
        SecurityService securityService = new SecurityService();

        assertNotNull("Invalid Profile", profile.getProfile());

        // Init profile
        application.setProfileManager(profile);

        // Init session
        KerneosSession session = new KerneosSession();
        Collection<String> roles = new LinkedList<String>();
        roles.add("jonas");
        session.setUsername("Test");
        session.setRoles(roles);
        KerneosSession.setCurrent(session);

        // Init context
        application.getConfiguration().setAuthentication(Authentication.INHERIT);

        TestModule module = new TestModule("test");

        assertEquals("Multiple rules", securityService.isAuthorized(application, module, null, null), KerneosSecurityService.SecurityError.NO_ERROR);

        roles.clear();
        roles.add("principal1");
        roles.add("principal2");
        assertEquals("Multiple roles", securityService.isAuthorized(application, module, null, null), KerneosSecurityService.SecurityError.NO_ERROR);
    }

    /**
     * Override rules management.
     * ALLOW can override a DENY policy.
     * DENY can override a ALLOW policy.
     */
    public void testOverrideRule() throws Exception {
        TestApplication application = new TestApplication("test");
        KerneosProfile profile = new TestProfileManager("rules3.xml");
        SecurityService securityService = new SecurityService();

        assertNotNull("Invalid Profile", profile.getProfile());

        // Init profile
        application.setProfileManager(profile);

        // Init session
        KerneosSession session = new KerneosSession();
        Collection<String> roles = new LinkedList<String>();
        roles.add("principal1");
        session.setUsername("Test");
        session.setRoles(roles);
        KerneosSession.setCurrent(session);

        // Init context
        application.getConfiguration().setAuthentication(Authentication.INHERIT);

        TestModule module = new TestModule("test1");

        assertEquals("Override allow rule", securityService.isAuthorized(application, module, null, null), KerneosSecurityService.SecurityError.NO_ERROR);

        String service = "test1";

        assertEquals("Propagation of parent rule", securityService.isAuthorized(application, module, service, null), KerneosSecurityService.SecurityError.NO_ERROR);

        service = "test2";
        assertEquals("Override deny rule", securityService.isAuthorized(application, module, service, null), KerneosSecurityService.SecurityError.ACCESS_DENIED);

        String method = "test3";
        assertEquals("Override allow in deny rule", securityService.isAuthorized(application, module, service, method), KerneosSecurityService.SecurityError.NO_ERROR);
    }

}
