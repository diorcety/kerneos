package org.ow2.kerneos.core;

import org.osgi.framework.Bundle;
import org.ow2.kerneos.common.config.generated.Module;
import org.ow2.kerneos.common.service.KerneosModule;

public class TestModule implements KerneosModule {
    private String id;
    private Module module = new Module();

    public TestModule(String id) {
        this.id = id;
    }


    @Override
    public Module getConfiguration() {
        return module;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Bundle getBundle() {
        return null;
    }
}
