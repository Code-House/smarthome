package org.eclipse.smarthome.io.http.auth.basic.internal;

import java.util.Base64;
import java.util.Optional;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.smarthome.auth.password.UsernamePasswordCredentials;
import org.eclipse.smarthome.core.auth.Credentials;
import org.eclipse.smarthome.io.http.auth.CredentialsExtractor;

public class BasicCredentialsExtractor implements CredentialsExtractor {

    @Override
    public Optional<Credentials> retrieveCredentials(HttpServletRequest request) {
        String authenticationHeader = request.getHeader("Authorization");

        if (authenticationHeader == null) {
            return null;
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
