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
    public final static String ID = "ID";
    public final static String BUNDLE = "BUNDLE";
    public final static String CONFIGURATION = "CONFIGURATION";
    public final static String LOGIN = "LOGIN";
    public final static String ROLES = "ROLES";
    public final static String PROFILE = "PROFILE";

    @Property(name = ID)
    @ServiceProperty(name = KerneosApplication.ID)
    private String id;

    @Property(name = CONFIGURATION)
    private Application configuration;

    @Property(name = BUNDLE)
    @ServiceProperty(name = KerneosApplication.BUNDLE)
    private long bundle;

    @Requires(id = LOGIN)
    private KerneosLogin kerneosLogin;

    @Requires(id = ROLES)
    private KerneosRoles kerneosRoles;

    @Requires(id = PROFILE)
    private KerneosProfile kerneosProfile;

    private BundleContext bundleContext;

    ApplicationImpl(BundleContext bundleContext) {
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
