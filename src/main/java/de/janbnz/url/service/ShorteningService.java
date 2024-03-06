package de.janbnz.url.service;

import de.janbnz.url.database.Database;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ShorteningService {

    private final Database database;
    private final Supplier<String> codeSupplier;

    public ShorteningService(Database database, Supplier<String> codeSupplier) {
        this.database = database;
        this.codeSupplier = codeSupplier;
    }

    /**
     * Generates a unique short code and inserts the necessary data to the sql database
     *
     * @param originalURL the original url
     * @return the shortened url
     */
    public CompletableFuture<String> createURL(String originalURL) {
        return generateUniqueShortCode().thenComposeAsync(shortenedUrl -> database.createURL(originalURL, shortenedUrl).thenApplyAsync(result -> shortenedUrl));
    }

    /**
     * Returns the original url and increments the amount of redirects in the database
     *
     * @param shortenedURL the shortened url
     * @return the original url
     */
    public CompletableFuture<String> createRedirect(String shortenedURL) {
        return this.getInformation(shortenedURL).thenComposeAsync(information -> {
            if (information == null) return CompletableFuture.completedFuture(null);

            final String originalURL = information.originalURL();
            return database.createRedirect(shortenedURL).thenApplyAsync(result -> originalURL);
        });
    }

    /**
     * Returns information about a shortened url
     *
     * @param shortenedURL The shortened url
     * @return The url object or null if not existing
     */
    public CompletableFuture<ShortenedURL> getInformation(String shortenedURL) {
        return this.database.getInformation(shortenedURL);
    }

    /**
     * Generates a unique short code which is not existing in the database
     *
     * @return a unique short code
     */
    private CompletableFuture<String> generateUniqueShortCode() {
        return CompletableFuture.supplyAsync(this.codeSupplier).thenComposeAsync(code -> this.database.isCodeExisting(code)
                .thenComposeAsync(codeExists -> codeExists ? generateUniqueShortCode() : CompletableFuture.completedFuture(code)));
    }
}