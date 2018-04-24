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

import org.osgi.service.http.HttpContext;

/**
 * Base class for HTTP servlets developed in Eclispe Smart Home.
 *
 * @author Łukasz Dywicki
 */
public abstract class SmartHomeServlet extends BaseSmartHomeServlet {

    /**
     * Http context.
     */
    protected HttpContext httpContext;

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public void unsetHttpContext(HttpContext httpContext) {
        this.httpContext = null;
    }

    protected void activate(String alias) {
        super.activate(alias, httpContext);
    }

}
