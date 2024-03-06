package de.janbnz.url.rest.endpoint.impl;

import de.janbnz.url.auth.AuthenticationProvider;
import de.janbnz.url.rest.endpoint.Endpoint;
import io.javalin.http.Handler;

public class AuthAPI extends Endpoint {

    private final AuthenticationProvider authProvider;

    public AuthAPI(AuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    public Handler registerEndpoint() {
        return ctx -> {
        };
    }

    public Handler loginEndpoint() {
        return ctx -> {
        };
    }
}