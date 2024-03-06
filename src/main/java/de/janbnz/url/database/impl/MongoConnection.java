package de.janbnz.url.database.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.janbnz.url.auth.Encryption;
import de.janbnz.url.auth.user.Role;
import de.janbnz.url.auth.user.User;
import de.janbnz.url.database.Database;
import de.janbnz.url.service.ShortenedURL;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.concurrent.CompletableFuture;

public class MongoConnection extends Database {

    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final String databaseName;

    private MongoClient client;

    private MongoCollection<Document> urlCollection;
    private MongoCollection<Document> userCollection;

    public MongoConnection(String host, String port, String username, String password, String databaseName) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
    }

    @Override
    public void connect() {
        this.client = MongoClients.create("mongodb://" + this.username + ":" + this.password + "@" + this.host + ":" + this.port);

        final MongoDatabase database = this.client.getDatabase(this.databaseName);
        this.urlCollection = database.getCollection("urls");
        this.userCollection = database.getCollection("users");
    }

    @Override
    public void disconnect() {
        if (this.client == null) return;
        this.client.close();
        this.client = null;
    }

    @Override
    public CompletableFuture<Void> createURL(String originalURL, String shortenedURL) {
        return CompletableFuture.runAsync(() -> {
            final Document document = new Document().append("originalURL", originalURL).append("shortenedURL", shortenedURL).append("redirects", 0);
            this.urlCollection.insertOne(document);
        });
    }

    @Override
    public CompletableFuture<Void> createRedirect(String shortenedURL) {
        return CompletableFuture.runAsync(() -> {
            final Bson filter = Filters.eq("shortenedURL", shortenedURL);

            final Document document = this.urlCollection.find(filter).first();
            if (document == null) return;

            document.put("redirects", document.getInteger("redirects") + 1);
            this.urlCollection.findOneAndReplace(filter, document);
        });
    }

    @Override
    public CompletableFuture<ShortenedURL> getInformation(String shortenedURL) {
        return CompletableFuture.supplyAsync(() -> {
            final Bson filter = Filters.eq("shortenedURL", shortenedURL);

            final Document document = this.urlCollection.find(filter).first();
            if (document == null) return null;

            return new ShortenedURL(document.getString("originalURL"), shortenedURL, document.getInteger("redirects"));
        });
    }

    @Override
    public CompletableFuture<Boolean> isCodeExisting(String shortenedURL) {
        return CompletableFuture.supplyAsync(() -> {
            final Bson filter = Filters.eq("shortenedURL", shortenedURL);

            final Document document = this.urlCollection.find(filter).first();
            return document != null;
        });
    }

    @Override
    public CompletableFuture<Void> registerUser(String username, String encryptedPassword, Role role) {
        return CompletableFuture.runAsync(() -> {
            final Document document = new Document().append("username", username).append("password", encryptedPassword).append("role", role.toString());
            this.userCollection.insertOne(document);
        });
    }

    @Override
    public CompletableFuture<Boolean> isUserExisting(String username) {
        return CompletableFuture.supplyAsync(() -> {
            final Bson filter = Filters.eq("username", username);

            final Document document = this.userCollection.find(filter).first();
            return document != null;
        });
    }

    @Override
    public CompletableFuture<User> login(String username, String password, Encryption encryption) {
        return CompletableFuture.supplyAsync(() -> {
            final Bson filter = Filters.eq("username", username);

            final Document document = this.userCollection.find(filter).first();
            if (document == null) return null;

            final String encryptedPassword = document.getString("password");
            if (!encryption.verify(password, encryptedPassword)) return null;

            return new User(document.getObjectId("_id").toString(), document.getString("username"), Role.valueOf(document.getString("role")));
        });
    }
}
