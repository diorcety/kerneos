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

package org.ow2.kerneos.profile.file;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.ow2.kerneos.profile.KerneosProfile;
import org.ow2.kerneos.profile.config.generated.ObjectFactory;
import org.ow2.kerneos.profile.config.generated.Profile;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
@Provides
public class ProfileFile implements KerneosProfile {
    /**
     * The logger.
     */
    private static final Log LOGGER = LogFactory.getLog(ProfileFile.class);

    /**
     * Mandatory service property used by Kerneos core.
     */
    @Property(name = KerneosProfile.ID, mandatory = true)
    @ServiceProperty(name = KerneosProfile.ID)
    private String id;

    /**
     * Property to set the file supported by this deployer.
     */
    @Property(name = "bundle", mandatory = false)
    private String bundle;

    @Property(name = "file", mandatory = true)
    private String file;

    private BundleContext bundleContext;

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
    ProfileFile(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;

        jaxbContext = JAXBContext.newInstance(
                ObjectFactory.class.getPackage().getName(),
                ObjectFactory.class.getClassLoader());
    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        LOGGER.debug("Start ProfileFileInstall(" + id + ")");
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        LOGGER.debug("Stop ProfileFileInstall(" + id + ")");
    }

    /**
     * Get the Profile describe in the file.
     *
     * @param resource the resource to parse.
     * @return the Profile contained in the XML format in the file.
     * @throws Exception the file can be correctly parsed.
     */
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

    public synchronized Profile getProfile() {
        if (profile == null) {
            try {
                if (bundle != null) {
                    for (Bundle bundle : bundleContext.getBundles()) {
                        if (bundle.getSymbolicName().equals(this.bundle)) {
                            URL url = bundle.getResource(file);
                            if (url != null) {
                                profile = loadProfileConfig(url.openStream());
                                return profile;
                            } else {
                                LOGGER.error("File " + this.file + " not found in Bundle " + this.bundle);
                                return profile;
                            }
                        }
                    }
                    LOGGER.error("Bundle " + this.bundle + " not found");
                } else {
                    URL url = new URL(file);
                    loadProfileConfig(url.openStream());
                }
            } catch (Exception e) {
                LOGGER.error("Can't load the profile file", e);
            }
        }
        return profile;

    }
}
