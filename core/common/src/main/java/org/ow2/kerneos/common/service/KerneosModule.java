package org.ow2.kerneos.common.service;

import org.osgi.framework.Bundle;
import org.ow2.kerneos.common.config.generated.Module;

public interface KerneosModule {
    public static final String ID = "ID";

    Module getConfiguration();

    String getId();

    Bundle getBundle();
}
