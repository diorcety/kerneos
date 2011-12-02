package org.ow2.kerneos.flex;


import org.ow2.kerneos.common.service.KerneosApplication;
import org.ow2.kerneos.common.service.KerneosModule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public interface ICore {

    public KerneosModule getModule(String moduleId);

    public Collection<KerneosModule> getModules();

    public KerneosApplication getApplication(String applicationId);

    public Collection<KerneosApplication> getApplications();

    void updateContext(HttpServletRequest request, HttpServletResponse response);

    void updateContext(String destination, String method);
}
