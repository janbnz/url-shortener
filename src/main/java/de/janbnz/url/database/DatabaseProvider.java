package de.janbnz.url.database;

import de.janbnz.url.database.impl.MongoConnection;
import de.janbnz.url.database.impl.SqlDatabase;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseProvider {

    private final Dotenv dotenv;

    public DatabaseProvider(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    public Database getDatabase() {
        final String databaseType = this.dotenv.get("DATABASE");
        Database database = null;

        switch (databaseType.toLowerCase()) {
            case "sqlite" -> database = this.createSqliteDatabase();
            case "mysql" -> database = this.createMySqlDatabase();
            case "mongodb" -> database = this.createMongoDatabase();
        }

        return database;
    }

    private Database createSqliteDatabase() {
        final String path = this.dotenv.get("SQLITE_PATH");
        final String user = this.dotenv.get("SQLITE_USER");
        final String password = this.dotenv.get("SQLITE_PASSWORD");
        return new SqlDatabase("jdbc:sqlite:" + path, user, password);
    }

    private Database createMySqlDatabase() {
        final String host = this.dotenv.get("MYSQL_HOST");
        final String port = this.dotenv.get("MYSQL_PORT");
        final String user = this.dotenv.get("MYSQL_USER");
        final String password = this.dotenv.get("MYSQL_PASSWORD");
        final String databaseName = this.dotenv.get("MYSQL_DATABASE");

        return new SqlDatabase("jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?autoReconnect=true", user, password);
    }

    private Database createMongoDatabase() {
        final String host = this.dotenv.get("MONGODB_HOST");
        final String port = this.dotenv.get("MONGODB_PORT");
        final String user = this.dotenv.get("MONGODB_USER");
        final String password = this.dotenv.get("MONGODB_PASSWORD");
        final String databaseName = this.dotenv.get("MONGODB_DATABASE");

        return new MongoConnection(host, port, user, password, databaseName);
    }
}