package de.janbnz.url.service;

import de.janbnz.url.database.impl.SqlDatabase;

public class AuthorizationService {

    private final SqlDatabase database;

    public AuthorizationService(SqlDatabase database) {
        this.database = database;
    }
}