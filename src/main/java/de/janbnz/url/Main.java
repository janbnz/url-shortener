package de.janbnz.url;

import de.janbnz.url.auth.AuthenticationProvider;
import de.janbnz.url.auth.Encryption;
import de.janbnz.url.database.Database;
import de.janbnz.url.database.DatabaseProvider;
import de.janbnz.url.rest.RestServer;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) {
        final Dotenv dotenv = Dotenv.load();

        final DatabaseProvider databaseProvider = new DatabaseProvider(dotenv);

        final Database database = databaseProvider.getDatabase();
        if (database == null) throw new NullPointerException("Database not found");
        database.connect();

        final Encryption encryption = new Encryption(dotenv.get("salt"));
        final String jwtSecret = dotenv.get("jwtSecret");

        final AuthenticationProvider authProvider = new AuthenticationProvider(encryption, jwtSecret, database);
        new RestServer(8020, database, authProvider);
    }
}