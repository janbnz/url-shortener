package de.janbnz.url.rest.endpoint.impl;

import de.janbnz.url.rest.endpoint.Endpoint;
import de.janbnz.url.service.ShorteningService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.InternalServerErrorResponse;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class AddressAPI extends Endpoint {

    private final ShorteningService service;

    public AddressAPI(ShorteningService service) {
        this.service = service;
    }

    /**
     * Is called when the /api/create endpoint is being requested
     */
    public Handler createEndpoint() {
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
    public Handler statsEndpoint() {
        return ctx -> ctx.future(() -> {
            final String url = ctx.pathParam("url");
            return this.getStats(url).thenAcceptAsync(result -> ctx.status(HttpStatus.OK).result(result));
        });
    }

    /**
     * Is called when the /{name} endpoint is being requested
     */
    public Handler getEndpoint() {
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
}