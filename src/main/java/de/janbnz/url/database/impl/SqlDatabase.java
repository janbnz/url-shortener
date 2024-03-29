package de.janbnz.url.database.impl;

import de.janbnz.url.auth.Encryption;
import de.janbnz.url.auth.user.Role;
import de.janbnz.url.auth.user.User;
import de.janbnz.url.database.Database;
import de.janbnz.url.service.ShortenedURL;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class SqlDatabase extends Database {

    private Connection connection;

    private final String url;
    private final String username;
    private final String password;

    public SqlDatabase(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Connects to the SQLite database
     */
    public void connect() {
        try {
            if (this.username.isEmpty() || this.password.isEmpty()) {
                this.connection = DriverManager.getConnection(this.url);
            } else {
                this.connection = DriverManager.getConnection(this.url, this.username, this.password);
            }

            // Create tables if not exists
            this.executeUpdate("CREATE TABLE IF NOT EXISTS urls(original_url varchar(150), shortened_url varchar(10), redirects int);").join();
            this.executeUpdate("CREATE TABLE IF NOT EXISTS users(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username VARCHAR(32) NOT NULL, " +
                    "password VARCHAR(512) NOT NULL," +
                    "role VARCHAR(32) NOT NULL);");
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
    public CompletableFuture<Void> registerUser(String username, String encryptedPassword, Role role) {
        final String sql = "INSERT INTO users(id, username, password, role) VALUES (?, ?, ?, ?);";
        return this.executeUpdate(sql, null, username, encryptedPassword, role.toString());
    }

    @Override
    public CompletableFuture<Boolean> isUserExisting(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        return this.getCount(sql, username).thenApplyAsync(count -> (count > 0));
    }

    @Override
    public CompletableFuture<User> login(String username, String password, Encryption encryption) {
        final String sql = "SELECT * FROM users WHERE username = ?";
        return this.executeQuery(sql, username).thenApplyAsync(set -> {
            try (set) {
                if (set == null || !set.next()) return null;

                final String encryptedPassword = set.getString("password");
                if (!encryption.verify(password, encryptedPassword)) return null;

                return new User(String.valueOf(set.getInt("id")), set.getString("username"), Role.valueOf(set.getString("role")));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
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
        });
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