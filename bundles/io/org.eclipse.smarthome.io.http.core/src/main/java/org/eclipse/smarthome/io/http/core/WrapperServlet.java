/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.io.http.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smarthome.io.http.core.internal.filter.ServletDelegatingFilter;
import org.eclipse.smarthome.io.http.core.internal.filter.WrappedFilterChain;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * A utility servlet which additionally calls filter chain.
 *
 * @author Łukasz Dywicki
 */
class WrapperServlet extends HttpServlet {

    private final HttpServlet delegate;
    private final Bundle bundle;

    public WrapperServlet(HttpServlet delegate) {
        this.delegate = delegate;
        this.bundle = FrameworkUtil.getBundle(delegate.getClass());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Filter> calledFilters = new ArrayList<Filter>(getMatchingFilters(req));
        calledFilters.add(new ServletDelegatingFilter(delegate));
        // we don't do anything but let filters to decorate request in the chain and then execute wrapped servlet.
        new WrappedFilterChain(calledFilters).doFilter(req, resp);
    }

    private List<Filter> getMatchingFilters(HttpServletRequest req) {
        if (bundle == null) {
            return Collections.emptyList();
        }

        BundleContext bundleContext = bundle.getBundleContext();
        ServiceReference<FilterRegistry> reference = bundleContext.getServiceReference(FilterRegistry.class);
        if (reference == null) {
            return Collections.emptyList();
        }

        try {
            return bundleContext.getService(reference).getFilters(req);
        } catch (Exception e) {
            return Collections.emptyList();
        } finally {
            bundleContext.ungetService(reference);
        }

    }

}