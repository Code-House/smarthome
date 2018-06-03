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
package org.eclipse.smarthome.io.http.internal;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smarthome.io.http.Handler;
import org.eclipse.smarthome.io.http.WrappingHttpContext;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.http.HttpContext;

/**
 * Default http context which groups all Smart Home related http elements into one logical application.
 *
 * @author Łukasz Dywicki
 */
@Component(service = { HttpContext.class, WrappingHttpContext.class })
public class SmartHomeHttpContext implements WrappingHttpContext {

    private final List<Handler> handlers = new CopyOnWriteArrayList<>();

    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DefaultHandlerContext handlerContext = new DefaultHandlerContext(new ArrayList<>(handlers));
        handlerContext.execute(request, response);

        if (handlerContext.hasError()) {
            return false;
        }

        return true;
    }

    @Override
    public URL getResource(String name) {
        return null;
    }

    @Override
    public String getMimeType(String name) {
        return null;
    }

    @Override
    public HttpContext wrap(Bundle bundle) {
        return new BundleHttpContext(this, bundle);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE)
    public void addHandler(Handler handler) {
        this.handlers.add(handler);
    }

    public void removeHandler(Handler handler) {
        this.handlers.remove(handler);
    }

}
