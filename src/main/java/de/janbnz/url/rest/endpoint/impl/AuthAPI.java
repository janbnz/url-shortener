package de.janbnz.url.rest.endpoint.impl;

import de.janbnz.url.auth.AuthenticationProvider;
import de.janbnz.url.auth.user.Role;
import de.janbnz.url.auth.user.User;
import de.janbnz.url.rest.endpoint.Endpoint;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import io.javalin.http.OkResponse;
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
            final String username = this.getJsonString(body, "username");
            final String password = this.getJsonString(body, "password");

            ctx.future(() -> this.authProvider.isExisting(username).thenComposeAsync(existing -> {
                if (existing) throw new BadRequestResponse("A user with this name already exists");
                return this.authProvider.register(username, password, Role.USER);
            }));
        };
    }

    /**
     * Is called when the /api/auth/login endpoint is being requested
     */
    public Handler loginEndpoint() {
        return ctx -> {
            final JSONObject body = this.getBody(ctx.body());
            final String username = this.getJsonString(body, "username");
            final String password = this.getJsonString(body, "password");

            ctx.future(() -> this.authProvider.isExisting(username).thenComposeAsync(existing -> {
                if (!existing) throw new BadRequestResponse("User not found");

                return this.authProvider.login(username, password).thenComposeAsync(user -> {
                    if (user == null) throw new BadRequestResponse("Failed to log in");

                    final String token = this.authProvider.getAuthentication().generate(user);
                    final String userJson = this.gson.toJsonString(user, User.class);
                    final JSONObject response = new JSONObject().put("user", userJson).put("token", token);

                    throw new OkResponse(response.toString());
                });
            }));
        };
    }
}