package org.eclipse.smarthome.io.http.auth.jwt.internal;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import org.eclipse.smarthome.core.auth.Credentials;
import org.eclipse.smarthome.io.http.auth.CredentialsExtractor;

public class JWTCredetialsExtractor implements CredentialsExtractor {

    @Override
    public Optional<Credentials> retrieveCredentials(HttpServletRequest request) {
        return Optional.empty();
    }

}
