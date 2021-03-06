package org.ow2.kerneos.core;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;

import org.osgi.framework.Bundle;

import org.osgi.framework.BundleContext;
import org.ow2.kerneos.common.config.generated.Module;
import org.ow2.kerneos.common.service.KerneosModule;

@Component
@Provides
public class ModuleImpl implements KerneosModule {
    public static final String ID = "ID";
    public static final String BUNDLE = "BUNDLE";
    public static final String CONFIGURATION = "CONFIGURATION";

    @Property(name = ID)
    @ServiceProperty(name = KerneosModule.ID)
    private String id;

    @Property(name = CONFIGURATION)
    private Module configuration;

    @Property(name = BUNDLE)
    private long bundle;

    private BundleContext bundleContext;

    private ModuleImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public Module getConfiguration() {
        return configuration;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Bundle getBundle() {
        return bundleContext.getBundle(bundle);
    }
}
