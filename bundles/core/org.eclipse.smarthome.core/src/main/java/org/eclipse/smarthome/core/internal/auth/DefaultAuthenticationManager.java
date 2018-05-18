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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.smarthome.core.auth.Authentication;
import org.eclipse.smarthome.core.auth.AuthenticationException;
import org.eclipse.smarthome.core.auth.AuthenticationManager;
import org.eclipse.smarthome.core.auth.AuthenticationProvider;
import org.eclipse.smarthome.core.auth.Credentials;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of authentication manager.
 *
 * @author Łukasz Dywicki - Initial contribution and API
 *
 */
@Component
public class DefaultAuthenticationManager implements AuthenticationManager {

    private final Logger logger = LoggerFactory.getLogger(DefaultAuthenticationManager.class);

    private final List<AuthenticationProvider> providers = new CopyOnWriteArrayList<>();

    @Override
    public Authentication authenticate(Credentials credentials) {
        for (AuthenticationProvider provider : providers) {
            if (provider.supports(credentials.getClass())) {
                try {
                    Authentication authentication = provider.authenticate(credentials);
                    if (authentication != null) {
                        return authentication;
                    }
                } catch (AuthenticationException e) {
                    logger.info("Faiiled to authenticate credentials {} with provider {}", credentials, provider, e);
                }
            }
        }

        throw new AuthenticationException("Could not authenticate credentials " + credentials);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE)
    public void addAuthenticationProvider(AuthenticationProvider provider) {
        providers.add(provider);
    }

    public void removeAuthenticationProvider(AuthenticationProvider provider) {
        providers.remove(provider);
    }
}
