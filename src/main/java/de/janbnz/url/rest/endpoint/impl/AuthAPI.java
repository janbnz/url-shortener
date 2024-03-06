package de.janbnz.url.rest.endpoint.impl;

import de.janbnz.url.auth.AuthenticationProvider;
import de.janbnz.url.rest.endpoint.Endpoint;
import io.javalin.http.Handler;
import org.json.JSONObject;

public class AuthAPI extends Endpoint {

    private final AuthenticationProvider authProvider;

    public AuthAPI(AuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    /**
     * Is called when the /api/auth/register endpoint is being requested
     */
    public Handler registerEndpoint() {
        return ctx -> {
            final JSONObject body = this.getBody(ctx.body());
            final String name = body.getString("");

        };
    }

    /**
     * Is called when the /api/auth/login endpoint is being requested
     */
    public Handler loginEndpoint() {
        return ctx -> {
        };
    }
}