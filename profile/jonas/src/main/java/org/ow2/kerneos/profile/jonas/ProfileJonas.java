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

package org.ow2.kerneos.profile.jonas;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import org.ow2.kerneos.common.KerneosConstants;
import org.ow2.kerneos.profile.KerneosProfile;
import org.ow2.kerneos.profile.config.generated.ObjectFactory;
import org.ow2.kerneos.profile.config.generated.Profile;
import org.ow2.util.ee.deploy.api.deployable.IDeployable;
import org.ow2.util.ee.deploy.api.deployer.DeployerException;
import org.ow2.util.ee.deploy.api.deployer.IDeployer;
import org.ow2.util.ee.deploy.api.deployer.IDeployerManager;
import org.ow2.util.ee.deploy.api.deployer.UnsupportedDeployerException;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

@Component
@Provides(specifications = {KerneosProfile.class})
public class ProfileJonas implements IDeployer, KerneosProfile {
    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog(ProfileJonas.class);

    /**
     * Mandatory service property used by Kerneos core.
     */
    @Property(name = KerneosProfile.ID, mandatory = true)
    @ServiceProperty(name = KerneosProfile.ID)
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

    private IDeployerManager deployerManager;

    private boolean started = false;

    /**
     * Constructor
     */
    ProfileJonas() throws Exception {
        jaxbContext = JAXBContext.newInstance(
                ObjectFactory.class.getPackage().getName(),
                ObjectFactory.class.getClassLoader());
    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        LOGGER.debug("Start ProfileJonas(" + ID + ")");
        if (deployerManager != null) {
            deployerManager.register(this);
        }
        started = true;
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        LOGGER.debug("Stop ProfileJonas(" + ID + ")");
        started = false;
        if (deployerManager != null) {
            deployerManager.unregister(this);
        }
    }


    /**
     * Bind method for the DeployerManager.
     *
     * @param deployerManager the deployer manager.
     */
    @Bind
    public void bindDeployerManager(final IDeployerManager deployerManager) {
        this.deployerManager = deployerManager;
        if (started) {
            deployerManager.register(this);
        }
    }

    /**
     * Unbind method for the DeployerManager.
     *
     * @param deployerManager the deployer manager.
     */
    @Unbind
    public void unbindDeployerManager(final IDeployerManager deployerManager) {
        this.deployerManager = null;
        if (started) {
            deployerManager.unregister(this);
        }
    }


    /**
     * Called when a supported file is deployed.
     */
    public void deploy(IDeployable<?> deployable) throws DeployerException, UnsupportedDeployerException {
        try {
            boolean update = profile != null;
            profile = loadProfileConfig(deployable.getArchive().getURL());

            // Send message
            Dictionary<String, Object> properties = new Hashtable<String, Object>();
            properties.put(KerneosConstants.KERNEOS_TOPIC_DATA, profile);
            Event event = new Event(KerneosConstants.KERNEOS_APPLICATION_TOPIC + "/" + ID + KerneosConstants.KERNEOS_PROFILE_SUFFIX, properties);
            eventAdmin.sendEvent(event);

            if (!update)
                LOGGER.info("New Kerneos Profile(" + ID + "): " + deployable.getShortName());
            else
                LOGGER.info("Update Kerneos Profile(" + ID + "): " + deployable.getShortName());
        } catch (Exception ex) {
            LOGGER.error(ex, "Invalid Kerneos Profile file(" + ID + "): " + deployable.getShortName());
        }
    }

    /**
     * Called when a deployed supported file cis undeployed.
     */
    public void undeploy(IDeployable<?> deployable) throws DeployerException {
        try {
            if (!new File(deployable.getArchive().getURL().getFile()).exists()) {
                profile = null;

                LOGGER.info("Delete Kerneos Profile(" + ID + "): " + deployable.getShortName());
            }
        } catch (Exception ex) {
            LOGGER.error(ex, "Invalid Kerneos Profile file(" + ID + "): " + deployable.getShortName());
        }
    }

    public boolean isDeployed(IDeployable<?> deployable) throws DeployerException {
        return profile != null;
    }

    /**
     * Check if  this deployer can support a file.
     *
     * @param deployable is the file to check.
     * @return true if the file is supported otherwise false.
     */
    public boolean supports(IDeployable<?> deployable) {
        return KerneosProfileDeployable.class.isAssignableFrom(deployable.getClass()) && deployable.getShortName().equals(FILE);
    }

    /**
     * Get the Profile describe in the file.
     *
     * @param url the url to parse.
     * @return the Profile contained in the XML format in the file.
     * @throws Exception the file can be correctly parsed.
     */
    private Profile loadProfileConfig(URL url) throws Exception {

        // Retrieve the Kerneos module file
        InputStream resource = url.openStream();
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
