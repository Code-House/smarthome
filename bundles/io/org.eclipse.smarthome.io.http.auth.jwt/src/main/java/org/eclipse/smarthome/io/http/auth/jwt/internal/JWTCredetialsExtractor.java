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
package org.eclipse.smarthome.io.http.auth.jwt.internal;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.smarthome.core.auth.Credentials;
import org.eclipse.smarthome.io.http.auth.CredentialsExtractor;
import org.osgi.service.component.annotations.Component;

/**
 * Extractor of JWT from incoming request.
 *
 * @author Łukasz Dywicki - initial contribution.
 */
@Component(property = { "context=javax.servlet.http.HttpServletRequest" })
public class JWTCredetialsExtractor implements CredentialsExtractor<HttpServletRequest> {

    @Override
    public Optional<Credentials> retrieveCredentials(HttpServletRequest request) {
        return Optional.empty();
    }

}
