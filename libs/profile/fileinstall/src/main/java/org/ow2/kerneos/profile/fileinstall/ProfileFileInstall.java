package org.ow2.kerneos.profile.fileinstall;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.handlers.event.publisher.Publisher;

import org.granite.osgi.GraniteClassRegistry;

import org.osgi.service.cm.ConfigurationAdmin;

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

@Component
@Provides
public class ProfileFileInstall implements ArtifactInstaller, KerneosProfile {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(ProfileFileInstall.class);

    @Property(name = "ID", mandatory = true)
    @ServiceProperty(name = "ID")
    private String ID;

    @Property(name = "file", mandatory = true)
    private String FILE;

    /**
     * Async Event Publisher.
     */
    @org.apache.felix.ipojo.handlers.event.Publisher(
            name = "KerneosConfigurationService",
            topics = KerneosConstants.KERNEOS_PROFILE_TOPIC
    )
    private Publisher publisher;

    /**
     * Granite Class Registry.
     */
    @Requires
    private GraniteClassRegistry gcr;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    /**
     * The JAXB context for rules packages serialization/deserialization. Must
     * be declared with all the potentially involved classes.
     */
    private JAXBContext jaxbContext;

    ProfileFileInstall() throws Exception {
        jaxbContext = JAXBContext.newInstance(
                ObjectFactory.class.getPackage().getName(),
                ObjectFactory.class.getClassLoader());
    }

    private Profile profile;

    @Validate
    private void start() throws IOException {
        logger.debug("Start ProfileFileInstall");
    }

    @Invalidate
    private void stop() throws IOException {
        logger.debug("Stop ProfileFileInstall");
    }


    public synchronized void install(File file) throws Exception {
        try {
            profile = loadProfileConfig(file);
            logger.info("New Kerneos Profile: " + file.getPath());
        } catch (Exception ex) {

            logger.error(ex, "Invalid Kerneos Profile file: " + file.getPath());
        }
    }

    public synchronized void update(File file) throws Exception {
        try {
            profile = loadProfileConfig(file);
            logger.info("Update Kerneos Profile: " + file.getPath());
        } catch (Exception ex) {
            logger.error(ex, "Invalid Kerneos Profile file: " + file.getPath());
        }
    }

    public synchronized void uninstall(File file) throws Exception {
        try {
            profile = null;
            logger.info("Delete Kerneos Profile: " + file.getPath());
        } catch (Exception ex) {

            logger.error(ex, "Invalid Kerneos Profile file: " + file.getPath());
        }
    }

    public boolean canHandle(File file) {
        return file.getName().endsWith(FILE);
    }

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
