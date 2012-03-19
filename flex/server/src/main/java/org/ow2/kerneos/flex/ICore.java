package org.ow2.kerneos.flex;


import org.ow2.kerneos.common.service.KerneosApplication;
import org.ow2.kerneos.common.service.KerneosModule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public interface ICore {

    KerneosModule getModule(String moduleId);

    Collection<KerneosModule> getModules();

    KerneosApplication getApplication(String applicationId);

    Collection<KerneosApplication> getApplications();

    void updateContext(HttpServletRequest request, HttpServletResponse response);

    void updateContext(String destination, String method);
}
