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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smarthome.io.http.Handler;
import org.eclipse.smarthome.io.http.HandlerContext;

/**
 * Execution chain.
 *
 * @author Łukasz Dywicki - Initial contribution and API.
 */
public class DefaultHandlerContext implements HandlerContext {

    private final List<Handler> handlers;
    private final int limit;
    private int cursor;
    private Exception error;

    public DefaultHandlerContext(List<Handler> handlers) {
        this.handlers = handlers;
        this.limit = handlers.size();
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        if (cursor < limit) {
            boolean hasError = hasError();

            Handler handler = new CatchHandler(handlers.get(cursor++));
            if (hasError) {
                handler.handleError(request, response, this);
            } else {
                handler.handle(request, response, this);
            }

            if (!hasError && hasError()) {
                // we didn't have an error and we have it now, meaning a current handler reported issue.
                // so here reset cursor to 0 and restart execution forcing handleError method execution.
                request.setAttribute(ERROR_ATTRIBUTE, error);
                cursor = 0;
                execute(request, response);
            }
        }
    }

    @Override
    public boolean hasError() {
        return error != null;
    }

    @Override
    public void error(Exception error) {
        this.error = error;
    }

}
