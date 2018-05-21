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
package org.eclipse.smarthome.io.http.auth.internal;

import org.eclipse.smarthome.core.auth.Authentication;

/**
 * A thread local binder.
 *
 * TODO verify if this is really needed.
 *
 * @author Łukasz Dywicki - Initial contribution and API.
 *
 */
public class AuthenticationContext {

    private final static ThreadLocal<Authentication> CONTEXT = new ThreadLocal<>();

    static void execute(Authentication authentication, Runnable runnable) {
        try {
            // this is very ugly way of propagating context down the hill
            CONTEXT.set(authentication);
            runnable.run();
        } finally {
            CONTEXT.remove();
        }
    }

}
