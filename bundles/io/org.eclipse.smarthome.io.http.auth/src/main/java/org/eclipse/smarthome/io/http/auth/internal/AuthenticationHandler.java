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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smarthome.io.http.Handler;
import org.eclipse.smarthome.io.http.HandlerPriorities;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

@Component(configurationPid = "org.eclipse.smarthome.io.http.auth")
public class AuthenticationHandler implements Handler {

    private static final String AUTHENTICATION_ENABLED = "authentication.enabled";
    private boolean enabled = false;

    @Override
    public int getPriority() {
        return HandlerPriorities.AUTHENTICATION;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        if (this.enabled) {

        }
    }

    @Modified
    void update(Map<String, Object> properties) {
        Object authenticationEnabled = properties.get(AUTHENTICATION_ENABLED);

        if (authenticationEnabled != null && authenticationEnabled instanceof String) {
            this.enabled = Boolean.valueOf((String) authenticationEnabled);
        }
    }

}
