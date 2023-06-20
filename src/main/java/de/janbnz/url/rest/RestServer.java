package de.janbnz.url.rest;

import de.janbnz.url.database.SqlDatabase;
import de.janbnz.url.generator.AlphaNumericCodeGenerator;
import de.janbnz.url.generator.ShortCodeGenerator;
import de.janbnz.url.service.ShorteningService;
import fi.iki.elonen.NanoHTTPD;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * The RestServer class extends NanoHTTPD to create a simple HTTP server.
 * It listens on port 8590 and handles incoming requests.
 */
public class RestServer extends NanoHTTPD {

    private final ShorteningService service;

    /**
     * Creates a new RestServer instance and starts the server.
     *
     * @throws IOException if an I/O error occurs while starting the server
     */
    public RestServer(SqlDatabase database) throws IOException {
        super(8590);

        final ShortCodeGenerator codeGenerator = new ShortCodeGenerator(new AlphaNumericCodeGenerator());
        this.service = new ShorteningService(database, codeGenerator::generateShortCode);
        this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8590/ \n");
    }

    /**
     * Handles incoming HTTP requests
     *
     * @param session the HTTP session representing the incoming request
     * @return the response to the incoming request
     */
    @Override
    public Response serve(IHTTPSession session) {
        final String uri = session.getUri();

        switch (session.getMethod()) {
            case POST -> {
                final JSONObject body = this.getBody(session);

                if (uri.equals("/api/create")) {
                    return this.createUrl(body);
                }
            }
            case GET -> {
                if (uri.startsWith("/api/stats/")) {
                    String shortenedURL = uri.substring("/api/stats/".length());
                    return this.getStats(shortenedURL);
                } else {
                    return this.redirect(uri.replaceFirst("/", ""));
                }
            }
        }

        return newFixedLengthResponse(new JSONObject("response", "Please provide a valid action").toString());
    }

    /**
     * Create a shortened url
     *
     * @param body the HTTP request body
     * @return a HTTP response
     */
    private Response createUrl(JSONObject body) {
        String originalURL = body.getString("url");

        CompletableFuture<String> shortenedURLFuture = this.service.createURL(originalURL);

        return shortenedURLFuture.thenApplyAsync(shortenedURL -> {
            String responseText = new JSONObject().put("url", shortenedURL).toString();
            return newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, responseText);
        }).join();
    }

    /**
     * Redirect to the original url
     *
     * @param uri the shortened url
     * @return a HTTP response
     */
    private Response redirect(String uri) {
        CompletableFuture<Response> futureResponse = this.service.createRedirect(uri).thenApplyAsync(originalURL -> {
            if (originalURL != null) {
                Response response = newFixedLengthResponse(Response.Status.REDIRECT, NanoHTTPD.MIME_HTML, "");
                response.addHeader("Location", originalURL);
                return response;
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, "URL not found");
            }
        });
        return futureResponse.join();
    }

    /**
     * Show stats of the shortened url
     *
     * @param shortenedURL the shortened url
     * @return stats like the amount of redirects and the original url
     */
    private Response getStats(String shortenedURL) {
        CompletableFuture<Response> futureResponse = this.service.getInformation(shortenedURL).thenApplyAsync(information -> {
            if (information == null) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, "URL not found");
            }
            String responseText = new JSONObject().put("original_url", information.getOriginalURL())
                    .put("shortened_url", shortenedURL).put("redirects", information.getRedirects()).toString();
            return newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, responseText);
        });
        return futureResponse.join();
    }

    /**
     * Returns the body of a NanoHTTPD session
     *
     * @param session the NanoHTTPD session
     * @return the body as json
     */
    private JSONObject getBody(IHTTPSession session) {
        final HashMap<String, String> map = new HashMap<>();
        try {
            session.parseBody(map);
        } catch (IOException | ResponseException e) {
            throw new RuntimeException(e);
        }
        return new JSONObject(map.get("postData"));
    }
}