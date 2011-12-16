package org.ow2.kerneos.common.service;

import org.osgi.framework.Bundle;
import org.ow2.kerneos.common.config.generated.Application;
import org.ow2.kerneos.login.KerneosLogin;
import org.ow2.kerneos.profile.KerneosProfile;
import org.ow2.kerneos.roles.KerneosRoles;

public interface KerneosApplication {
    public static final String ID = "ID";

    KerneosLogin getLoginManager();

    KerneosRoles getRolesManager();

    KerneosProfile getProfileManager();

    Application getConfiguration();

    String getId();

    Bundle getBundle();
}
