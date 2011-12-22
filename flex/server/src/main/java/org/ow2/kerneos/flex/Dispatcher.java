package org.ow2.kerneos.flex;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.ow2.kerneos.common.KerneosConstants;
import org.ow2.kerneos.common.config.ApplicationEvent;
import org.ow2.kerneos.common.config.ModuleEvent;
import org.ow2.kerneos.common.service.KerneosApplication;
import org.ow2.kerneos.profile.config.generated.Profile;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


@Component
@Instantiate
public class Dispatcher {
    /**
     * The logger.
     */
    private final static Log LOGGER = LogFactory.getLog(Dispatcher.class);

    @Requires(policy = "static")
    private EventAdmin eventAdmin;

    private Map<KerneosApplication, ProfileEventHandler> profileEventHandlerMap = new HashMap<KerneosApplication, ProfileEventHandler>();
    private ApplicationEventHandler applicationEventHandler;
    private ModuleEventHandler moduleEventHandler;
    private BundleContext bundleContext;

    Dispatcher(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Validate
    private synchronized void start() {
        LOGGER.debug("Start");
        applicationEventHandler = new ApplicationEventHandler(bundleContext);
        moduleEventHandler = new ModuleEventHandler(bundleContext);
    }

    @Invalidate
    private synchronized void stop() {
        LOGGER.debug("Stop");
        applicationEventHandler.dispose();
        moduleEventHandler.dispose();

        while (profileEventHandlerMap.size() != 0) {
            KerneosApplication application = profileEventHandlerMap.keySet().iterator().next();
            ProfileEventHandler profileEventHandler = profileEventHandlerMap.remove(application);
            profileEventHandler.dispose();
        }

        this.bundleContext = null;
    }

    @Bind(aggregate = true, optional = true)
    private synchronized void bindKerneosApplication(KerneosApplication application) {
        profileEventHandlerMap.put(application, new ProfileEventHandler(bundleContext, application.getId()));
    }

    @Unbind
    private synchronized void unbindKerneosApplication(KerneosApplication application) {
        profileEventHandlerMap.remove(application);
    }

    class ApplicationEventHandler implements EventHandler {
        private ServiceRegistration registration;

        ApplicationEventHandler(BundleContext bundleContext) {
            Dictionary props = new Hashtable();
            props.put(EventConstants.EVENT_TOPIC, new String[]{KerneosConstants.KERNEOS_APPLICATIONS_TOPIC});
            registration = bundleContext.registerService(EventHandler.class.getName(), this, props);
        }

        public void handleEvent(Event event) {
            ApplicationEvent applicationEvent = (ApplicationEvent) event.getProperty(KerneosConstants.KERNEOS_TOPIC_DATA);
        }

        public void dispose() {
            registration.unregister();
        }
    }

    class ModuleEventHandler implements EventHandler {
        private ServiceRegistration registration;

        ModuleEventHandler(BundleContext bundleContext) {
            Dictionary props = new Hashtable();
            props.put(EventConstants.EVENT_TOPIC, new String[]{KerneosConstants.KERNEOS_MODULES_TOPIC});
            registration = bundleContext.registerService(EventHandler.class.getName(), this, props);
        }

        public void handleEvent(Event event) {
            ModuleEvent moduleEvent = (ModuleEvent) event.getProperty(KerneosConstants.KERNEOS_TOPIC_DATA);
            // Post an event
            Dictionary<String, Object> properties = new Hashtable<String, Object>();
            properties.put(EAConstants.DATA, moduleEvent);
            Event newEvent = new Event(KerneosConstants.KERNEOS_MODULES_TOPIC + FlexConstants.KERNEOS_FLEX_TOPIC_SUFFIX, properties);

            eventAdmin.sendEvent(newEvent);
        }

        public void dispose() {
            registration.unregister();
        }
    }

    class ProfileEventHandler implements EventHandler {
        private ServiceRegistration registration;
        private String applicationId;

        ProfileEventHandler(BundleContext bundleContext, String applicationId) {
            this.applicationId = applicationId;
            Dictionary props = new Hashtable();
            props.put(EventConstants.EVENT_TOPIC, new String[]{KerneosConstants.KERNEOS_APPLICATION_TOPIC + "/" +
                    applicationId +
                    KerneosConstants.KERNEOS_PROFILE_SUFFIX});
            registration = bundleContext.registerService(EventHandler.class.getName(), this, props);
        }

        public void handleEvent(Event event) {
            Profile profile = (Profile) event.getProperty(KerneosConstants.KERNEOS_TOPIC_DATA);

            // Post an event
            Dictionary<String, Object> properties = new Hashtable<String, Object>();
            properties.put(EAConstants.DATA, profile);
            Event newEvent = new Event(KerneosConstants.KERNEOS_APPLICATION_TOPIC + "/" +
                    applicationId +
                    KerneosConstants.KERNEOS_PROFILE_SUFFIX +
                    FlexConstants.KERNEOS_FLEX_TOPIC_SUFFIX, properties);

            eventAdmin.sendEvent(newEvent);
        }

        public void dispose() {
            registration.unregister();
        }
    }
}
