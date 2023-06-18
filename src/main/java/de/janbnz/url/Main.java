package de.janbnz.url;

import de.janbnz.url.database.SqlDatabase;
import de.janbnz.url.rest.RestServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        final SqlDatabase database = new SqlDatabase();
        database.connect().join();
        database.executeUpdate("CREATE TABLE IF NOT EXISTS urls(original_url varchar(150), shortened_url varchar(10), redirects int);").join();

        new RestServer(database);
    }
}