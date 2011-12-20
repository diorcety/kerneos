package org.ow2.kerneos.flex;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ow2.kerneos.common.KerneosConstants;
import org.ow2.kerneos.common.config.generated.Service;
import org.ow2.kerneos.common.config.generated.SwfModule;
import org.ow2.kerneos.common.service.KerneosApplication;
import org.ow2.kerneos.common.service.KerneosModule;
import org.ow2.kerneos.login.KerneosSession;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * The core of Flex Kerneos.
 */
@Component
@Instantiate
@Provides
public class Core implements ICore {
    private static final String KERNEOS_SESSION_KEY = "KERNEOS-SESSION-";

    /**
     * The logger.
     */
    private static final Log LOGGER = LogFactory.getLog(Core.class);

    private Configuration granite, gravity;

    private Map<String, KerneosApplication> applications = new HashMap<String, KerneosApplication>();
    private Map<String, ComponentInstance> applicationHttpServices = new HashMap<String, ComponentInstance>();
    private Map<String, KerneosModule> modules = new HashMap<String, KerneosModule>();

    @Requires
    private ConfigurationAdmin configurationAdmin;

    @Requires(filter = "(factory.name=org.ow2.kerneos.flex.FlexHttpService)")
    private Factory flexHttpServiceFactory;

    /**
     * Constructor.
     * Avoid direct component instantiation.
     */
    private Core() {

    }

    /**
     * Called when all the component dependencies are met.
     *
     * @throws IOException an issue occurs during the validation
     */
    @Validate
    private synchronized void start() throws Exception {
        LOGGER.debug("Start FlexCore");

        // Gravity Configurations
        {
            Dictionary properties = new Hashtable();
            properties.put("id", FlexConstants.GRAVITY_SERVICE);
            properties.put("messageTypes", "flex.messaging.messages.AsyncMessage");
            properties.put("defaultAdapter", EAConstants.ADAPTER_ID);
            gravity = configurationAdmin.createFactoryConfiguration(org.granite.config.flex.Service.class.getName(),
                    null);
            gravity.update(properties);
        }

        // Granite Configurations
        {
            Dictionary properties = new Hashtable();
            properties.put("id", FlexConstants.GRANITE_SERVICE);
            granite = configurationAdmin.createFactoryConfiguration(org.granite.config.flex.Service.class.getName(),
                    null);
            granite.update(properties);
        }
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     *
     * @throws IOException an issue occurs during the validation
     */
    @Invalidate
    private synchronized void stop() throws IOException {
        LOGGER.debug("Stop FlexCore");

        // Dispose configurations
        gravity.delete();
        granite.delete();
    }


    /**
     * Optional Kerneos Application binding.
     * Create a new FlexHttpService for each Kerneos Application.
     *
     * @param application the new Kerneos Application
     */
    @Bind(aggregate = true, optional = true)
    synchronized void bindKerneosApplication(KerneosApplication application) {
        applications.put(application.getId(), application);
        try {
            LOGGER.debug("Create FlexHttpService for " + application.getId());
            Dictionary properties = new Hashtable();
            Dictionary filters = new Hashtable();
            filters.put(FlexHttpService.APPLICATION, "(" + KerneosApplication.ID + "=" + application.getId() + ")");
            properties.put("requires.filters", filters);
            ComponentInstance instance = flexHttpServiceFactory.createComponentInstance(properties);
            applicationHttpServices.put(application.getId(), instance);
            instance.start();
        } catch (Exception e) {
            LOGGER.error("Can't create FlexHttpService for " + application.getId());
        }
    }

    /**
     * Optional Kerneos Application binding.
     * Remove the FlexHttpService associated with the Kerneos Application.
     *
     * @param application the old Kerneos Application
     */
    @Unbind
    synchronized void unbindKerneosApplication(KerneosApplication application) {
        applications.remove(application.getId());
        ComponentInstance instance = applicationHttpServices.remove(application.getId());
        if (instance != null) {
            instance.dispose();
            LOGGER.debug("Destroy FlexHttpService for " + application.getId());
        }
    }

    /**
     * Optional Kerneos Module binding.
     *
     * @param module the new Kerneos Module
     */
    @Bind(aggregate = true, optional = true)
    synchronized void bindKerneosModule(KerneosModule module) {
        modules.put(module.getId(), module);
    }

    /**
     * Optional Kerneos Module binding.
     *
     * @param module the old Kerneos Module
     */
    @Unbind
    synchronized void unbindKerneosModule(KerneosModule module) {
        modules.remove(module.getId());
    }

    /**
     * Update the current flex context.
     *
     * @param request  the HTTP request associated with the context
     * @param response the HTTP response associated with the context
     */
    public void updateContext(HttpServletRequest request, HttpServletResponse response) {
        FlexContext flexContext = FlexContext.getCurrent();

        flexContext.setRequest(request);
        flexContext.setResponse(response);

        // Get the module and the associated path
        String path = request.getRequestURI();
        int index = path.indexOf(flexContext.getApplication().getConfiguration().getApplicationUrl());
        path = path.substring(index);

        KerneosModule currentModule = null;
        if (path != null && path.startsWith(KerneosConstants.KERNEOS_MODULE_URL)) {
            path = path.substring(KerneosConstants.KERNEOS_MODULE_URL.length());
            int sep = path.indexOf("/");
            if (sep != -1) {
                synchronized (this) {
                    currentModule = modules.get(path.substring(0, sep));
                }
                path = path.substring(sep);
            }
        }

        flexContext.setModule(currentModule);
        flexContext.setPath(path);
        flexContext.setService(null);
        flexContext.setMethod(null);

        // Get or create a session
        KerneosSession session = null;
        String sessionKey = KERNEOS_SESSION_KEY + flexContext.getApplication().getId();
        Object obj = request.getSession().getAttribute(sessionKey);
        if (obj == null || !(obj instanceof KerneosSession)) {
            session = flexContext.getApplication().getLoginManager().newSession();
            if (session.getRoles() != null) {
                session.setRoles(flexContext.getApplication().getRolesManager().resolve(session.getRoles()));
            }
            request.getSession().setAttribute(sessionKey, session);
        } else {
            session = (KerneosSession) obj;
        }
        KerneosSession.setCurrent(session);
    }

    /**
     * Update the current flex context.
     *
     * @param destination the new destination associated with the context
     * @param method      the new method associated with the context
     */
    public void updateContext(String destination, String method) {
        FlexContext flexContext = FlexContext.getCurrent();
        flexContext.setPath(null);
        flexContext.setMethod(method);
        KerneosModule module = null;
        String service = null;
        synchronized (this) {
            for (KerneosModule amodule : modules.values()) {
                if (amodule.getConfiguration() instanceof SwfModule) {
                    SwfModule configuration = (SwfModule) amodule.getConfiguration();
                    for (Service aservice : configuration.getServices()) {
                        if (aservice.getDestination() == destination) {
                            service = aservice.getId();
                            module = amodule;
                        }
                    }
                }
            }
        }
        if (module != null) {
            flexContext.setModule(module);
            flexContext.setService(service);
        }
    }

    @Override
    public KerneosModule getModule(String moduleId) {
        return modules.get(moduleId);
    }

    @Override
    public KerneosApplication getApplication(String applicationId) {
        return applications.get(applicationId);
    }

    @Override
    public Collection<KerneosModule> getModules() {
        return modules.values();
    }

    @Override
    public Collection<KerneosApplication> getApplications() {
        return applications.values();
    }

}
