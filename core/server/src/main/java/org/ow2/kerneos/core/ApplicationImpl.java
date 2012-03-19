package org.ow2.kerneos.core;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.ow2.kerneos.common.config.generated.Application;
import org.ow2.kerneos.common.service.KerneosApplication;
import org.ow2.kerneos.login.KerneosLogin;
import org.ow2.kerneos.profile.KerneosProfile;
import org.ow2.kerneos.roles.KerneosRoles;

@Component
@Provides
public class ApplicationImpl implements KerneosApplication {
    public static final String ID = "ID";
    public static final String BUNDLE = "BUNDLE";
    public static final String CONFIGURATION = "CONFIGURATION";
    public static final String LOGIN = "LOGIN";
    public static final String ROLES = "ROLES";
    public static final String PROFILE = "PROFILE";

    @Property(name = ID)
    @ServiceProperty(name = KerneosApplication.ID)
    private String id;

    @Property(name = CONFIGURATION)
    private Application configuration;

    @Property(name = BUNDLE)
    private long bundle;

    @Requires(id = LOGIN)
    private KerneosLogin kerneosLogin;

    @Requires(id = ROLES)
    private KerneosRoles kerneosRoles;

    @Requires(id = PROFILE)
    private KerneosProfile kerneosProfile;

    private BundleContext bundleContext;

    /**
     * Constructor
     *
     * @param bundleContext Context of current bundle
     */
    private ApplicationImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public KerneosLogin getLoginManager() {
        return kerneosLogin;
    }

    @Override
    public KerneosRoles getRolesManager() {
        return kerneosRoles;
    }

    @Override
    public KerneosProfile getProfileManager() {
        return kerneosProfile;
    }

    @Override
    public Application getConfiguration() {
        return configuration;
    }

    @Override
    public Bundle getBundle() {
        return bundleContext.getBundle(bundle);
    }

    @Override
    public String getId() {
        return id;
    }
}
