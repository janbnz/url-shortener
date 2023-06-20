package de.janbnz.url.database;

import de.janbnz.url.config.ServiceConfig;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SqlDatabase {

    private Connection connection;
    private final ExecutorService executorService;

    private final ServiceConfig config;

    public SqlDatabase(ServiceConfig config) {
        this.executorService = Executors.newFixedThreadPool(1);
        this.config = config;
    }

    /**
     * Connects to the SQLite database
     *
     * @return CompletableFuture<Void> indicating the completion of the connect operation
     */
    public CompletableFuture<Void> connect() {
        return CompletableFuture.runAsync(() -> {
            try {
                connection = DriverManager.getConnection(this.config.getProperties().getProperty("db.url"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Disconnects from the SQLite database
     *
     * @return CompletableFuture<Void> indicating the completion of the disconnect operation
     */
    public CompletableFuture<Void> disconnect() {
        return CompletableFuture.runAsync(() -> {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Executes an SQLite update
     *
     * @return CompletableFuture<Void> indicating the completion of the update operation
     */
    public CompletableFuture<Void> executeUpdate(String sql, Object... params) {
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
    public CompletableFuture<ResultSet> executeQuery(String sql, Object... params) {
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