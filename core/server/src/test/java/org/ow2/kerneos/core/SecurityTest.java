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

import org.ow2.kerneos.core.config.generated.Application;
import org.ow2.kerneos.core.config.generated.Authentication;
import org.ow2.kerneos.core.config.generated.Module;
import org.ow2.kerneos.core.config.generated.Service;
import org.ow2.kerneos.core.manager.KerneosProfile;
import org.ow2.kerneos.core.impl.IKerneosSecurityService;
import org.ow2.kerneos.core.impl.KerneosSecurityService;
import org.ow2.kerneos.login.Session;
import org.ow2.kerneos.profile.config.generated.ObjectFactory;
import org.ow2.kerneos.profile.config.generated.Profile;
import org.ow2.kerneos.profile.config.generated.ProfilePolicy;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;

public class SecurityTest extends TestCase {

    private JAXBContext jaxbContext;

    public SecurityTest() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(
                ObjectFactory.class.getPackage().getName(),
                ObjectFactory.class.getClassLoader());
    }

    /**
     * Test the default policy management.
     * INVALID_CREDENTIALS: Not set roles && Denied roles.
     * NO_ERROR: Allowed roles.
     */
    public void testDefaultPolicy() throws Exception {
        KerneosContext context = KerneosContext.getCurrentContext();
        KerneosProfile profile = new TestProfileManager("rules1.xml");
        KerneosSecurityService securityService = new KerneosSecurityService();

        assertNotNull("Invalid Profile", profile.getProfile());

        // Init profile
        context.setProfileManager(profile);

        // Init session
        Session session = new Session();
        Collection<String> roles = new LinkedList<String>();
        roles.add("test");
        session.setUsername("Test");
        session.setRoles(roles);
        context.setSession(session);

        // Init context
        ApplicationBundle application = new ApplicationBundle("test", new Application(), null);
        application.getApplication().setAuthentication(Authentication.INHERIT);
        context.setApplicationBundle(application);

        ModuleBundle module = new ModuleBundle("test", new Module(), null);
        context.setModuleBundle(module);

        profile.getProfile().setDefaultPolicy(ProfilePolicy.ALLOW);

        assertEquals("Default allow policy (unset)", securityService.authorize(), IKerneosSecurityService.SecurityError.NO_ERROR);

        profile.getProfile().setDefaultPolicy(ProfilePolicy.DENY);

        assertEquals("Default deny policy (unset)", securityService.authorize(), IKerneosSecurityService.SecurityError.ACCESS_DENIED);

        roles.clear();
        roles.add("jonas");
        assertEquals("Default allow rule", securityService.authorize(), IKerneosSecurityService.SecurityError.NO_ERROR);

        roles.clear();
        roles.add("principal1");
        assertEquals("Default deny rule", securityService.authorize(), IKerneosSecurityService.SecurityError.ACCESS_DENIED);
    }

    /**
     * Multiple matching rules management.
     * ALLOW override the other values (multiple rules or multiple roles).
     */
    public void testMultipleRule() throws Exception {
        KerneosContext context = KerneosContext.getCurrentContext();
        KerneosProfile profile = new TestProfileManager("rules2.xml");
        KerneosSecurityService securityService = new KerneosSecurityService();

        assertNotNull("Invalid Profile", profile.getProfile());

        // Init profile
        context.setProfileManager(profile);

        // Init session
        Session session = new Session();
        Collection<String> roles = new LinkedList<String>();
        roles.add("jonas");
        session.setUsername("Test");
        session.setRoles(roles);
        context.setSession(session);

        // Init context
        ApplicationBundle application = new ApplicationBundle("test", new Application(), null);
        application.getApplication().setAuthentication(Authentication.INHERIT);
        context.setApplicationBundle(application);

        ModuleBundle module = new ModuleBundle("test", new Module(), null);
        context.setModuleBundle(module);

        assertEquals("Multiple rules", securityService.authorize(), IKerneosSecurityService.SecurityError.NO_ERROR);

        roles.clear();
        roles.add("principal1");
        roles.add("principal2");
        assertEquals("Multiple roles", securityService.authorize(), IKerneosSecurityService.SecurityError.NO_ERROR);
    }

    /**
     * Override rules management.
     * ALLOW can override a DENY policy.
     * DENY can override a ALLOW policy.
     */
    public void testOverrideRule() throws Exception {
        KerneosContext context = KerneosContext.getCurrentContext();
        KerneosProfile profile = new TestProfileManager("rules3.xml");
        KerneosSecurityService securityService = new KerneosSecurityService();

        assertNotNull("Invalid Profile", profile.getProfile());

        // Init profile
        context.setProfileManager(profile);

        // Init session
        Session session = new Session();
        Collection<String> roles = new LinkedList<String>();
        roles.add("principal1");
        session.setUsername("Test");
        session.setRoles(roles);
        context.setSession(session);

        // Init context
        ApplicationBundle application = new ApplicationBundle("test", new Application(), null);
        application.getApplication().setAuthentication(Authentication.INHERIT);
        context.setApplicationBundle(application);

        ModuleBundle module = new ModuleBundle("test1", new Module(), null);
        context.setModuleBundle(module);

        assertEquals("Override allow rule", securityService.authorize(), IKerneosSecurityService.SecurityError.NO_ERROR);

        Service service = new Service();
        service.setId("test1");
        context.setService(service);

        assertEquals("Propagation of parent rule", securityService.authorize(), IKerneosSecurityService.SecurityError.NO_ERROR);

        service.setId("test2");
        assertEquals("Override deny rule", securityService.authorize(), IKerneosSecurityService.SecurityError.ACCESS_DENIED);

        context.setMethod("test3");
        assertEquals("Override allow in deny rule", securityService.authorize(), IKerneosSecurityService.SecurityError.NO_ERROR);
    }

    class TestProfileManager implements KerneosProfile {
        private Profile profile;

        TestProfileManager(String file) throws Exception {
            profile = loadProfileConfig(this.getClass().getClassLoader().getResourceAsStream(file));

        }

        private Profile loadProfileConfig(InputStream resource) throws Exception {

            // Unmarshall it
            try {
                // Create an unmarshaller
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                // Deserialize the application file
                JAXBElement element = (JAXBElement) unmarshaller.unmarshal(resource);
                return (Profile) element.getValue();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                resource.close();
            }
        }

        public Profile getProfile() {
            return profile;
        }
    }
}
