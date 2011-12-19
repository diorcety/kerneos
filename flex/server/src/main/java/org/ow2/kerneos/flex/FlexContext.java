package org.ow2.kerneos.flex;

import org.ow2.kerneos.common.service.KerneosApplication;
import org.ow2.kerneos.common.service.KerneosModule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FlexContext {

    private static ThreadLocal<FlexContext> kerneosSessionThreadLocal = new ThreadLocal<FlexContext>() {
        @Override
        protected FlexContext initialValue() {
            return (null);
        }
    };

    /**
     * Get the current Kerneos session.
     *
     * @return the current Kerneos session.
     */
    public static FlexContext getCurrent() {
        FlexContext context = kerneosSessionThreadLocal.get();
        if (context == null) {
            context = new FlexContext();
            kerneosSessionThreadLocal.set(context);
        }
        return context;
    }


    // Request
    private HttpServletRequest request;
    private HttpServletResponse response;

    private KerneosApplication application;
    private KerneosModule module;
    private String service;
    private String method;

    private String path;

    private FlexContext() {

    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public KerneosApplication getApplication() {
        return application;
    }

    public void setApplication(KerneosApplication application) {
        this.application = application;
    }

    public KerneosModule getModule() {
        return module;
    }

    public void setModule(KerneosModule module) {
        this.module = module;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
