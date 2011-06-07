package org.ow2.kerneos.core.service;

import org.ow2.kerneos.core.IApplicationInstance;
import org.ow2.kerneos.core.IModuleInstance;

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
    public static void set(KerneosContext kerneosContext) {
        kerneosContextThreadLocal.set(kerneosContext);
    }

    /**
     * Get the current Kerneos context.
     *
     * @return the current Kerneos context.
     */
    public static KerneosContext get() {
        return kerneosContextThreadLocal.get();
    }

    private HttpServletRequest currentHttpRequest;
    private IApplicationInstance applicationInstance;
    private IModuleInstance moduleInstance;

    public KerneosContext(HttpServletRequest currentHttpRequest, IApplicationInstance applicationInstance, IModuleInstance moduleInstance) {
        this.currentHttpRequest = currentHttpRequest;
        this.applicationInstance = applicationInstance;
        this.moduleInstance = moduleInstance;
    }

    public IApplicationInstance getApplicationInstance() {
        return applicationInstance;
    }

    public IModuleInstance getModuleInstance() {
        return moduleInstance;
    }

    public HttpServletRequest getCurrentHttpRequest()
    {
        return currentHttpRequest;
    }
}
