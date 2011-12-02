package org.ow2.kerneos.core;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.ow2.kerneos.common.KerneosConstants;
import org.ow2.kerneos.common.config.ApplicationEvent;
import org.ow2.kerneos.common.config.ModuleEvent;
import org.ow2.kerneos.common.service.KerneosApplication;
import org.ow2.kerneos.common.service.KerneosModule;

import java.util.Dictionary;
import java.util.Hashtable;

@Component
@Instantiate
public class Dispatcher {

    @Requires
    private EventAdmin eventAdmin;

    @Bind(aggregate = true, optional = true)
    private void bindKerneosApplication(KerneosApplication application) {
        // Post an event
        ApplicationEvent me = new ApplicationEvent(application.getConfiguration(), ApplicationEvent.LOAD);
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(KerneosConstants.KERNEOS_TOPIC_DATA, me);
        Event event = new Event(KerneosConstants.KERNEOS_APPLICATIONS_TOPIC, properties);
        eventAdmin.sendEvent(event);
    }

    @Unbind
    private void unbindKerneosApplication(KerneosApplication application) {
        // Post an event
        ApplicationEvent me = new ApplicationEvent(application.getConfiguration(), ApplicationEvent.UNLOAD);
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(KerneosConstants.KERNEOS_TOPIC_DATA, me);
        Event event = new Event(KerneosConstants.KERNEOS_APPLICATIONS_TOPIC, properties);
        eventAdmin.sendEvent(event);
    }

    @Bind(aggregate = true, optional = true)
    private void bindKerneosModule(KerneosModule module) {
        // Post an event
        ModuleEvent me = new ModuleEvent(module.getConfiguration(), ModuleEvent.LOAD);
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(KerneosConstants.KERNEOS_TOPIC_DATA, me);
        Event event = new Event(KerneosConstants.KERNEOS_MODULES_TOPIC, properties);
        eventAdmin.sendEvent(event);
    }

    @Unbind
    private void unbindKerneosModule(KerneosModule module) {
        // Post an event
        ModuleEvent me = new ModuleEvent(module.getConfiguration(), ModuleEvent.UNLOAD);
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(KerneosConstants.KERNEOS_TOPIC_DATA, me);
        Event event = new Event(KerneosConstants.KERNEOS_MODULES_TOPIC, properties);
        eventAdmin.sendEvent(event);
    }
}
