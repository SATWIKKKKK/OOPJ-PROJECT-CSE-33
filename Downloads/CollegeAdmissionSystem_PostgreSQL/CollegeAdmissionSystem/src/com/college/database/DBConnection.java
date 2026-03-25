package com.college.database;

import com.college.utils.EnvConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Database Connection Manager
 * Demonstrates DESIGN PATTERN (Singleton) + Encapsulation
 * Reads credentials from .env file via EnvConfig
 */
public class DBConnection {

    // ── Configuration loaded from .env (with safe defaults) ──────────────────
    private static final String HOST     = EnvConfig.get("DB_HOST", "localhost");
    private static final String PORT     = EnvConfig.get("DB_PORT", "5432");
    private static final String DATABASE = EnvConfig.get("DB_NAME", "college_db");
    private static final String USERNAME = EnvConfig.get("DB_USERNAME", "postgres");
    private static final String PASSWORD = EnvConfig.get("DB_PASSWORD", "admin123");

    private static final String URL =
        "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;

    // Singleton instance
    private static Connection connection = null;

    // Private constructor — prevents instantiation
    private DBConnection() {}

    /**
     * Returns the single shared Connection instance.
     * Creates it on first call (lazy initialization).
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                connection.setAutoCommit(true);
            } catch (ClassNotFoundException e) {
                throw new SQLException(
                    "PostgreSQL JDBC Driver not found.\n" +
                    "Download postgresql-42.x.x.jar from https://jdbc.postgresql.org/download/\n" +
                    "Place it in the 'lib/' folder and add to classpath.", e);
            }
        }
        return connection;
    }

    /** Gracefully close the connection (call on app shutdown). */
    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("  [DB] Connection closed.");
            } catch (SQLException e) {
                System.err.println("  [DB] Error closing connection: " + e.getMessage());
            }
        }
    }

    /** Test connectivity and print status. */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("  [DB] ✓ Connected to PostgreSQL — " + DATABASE + " @ " + HOST + ":" + PORT);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("  [DB] ✗ Connection failed: " + e.getMessage());
        }
        return false;
    }
}
