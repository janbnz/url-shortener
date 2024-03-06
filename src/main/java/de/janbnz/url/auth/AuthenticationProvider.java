package de.janbnz.url.auth;

import de.janbnz.url.auth.user.Role;
import de.janbnz.url.auth.user.User;
import de.janbnz.url.database.Database;

import java.util.concurrent.CompletableFuture;

public class AuthenticationProvider {

    private final Encryption encryption;
    private final Database database;
    private final Authentication authentication;

    public AuthenticationProvider(Encryption encryption, String jwtSecret, Database database) {
        this.encryption = encryption;
        this.database = database;
        this.authentication = new Authentication(jwtSecret);
    }

    public CompletableFuture<Void> register(String username, String password, Role role) {
        final String encryptedPassword = this.encryption.hash(password);
        return this.database.registerUser(username, encryptedPassword, role);
    }

    public CompletableFuture<Boolean> isExisting(String username) {
        return this.database.isUserExisting(username);
    }

    public CompletableFuture<User> login(String username, String password) {
        return this.database.login(username, password, this.encryption);
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}