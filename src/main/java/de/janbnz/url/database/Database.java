package de.janbnz.url.database;

import de.janbnz.url.auth.Encryption;
import de.janbnz.url.auth.user.Role;
import de.janbnz.url.auth.user.User;
import de.janbnz.url.service.ShortenedURL;

import java.util.concurrent.CompletableFuture;

public abstract class Database {

    public abstract void connect();

    public abstract void disconnect();

    public abstract CompletableFuture<Void> createURL(String originalURL, String shortenedURL);

    public abstract CompletableFuture<Void> createRedirect(String shortenedURL);

    public abstract CompletableFuture<ShortenedURL> getInformation(String shortenedURL);

    public abstract CompletableFuture<Boolean> isCodeExisting(String shortenedURL);

    public abstract CompletableFuture<Void> registerUser(String username, String encryptedPassword, Role role);

    public abstract CompletableFuture<Boolean> isUserExisting(String username);

    public abstract CompletableFuture<User> login(String username, String password, Encryption encryption);
}