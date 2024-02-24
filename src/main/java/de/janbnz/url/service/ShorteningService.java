package de.janbnz.url.service;

import de.janbnz.url.database.SqlDatabase;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ShorteningService {

    private final SqlDatabase database;
    private final Supplier<String> codeSupplier;

    public ShorteningService(SqlDatabase database, Supplier<String> codeSupplier) {
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
        return generateUniqueShortCode().thenComposeAsync(shortenedUrl -> {
            String sql = "INSERT INTO urls(original_url, shortened_url, redirects) VALUES (?, ?, ?)";
            return database.executeUpdate(sql, originalURL, shortenedUrl, 0).thenApply(result -> shortenedUrl);
        });
    }

    /**
     * Returns the original url and increments the amount of redirects in the database
     *
     * @param shortenedURL the shortened url
     * @return the original url
     */
    public CompletableFuture<String> createRedirect(String shortenedURL) {
        return getInformation(shortenedURL).thenComposeAsync(information -> {
            if (information == null) {
                return CompletableFuture.completedFuture(null);
            }

            final String originalURL = information.originalURL();
            String sql = "UPDATE urls SET redirects = redirects + 1 WHERE shortened_url = ?";
            return database.executeUpdate(sql, shortenedURL).thenApply(result -> originalURL);
        });
    }

    /**
     * Returns information about the shortened url
     *
     * @param shortenedURL the shortened url
     * @return information like the original url or the amount of redirects
     */
    public CompletableFuture<ShortenedURL> getInformation(String shortenedURL) {
        String sql = "SELECT * FROM urls WHERE shortened_url = ?";
        return database.executeQuery(sql, shortenedURL).thenApplyAsync(resultSet -> {
            try (resultSet) {
                if (resultSet == null || !resultSet.next()) return null;
                return new ShortenedURL(resultSet.getString("original_url"), shortenedURL, resultSet.getInt("redirects"));
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Generates a unique short code which is not existing in the database
     *
     * @return a unique short code
     */
    private CompletableFuture<String> generateUniqueShortCode() {
        return CompletableFuture.supplyAsync(this.codeSupplier).thenComposeAsync(code -> isCodeExisting(code)
                .thenComposeAsync(codeExists -> codeExists ? generateUniqueShortCode() : CompletableFuture.completedFuture(code)));
    }

    /**
     * Checks if a code is existing in the database
     *
     * @param code the unique short code
     * @return returns true if the code is existing
     */
    private CompletableFuture<Boolean> isCodeExisting(String code) {
        String sql = "SELECT COUNT(*) FROM urls WHERE shortened_url = ?";
        return database.executeQuery(sql, code).thenApplyAsync(resultSet -> {
            try (resultSet) {
                if (resultSet == null || !resultSet.next()) return false;
                int count = resultSet.getInt(1);
                return count > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }
}