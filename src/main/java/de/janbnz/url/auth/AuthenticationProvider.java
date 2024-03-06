package de.janbnz.url.auth;

import de.janbnz.url.database.SqlDatabase;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AuthenticationProvider {

    private final Encryption encryption;
    private final SqlDatabase database;
    private final Authentication authentication;

    public AuthenticationProvider(Encryption encryption, String jwtSecret, SqlDatabase database) {
        this.encryption = encryption;
        this.database = database;
        this.authentication = new Authentication(jwtSecret);
    }

    public CompletableFuture<Void> register(String username, String password) {
        return CompletableFuture.runAsync(() -> {
            final String sql = "INSERT INTO users(id, username, password) VALUES (?, ?, ?);";
            final String encryptedPassword = this.encryption.hash(password);
            this.database.executeUpdate(sql, null, username, encryptedPassword);
        });
    }

    public CompletableFuture<Boolean> isExisting(String username) {
        return CompletableFuture.supplyAsync(() -> {
            final String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            return this.database.executeQuery(sql, username).thenComposeAsync(set -> {
                try (set) {
                    if (set == null || !set.next()) return CompletableFuture.completedFuture(false);
                    int count = set.getInt(1);
                    return CompletableFuture.completedFuture(count > 0);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return CompletableFuture.completedFuture(false);
                }
            });
        }).thenCompose(Function.identity());
    }

    public CompletableFuture<Boolean> login(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            final String sql = "SELECT password FROM users WHERE username = ?";
            return this.database.executeQuery(sql, username).thenComposeAsync(set -> {
                try (set) {
                    if (set == null || !set.next()) return CompletableFuture.completedFuture(false);
                    final String encryptedPassword = set.getString("password");
                    return CompletableFuture.completedFuture(this.encryption.verify(password, encryptedPassword));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return CompletableFuture.completedFuture(false);
                }
            });
        }).thenCompose(Function.identity());
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}