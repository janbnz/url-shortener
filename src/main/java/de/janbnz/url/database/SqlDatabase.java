package de.janbnz.url.database;

import de.janbnz.url.auth.Encryption;
import de.janbnz.url.service.ShortenedURL;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SqlDatabase extends Database {

    private Connection connection;
    private final ExecutorService executorService;

    private final String url;

    public SqlDatabase(String url) {
        this.executorService = Executors.newFixedThreadPool(1);
        this.url = url;
    }

    /**
     * Connects to the SQLite database
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(url);

            // Create tables if not exists
            this.executeUpdate("CREATE TABLE IF NOT EXISTS urls(original_url varchar(150), shortened_url varchar(10), redirects int);").join();
            this.executeUpdate("CREATE TABLE IF NOT EXISTS users(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username VARCHAR(32) NOT NULL, " +
                    "password VARCHAR(512) NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnects from the SQLite database
     */
    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> createURL(String originalURL, String shortenedURL) {
        String sql = "INSERT INTO urls(original_url, shortened_url, redirects) VALUES (?, ?, ?)";
        return this.executeUpdate(sql, originalURL, shortenedURL, 0);
    }

    @Override
    public CompletableFuture<Void> createRedirect(String shortenedURL) {
        String sql = "UPDATE urls SET redirects = redirects + 1 WHERE shortened_url = ?";
        return this.executeUpdate(sql, shortenedURL);
    }

    @Override
    public CompletableFuture<ShortenedURL> getInformation(String shortenedURL) {
        String sql = "SELECT * FROM urls WHERE shortened_url = ?";
        return this.executeQuery(sql, shortenedURL).thenApplyAsync(resultSet -> {
            try (resultSet) {
                if (resultSet == null || !resultSet.next()) return null;
                return new ShortenedURL(resultSet.getString("original_url"), shortenedURL, resultSet.getInt("redirects"));
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isCodeExisting(String shortenedURL) {
        String sql = "SELECT COUNT(*) FROM urls WHERE shortened_url = ?";
        return this.getCount(sql, shortenedURL).thenApplyAsync(count -> (count > 0));
    }

    @Override
    public CompletableFuture<Void> registerUser(String username, String encryptedPassword) {
        final String sql = "INSERT INTO users(id, username, password) VALUES (?, ?, ?);";
        return this.executeUpdate(sql, null, username, encryptedPassword);
    }

    @Override
    public CompletableFuture<Boolean> isUserExisting(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        return this.getCount(sql, username).thenApplyAsync(count -> (count > 0));
    }

    @Override
    public CompletableFuture<Boolean> login(String username, String password, Encryption encryption) {
        final String sql = "SELECT password FROM users WHERE username = ?";
        return this.executeQuery(sql, username).thenApplyAsync(set -> {
            try (set) {
                if (set == null || !set.next()) return false;
                final String encryptedPassword = set.getString("password");
                return encryption.verify(password, encryptedPassword);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    private CompletableFuture<Integer> getCount(String sql, Object... params) {
        return this.executeQuery(sql, params).thenApplyAsync(resultSet -> {
            try (resultSet) {
                if (resultSet == null || !resultSet.next()) return 0;
                return resultSet.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    /**
     * Executes an SQLite update
     *
     * @return CompletableFuture<Void> indicating the completion of the update operation
     */
    private CompletableFuture<Void> executeUpdate(String sql, Object... params) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = prepareStatementWithParams(sql, params)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Queries from the SQLite database
     *
     * @return CompletableFuture<ResultSet> representing the result of the query
     */
    private CompletableFuture<ResultSet> executeQuery(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
                return preparedStatement.executeQuery();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return null;
        }, executorService);
    }

    /**
     * Creates a PreparedStatement with parameters
     *
     * @param sql    the SQL query string
     * @param params the parameters to be set in the PreparedStatement
     * @return the prepared statement
     * @throws SQLException if a database access error occurs
     */
    private PreparedStatement prepareStatementWithParams(String sql, Object... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement;
    }
}