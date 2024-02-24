package de.janbnz.url;

import de.janbnz.url.database.SqlDatabase;
import de.janbnz.url.rest.RestServer;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) {
        final Dotenv dotenv = Dotenv.load();

        final String sqlUrl = dotenv.get("DB_URL");
        final SqlDatabase database = new SqlDatabase(sqlUrl);
        database.connect().join();
        database.executeUpdate("CREATE TABLE IF NOT EXISTS urls(original_url varchar(150), shortened_url varchar(10), redirects int);").join();

        new RestServer(8020, database);
    }
}