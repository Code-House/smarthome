package org.eclipse.smarthome.ui.login.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.smarthome.io.http.core.SmartHomeServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;;

@Component
public class LoginMethodsServlet extends SmartHomeServlet {

    private static final String LOGIN_ADDRESS = "/login";

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        InputStream template = getClass().getResourceAsStream("/index.html");

        String templateStr = IOUtils.toString(template);
        templateStr = templateStr.replace("<ul />", "<a href=\"/login/form/index.html\">Login form</a>");

        PrintWriter writer = res.getWriter();
        writer.write(templateStr);
        writer.flush();
    }

    @Override
    @Reference
    public void setHttpContext(HttpContext httpContext) {
        super.setHttpContext(httpContext);
    }

    @Override
    public void unsetHttpContext(HttpContext httpContext) {
        super.unsetHttpContext(httpContext);
    }

    @Override
    @Reference
    public void setHttpService(HttpService httpService) {
        super.setHttpService(httpService);
    }

    @Override
    public void unsetHttpService(HttpService httpService) {
        super.unsetHttpService(httpService);
    }

    @Activate
    protected void activate() {
        super.activate(LOGIN_ADDRESS);
    }

    @Deactivate
    protected void deactivate() {
        super.deactivate(LOGIN_ADDRESS);
    }
}
