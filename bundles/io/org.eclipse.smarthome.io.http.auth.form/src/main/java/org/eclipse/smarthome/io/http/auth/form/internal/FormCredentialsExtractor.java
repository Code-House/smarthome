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
package org.eclipse.smarthome.io.http.auth.form.internal;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.smarthome.auth.password.UsernamePasswordCredentials;
import org.eclipse.smarthome.core.auth.Credentials;
import org.eclipse.smarthome.io.http.auth.CredentialsExtractor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * Extract user name and password from incoming request.
 *
 * @author Łukasz Dywicki - initial contribution.
 */
@Component(configurationPid = "org.eclipse.smarthome.io.http.auth.form", property = {
        "context=javax.servlet.http.HttpServletRequest" })
public class FormCredentialsExtractor implements CredentialsExtractor<HttpServletRequest> {

    private static final String LOGIN_ENDPOINT = "/login/form/index.html?process";

    private String loginEndpoint;

    @Override
    public Optional<Credentials> retrieveCredentials(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        if (loginEndpoint.equals(requestURI) && "post".equalsIgnoreCase(request.getMethod())) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            return Optional.of(new UsernamePasswordCredentials(username, password));
        }

        return Optional.empty();
    }

    @Modified
    void update(Map<String, Object> properties) {
        Object loginUri = properties.get(LOGIN_ENDPOINT);
        if (loginUri != null && loginUri instanceof String) {
            this.loginEndpoint = (String) loginUri;
        }
    }

}
