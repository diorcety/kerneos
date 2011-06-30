package org.ow2.kerneos.profile.fileinstall;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.handlers.event.publisher.Publisher;

import org.granite.osgi.GraniteClassRegistry;

import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.core.KerneosConstants;
import org.ow2.kerneos.core.KerneosContext;
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
import java.util.HashMap;
import java.util.Map;

@Component
@Instantiate
@Provides
public class ProfileFileInstall implements ArtifactInstaller, KerneosProfile {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(ProfileFileInstall.class);

    private static String PREFIX = "kerneos-";
    private static String SUFFIX = ".xml";

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

    private Map<String, Profile> applicationsProfile = new HashMap<String, Profile>();

    @Validate
    private void start() throws IOException {
        logger.debug("Start ProfileFileInstall");
    }

    @Invalidate
    private void stop() throws IOException {
        logger.debug("Stop ProfileFileInstall");
    }

    private String getApplicationName(String fileName) {
        return fileName.substring(PREFIX.length(), fileName.length() - SUFFIX.length());
    }

    public synchronized void install(File file) throws Exception {
        String application = getApplicationName(file.getName());
        logger.info("New Kerneos Profile: " + application);

        try {
            Profile profile = loadProfileConfig(file);
            applicationsProfile.put(application, profile);
        } catch (Exception ex) {

            logger.error(ex, "Invalid Kerneos Profile file: " + file.getPath());
        }
    }

    public synchronized void update(File file) throws Exception {
        String application = getApplicationName(file.getName());
        logger.info("Update Kerneos Profile: " + application);

        try {
            Profile profile = loadProfileConfig(file);
            applicationsProfile.put(application, profile);
        } catch (Exception ex) {
            logger.error(ex, "Invalid Kerneos Profile file: " + file.getPath());
        }
    }

    public synchronized void uninstall(File file) throws Exception {
        String application = getApplicationName(file.getName());
        logger.info("Delete Kerneos Profile: " + application);

        try {
            applicationsProfile.remove(application);
        } catch (Exception ex) {

            logger.error(ex, "Invalid Kerneos Profile file: " + file.getPath());
        }
    }

    public boolean canHandle(File file) {
        return file.getName().startsWith(PREFIX) && file.getName().endsWith(SUFFIX);
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
        KerneosContext kerneosContext = KerneosContext.getCurrentContext();
        return (Profile) applicationsProfile.get(kerneosContext.getApplicationBundle().getId());
    }
}
