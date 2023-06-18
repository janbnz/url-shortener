package de.janbnz.url.service;

import de.janbnz.url.database.SqlDatabase;
import de.janbnz.url.generator.ShortCodeGenerator;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class ShorteningService {

    private final SqlDatabase database;

    public ShorteningService(SqlDatabase database) {
        this.database = database;
    }

    public CompletableFuture<String> createURL(String originalURL) {
        return generateUniqueShortCode().thenComposeAsync(shortenedUrl -> {
            String sql = "INSERT INTO urls(original_url, shortened_url, redirects) VALUES (?, ?, ?)";
            return database.executeUpdate(sql, originalURL, shortenedUrl, 0).thenApply(result -> shortenedUrl);
        });
    }

    public CompletableFuture<String> createRedirect(String shortenedURL) {
        return getInformation(shortenedURL).thenComposeAsync(url -> {
            final String originalURL = url.getOriginalURL();
            if (originalURL != null) {
                String sql = "UPDATE urls SET redirects = redirects + 1 WHERE shortened_url = ?";
                return database.executeUpdate(sql, shortenedURL).thenApply(result -> originalURL);
            } else {
                return CompletableFuture.completedFuture(null);
            }
        });
    }

    public CompletableFuture<ShortenedURL> getInformation(String shortenedURL) {
        String sql = "SELECT * FROM urls WHERE shortened_url = ?";
        return database.executeQuery(sql, shortenedURL).thenApplyAsync(resultSet -> {
            try {
                if (resultSet != null && resultSet.next()) {
                    return new ShortenedURL(resultSet.getString("original_url"), shortenedURL, resultSet.getInt("redirects"));
                } else {
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private CompletableFuture<String> generateUniqueShortCode() {
        return CompletableFuture.supplyAsync(ShortCodeGenerator::generateShortCode).thenComposeAsync(code -> isCodeExisting(code)
                .thenComposeAsync(codeExists -> codeExists ? generateUniqueShortCode() : CompletableFuture.completedFuture(code)));
    }

    private CompletableFuture<Boolean> isCodeExisting(String code) {
        String sql = "SELECT COUNT(*) FROM urls WHERE shortened_url = ?";
        return database.executeQuery(sql, code).thenApplyAsync(resultSet -> {
            try {
                if (resultSet != null && resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return false;
        });
    }
}