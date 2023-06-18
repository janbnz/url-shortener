package de.janbnz.url.database;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SqlDatabase {

    private static final String DB_URL = "jdbc:sqlite:database.db";

    private Connection connection;

    private final ExecutorService executorService;

    public SqlDatabase() {
        this.executorService = Executors.newFixedThreadPool(1);
    }

    public CompletableFuture<Void> connect() {
        return CompletableFuture.runAsync(() -> {
            try {
                connection = DriverManager.getConnection(DB_URL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

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

    public CompletableFuture<Void> executeUpdate(String sql, Object... params) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = prepareStatementWithParams(sql, params)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

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

    private PreparedStatement prepareStatementWithParams(String sql, Object... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement;
    }

    public Connection getConnection() {
        return connection;
    }
}