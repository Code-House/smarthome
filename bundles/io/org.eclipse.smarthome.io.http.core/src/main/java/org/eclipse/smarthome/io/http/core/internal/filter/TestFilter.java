package org.eclipse.smarthome.io.http.core.internal.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.osgi.service.component.annotations.Component;

@Component(properties = { Constants.FILTER_NAME + "=test", Constants.FILTER_INIT_PARAM_PREFIX + "param=value",
        Constants.FILTER_INIT_PARAM_PREFIX + "param2=value2",
        Constants.FILTER_INIT_PARAM_PREFIX + "otherParam:long=100", Constants.HTTP_WHITEBOARD_FILTER_PATTERN + "=/*" })
public class TestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println(filterConfig.getFilterName() + " " + filterConfig.getInitParameterNames() + " "
                + filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        response.getWriter().println("BEFORE");

        chain.doFilter(request, response);

        response.getWriter().println("AFTER");
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
