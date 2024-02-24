package de.janbnz.url.rest;

import de.janbnz.url.auth.AuthenticationProvider;
import de.janbnz.url.database.SqlDatabase;
import de.janbnz.url.generator.AlphaNumericCodeGenerator;
import de.janbnz.url.generator.ShortCodeGenerator;
import de.janbnz.url.service.ShorteningService;
import io.javalin.Javalin;
import io.javalin.http.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class RestServer {

    private final ShorteningService service;

    /**
     * Creates a new RestServer instance and starts the server.
     */
    public RestServer(int port, SqlDatabase database, AuthenticationProvider authProvider) {
        final ShortCodeGenerator codeGenerator = new ShortCodeGenerator(new AlphaNumericCodeGenerator());
        this.service = new ShorteningService(database, codeGenerator::generateShortCode);

        final Javalin app = Javalin.create();
        app.start(port);

        app.post("/api/create", this.createEndpoint());
        app.get("/api/stats/{url}", this.statsEndpoint());
        app.get("/{url}", this.getEndpoint());
    }

    /**
     * Is called when the /api/create endpoint is being requested
     */
    private Handler createEndpoint() {
        return ctx -> ctx.future(() -> {
            final JSONObject body = this.getBody(ctx.body());
            final String url = this.getJsonString(body, "url");

            return createUrl(url).thenAcceptAsync(shortenedURL -> {
                final JSONObject result = new JSONObject().put("url", shortenedURL);
                ctx.status(HttpStatus.OK).result(result.toString());
            });
        });
    }

    /**
     * Is called when the /api/stats/{url} endpoint is being requested
     */
    private Handler statsEndpoint() {
        return ctx -> ctx.future(() -> {
            final String url = ctx.pathParam("url");
            return this.getStats(url).thenAcceptAsync(result -> ctx.status(HttpStatus.OK).result(result));
        });
    }

    /**
     * Is called when the /{name} endpoint is being requested
     */
    private Handler getEndpoint() {
        return ctx -> ctx.future(() -> {
            final String url = ctx.pathParam("url");
            return this.redirect(url).thenAcceptAsync(ctx::redirect);
        });
    }

    /**
     * Create a shortened url
     *
     * @param originalURL the original url
     * @return a HTTP response
     */
    private CompletableFuture<String> createUrl(String originalURL) {
        return this.service.createURL(originalURL).thenApplyAsync(shortenedURL -> {
            if (shortenedURL == null) throw new InternalServerErrorResponse("Error while shorting url");
            return shortenedURL;
        });
    }

    /**
     * Show stats of the shortened url
     *
     * @param shortenedURL the shortened url
     * @return stats like the amount of redirects and the original url
     */
    private CompletableFuture<String> getStats(String shortenedURL) {
        return this.service.getInformation(shortenedURL).thenApplyAsync(information -> {
            if (information == null) throw new BadRequestResponse("URL not found");

            return new JSONObject().put("original_url", information.originalURL())
                    .put("shortened_url", shortenedURL).put("redirects", information.redirects()).toString();
        });
    }

    /**
     * Redirect to the original url
     *
     * @param uri the shortened url
     * @return a HTTP response
     */
    private CompletableFuture<String> redirect(String uri) {
        return this.service.createRedirect(uri).thenApplyAsync(originalURL -> {
            if (originalURL == null) throw new BadRequestResponse("URL not found");
            return originalURL;
        });
    }

    /**
     * Returns the body of a NanoHTTPD session
     *
     * @param body The request body
     * @return the body as json object
     */
    private JSONObject getBody(String body) {
        try {
            return new JSONObject(body);
        } catch (JSONException ex) {
            throw new NotAcceptableResponse("Body is not a json string");
        }
    }

    /**
     * Returns the value of a specific key in a json object or throws an exception if not existing
     *
     * @param json The json object
     * @param key  The key
     * @return The value
     */
    private String getJsonString(JSONObject json, String key) {
        if (!json.has(key)) throw new NotAcceptableResponse("Please specify \"" + key + "\"");
        return json.getString(key);
    }
}