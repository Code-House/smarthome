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
package org.eclipse.smarthome.io.http.core.internal.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * A very naive implementation of filter chain which allows to call filters.
 *
 * Main purpose of this type is to provide a bridge from servlet over filters to wrapped servlet in environments which
 * do not support native filter registration.
 *
 * @author Łukasz Dywicki
 */
public class WrappedFilterChain implements FilterChain {

    private final List<Filter> filters;

    public WrappedFilterChain(List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        for (Filter filter : filters) {
            filter.doFilter(request, response, this);
        }
    }
}