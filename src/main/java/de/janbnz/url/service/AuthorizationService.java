package de.janbnz.url.service;

import de.janbnz.url.database.SqlDatabase;

import java.util.concurrent.CompletableFuture;

public class AuthorizationService {

    private final SqlDatabase database;

    public AuthorizationService(SqlDatabase database) {
        this.database = database;
    }
}