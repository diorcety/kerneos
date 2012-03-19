package org.ow2.kerneos.core;

import org.ow2.kerneos.profile.KerneosProfile;
import org.ow2.kerneos.profile.config.generated.ObjectFactory;
import org.ow2.kerneos.profile.config.generated.Profile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

class TestProfileManager implements KerneosProfile {
    private JAXBContext jaxbContext;

    private Profile profile;

    TestProfileManager(String file) throws Exception {
        jaxbContext = JAXBContext.newInstance(
                ObjectFactory.class.getPackage().getName(),
                ObjectFactory.class.getClassLoader());

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
