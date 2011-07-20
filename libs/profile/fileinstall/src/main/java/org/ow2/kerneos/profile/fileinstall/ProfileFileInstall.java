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

package org.ow2.kerneos.profile.fileinstall;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.gravity.osgi.adapters.ea.EAConstants;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.manager.KerneosProfile;
import org.ow2.kerneos.profile.config.generated.ObjectFactory;
import org.ow2.kerneos.profile.config.generated.Profile;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

@Component
@Provides
public class ProfileFileInstall implements ArtifactInstaller, KerneosProfile {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(ProfileFileInstall.class);

    /**
     * Mandatory service property used by Kerneos core.
     */
    @Property(name = "ID", mandatory = true)
    @ServiceProperty(name = "ID")
    private String ID;

    /**
     * Property to set the file supported by this deployer.
     */
    @Property(name = "file", mandatory = true)
    private String FILE;

    /**
     * Event Admin.
     */
    @Requires
    private EventAdmin eventAdmin;

    /**
     * The JAXB context for rules packages serialization/deserialization. Must
     * be declared with all the potentially involved classes.
     */
    private JAXBContext jaxbContext;

    /**
     * Profile associated with this manager.
     */
    private Profile profile;

    /**
     * Constructor
     */
    ProfileFileInstall() throws Exception {
        jaxbContext = JAXBContext.newInstance(
                ObjectFactory.class.getPackage().getName(),
                ObjectFactory.class.getClassLoader());
    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        logger.debug("Start ProfileFileInstall(" + ID + ")");
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        logger.debug("Stop ProfileFileInstall(" + ID + ")");
    }


    public synchronized void install(File file) throws Exception {
        try {
            profile = loadProfileConfig(file);

            // Send message
            Dictionary<String, Object> properties = new Hashtable<String, Object>();
            properties.put(EAConstants.DATA, profile);
            Event event = new Event(KerneosConstants.KERNEOS_PROFILE_TOPIC + "/" + ID, properties);
            eventAdmin.sendEvent(event);

            logger.info("New Kerneos Profile(" + ID + "): " + file.getPath());
        } catch (Exception ex) {
            logger.error(ex, "Invalid Kerneos Profile file(" + ID + "): " + file.getPath());
        }
    }

    public synchronized void update(File file) throws Exception {
        try {
            profile = loadProfileConfig(file);

            // Send message
            Dictionary<String, Object> properties = new Hashtable<String, Object>();
            properties.put(EAConstants.DATA, profile);
            Event event = new Event(KerneosConstants.KERNEOS_PROFILE_TOPIC + "/" + ID, properties);
            eventAdmin.sendEvent(event);

            logger.info("Update Kerneos Profile(" + ID + "): " + file.getPath());
        } catch (Exception ex) {
            logger.error(ex, "Invalid Kerneos Profile file(" + ID + "): " + file.getPath());
        }
    }

    public synchronized void uninstall(File file) throws Exception {
        try {
            profile = null;
            logger.info("Delete Kerneos Profile(" + ID + "): " + file.getPath());
        } catch (Exception ex) {
            logger.error(ex, "Invalid Kerneos Profile file(" + ID + "): " + file.getPath());
        }
    }

    public boolean canHandle(File file) {
        return file.getName().endsWith(FILE);
    }

    /**
     * Get the Profile describe in the file.
     *
     * @param file the file to parse.
     * @return the Profile contained in the XML format in the file.
     * @throws Exception the file can be correctly parsed.
     */
    private Profile loadProfileConfig(File file) throws Exception {

        // Retrieve the Kerneos module file
        InputStream resource = new FileInputStream(file);
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

    public synchronized Profile getProfile() {
        return profile;
    }
}
