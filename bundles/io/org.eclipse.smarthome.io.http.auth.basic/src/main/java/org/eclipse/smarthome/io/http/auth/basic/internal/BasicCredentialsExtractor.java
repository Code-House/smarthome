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
package org.eclipse.smarthome.io.http.auth.basic.internal;

import java.util.Base64;
import java.util.Optional;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.smarthome.auth.password.UsernamePasswordCredentials;
import org.eclipse.smarthome.core.auth.Credentials;
import org.eclipse.smarthome.io.http.auth.CredentialsExtractor;
import org.osgi.service.component.annotations.Component;

/**
 * Extract user name and password from incoming request.
 *
 * @author Łukasz Dywicki - initial contribution.
 */
@Component(property = { "context=javax.servlet.http.HttpServletRequest" })
public class BasicCredentialsExtractor implements CredentialsExtractor<HttpServletRequest> {

    @Override
    public Optional<Credentials> retrieveCredentials(HttpServletRequest request) {
        String authenticationHeader = request.getHeader("Authorization");

        if (authenticationHeader == null) {
            return Optional.empty();
        }

        StringTokenizer tokenizer = new StringTokenizer(authenticationHeader, " ");
        String authType = tokenizer.nextToken();
        if (HttpServletRequest.BASIC_AUTH.equalsIgnoreCase(authType)) {
            String usernameAndPassword = new String(Base64.getDecoder().decode(tokenizer.nextToken()));

            tokenizer = new StringTokenizer(usernameAndPassword, ":");
            String username = tokenizer.nextToken();
            String password = tokenizer.nextToken();

            return Optional.of(new UsernamePasswordCredentials(username, password));
        }

        return Optional.empty();
    }

}
