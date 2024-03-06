package de.janbnz.url.auth.user;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {

    ANYONE, USER, ADMIN
}