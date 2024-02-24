package de.janbnz.url;

import de.janbnz.url.auth.AuthenticationProvider;
import de.janbnz.url.auth.Encryption;
import de.janbnz.url.database.SqlDatabase;
import de.janbnz.url.rest.RestServer;
import io.github.cdimascio.dotenv.Dotenv;
import org.mindrot.jbcrypt.BCrypt;

public class Main {

    public static void main(String[] args) {
        final Dotenv dotenv = Dotenv.load();

        final SqlDatabase database = new SqlDatabase(dotenv.get("DB_URL"));
        database.connect().join();
        database.executeUpdate("CREATE TABLE IF NOT EXISTS urls(original_url varchar(150), shortened_url varchar(10), redirects int);").join();

        final Encryption encryption = new Encryption(dotenv.get("salt"));

        final AuthenticationProvider authProvider = new AuthenticationProvider(encryption, database);
        new RestServer(8020, database, authProvider);
    }
}