package org.ow2.kerneos.core;

import org.osgi.framework.Bundle;
import org.ow2.kerneos.common.config.generated.Application;
import org.ow2.kerneos.common.service.KerneosApplication;
import org.ow2.kerneos.login.KerneosLogin;
import org.ow2.kerneos.profile.KerneosProfile;
import org.ow2.kerneos.roles.KerneosRoles;

public class TestApplication implements KerneosApplication {
    private KerneosLogin login;
    private KerneosRoles roles;
    private KerneosProfile profile;
    private String id;
    private Application application = new Application();

    public TestApplication(String id) {
        this.id = id;
    }

    @Override
    public KerneosLogin getLoginManager() {
        return login;
    }

    @Override
    public KerneosRoles getRolesManager() {
        return roles;
    }

    @Override
    public KerneosProfile getProfileManager() {
        return profile;
    }

    @Override
    public Application getConfiguration() {
        return application;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Bundle getBundle() {
        return null;
    }

    public void setLoginManager(KerneosLogin login) {
        this.login = login;
    }

    public void setRolesManager(KerneosRoles roles) {
        this.roles = roles;
    }

    public void setProfileManager(KerneosProfile profile) {
        this.profile = profile;
    }
}
