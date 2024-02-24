package de.janbnz.url.rest;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.janbnz.url.auth.AuthenticationProvider;
import de.janbnz.url.auth.user.Role;
import de.janbnz.url.database.SqlDatabase;
import de.janbnz.url.generator.AlphaNumericCodeGenerator;
import de.janbnz.url.generator.ShortCodeGenerator;
import de.janbnz.url.rest.endpoint.AddressAPI;
import de.janbnz.url.rest.endpoint.AuthAPI;
import de.janbnz.url.service.ShorteningService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

public class RestServer {

    private final ShorteningService service;
    private final AuthenticationProvider authProvider;

    private final AuthAPI authAPI;
    private final AddressAPI addressAPI;

    /**
     * Creates a new RestServer instance and starts the server.
     */
    public RestServer(int port, SqlDatabase database, AuthenticationProvider authProvider) {
        this.authProvider = authProvider;

        final ShortCodeGenerator codeGenerator = new ShortCodeGenerator(new AlphaNumericCodeGenerator());
        this.service = new ShorteningService(database, codeGenerator::generateShortCode);

        final Javalin app = Javalin.create();
        app.start(port);

        // Authentication
        this.authAPI = new AuthAPI(authProvider);
        app.post("/api/register", this.authAPI.registerEndpoint(), Role.ANYONE);
        app.post("/api/login", this.authAPI.loginEndpoint(), Role.ANYONE);

        // URLs
        this.addressAPI = new AddressAPI(this.service);
        app.post("/api/create", this.addressAPI.createEndpoint(), Role.LOGGED_IN, Role.ADMIN);
        app.get("/api/stats/{url}", this.addressAPI.statsEndpoint(), Role.LOGGED_IN, Role.ADMIN);
        app.get("/{url}", this.addressAPI.getEndpoint(), Role.ANYONE);

        // Require authentication at some endpoints
        app.beforeMatched(ctx -> {
            final Role userRole = getRole(ctx);
            if (!ctx.routeRoles().isEmpty() && !ctx.routeRoles().contains(userRole)) throw new UnauthorizedResponse();
        });
    }

    /**
     * Returns the role
     *
     * @param context The HTTP request context
     * @return The user role
     */
    private Role getRole(Context context) {
        final DecodedJWT jwt = this.authProvider.getAuthentication().validate(context);
        return this.getRole(jwt);
    }

    /**
     * Returns the role
     *
     * @param jwt The decoded jwt key
     * @return The user role
     */
    public Role getRole(DecodedJWT jwt) {
        if (jwt == null) return Role.ANYONE;

        final Claim claim = jwt.getClaim("role");
        if (claim == null) return Role.ANYONE;
        return Role.valueOf(claim.asString());
    }
}