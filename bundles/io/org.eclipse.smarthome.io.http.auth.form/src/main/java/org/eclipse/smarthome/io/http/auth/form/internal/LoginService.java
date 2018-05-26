package org.eclipse.smarthome.io.http.auth.form.internal;

import org.eclipse.smarthome.io.http.HttpContextFactoryService;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoginService {

    private static final String LOGIN_FORM_ADDRESS = "/login/form";

    private final Logger logger = LoggerFactory.getLogger(LoginService.class);

    private HttpContextFactoryService httpContextFactoryService;
    private HttpService httpService;

    @Reference
    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    @Reference
    public void setHttpContextFactoryService(HttpContextFactoryService httpContextFactoryService) {
        this.httpContextFactoryService = httpContextFactoryService;
    }

    public void unsetHttpContextFactoryService(HttpContextFactoryService httpContextFactoryService) {
        this.httpContextFactoryService = null;
    }

    @Activate
    protected void activate(BundleContext bundleContext) {
        try {
            httpService.registerResources(LOGIN_FORM_ADDRESS, "web",
                    httpContextFactoryService.createDefaultHttpContext(bundleContext.getBundle()));
        } catch (NamespaceException e) {
            logger.warn("Couldn't register login form resources", e);
        }
    }

    @Deactivate
    protected void deactivate() {
        httpService.unregister(LOGIN_FORM_ADDRESS);
    }

}
