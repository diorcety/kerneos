package org.ow2.kerneos.core;

import org.ow2.kerneos.core.config.generated.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * The Object used the context for kerneos.
 */
public class KerneosContext {
    private static ThreadLocal<KerneosContext> kerneosContextThreadLocal = new ThreadLocal<KerneosContext>() {
        @Override
        protected KerneosContext initialValue() {
            return (null);
        }
    };

    /**
     * Set the current Kerneos context.
     *
     * @param kerneosContext The context.
     */
    public static void setCurrentContext(KerneosContext kerneosContext) {
        kerneosContextThreadLocal.set(kerneosContext);
    }

    /**
     * Get the current Kerneos context.
     *
     * @return the current Kerneos context.
     */
    public static KerneosContext getCurrentContext() {
        return kerneosContextThreadLocal.get();
    }

    private HttpServletRequest httpRequest;
    private KerneosSession session;
    private IApplicationInstance applicationInstance;
    private IModuleInstance moduleInstance;
    private Service service;

    public KerneosContext(HttpServletRequest httpRequest, KerneosSession session,
                          IApplicationInstance applicationInstance, IModuleInstance moduleInstance,
                          Service service) {
        this.httpRequest = httpRequest;
        this.session = session;
        this.applicationInstance = applicationInstance;
        this.moduleInstance = moduleInstance;
        this.service = service;
    }

    public IApplicationInstance getApplicationInstance() {
        return applicationInstance;
    }

    public IModuleInstance getModuleInstance() {
        return moduleInstance;
    }

    public Service getService() {
        return service;
    }

    public KerneosSession getSession() {
        return session;
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }
}
