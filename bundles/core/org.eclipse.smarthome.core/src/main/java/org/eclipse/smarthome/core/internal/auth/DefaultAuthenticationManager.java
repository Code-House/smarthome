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
package org.eclipse.smarthome.core.internal.auth;

import org.eclipse.smarthome.core.auth.Authentication;
import org.eclipse.smarthome.core.auth.AuthenticationException;
import org.eclipse.smarthome.core.auth.AuthenticationManager;
import org.eclipse.smarthome.core.auth.Credentials;
import org.osgi.service.component.annotations.Component;

/**
 * Default implementation of authentication manager.
 *
 * @author Łukasz Dywicki - Initial contribution and API
 *
 */
@Component
public class DefaultAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(Credentials credentials) {
        throw new AuthenticationException("Not implemented");
    }

}
