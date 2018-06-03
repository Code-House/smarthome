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
package org.eclipse.smarthome.io.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handler context represents a invocation chain.
 *
 * @author Łukasz Dywicki - Initial contribution and API.
 */
public interface HandlerContext {

    String ERROR_ATTRIBUTE = "handler.error";

    void execute(HttpServletRequest request, HttpServletResponse response);

    /**
     * Signal that an error occurred during handling of request and chain existed without calling all handlers.
     */
    void error(Exception error);

    /**
     * Checks if has any errors occurred while handling request.
     *
     * @return
     */
    boolean hasError();

}
