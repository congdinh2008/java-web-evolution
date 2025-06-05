package com.congdinh.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Utility class for database operations
 */
public class DatabaseUtil {
    
    private static final String PROPERTIES_FILE = "db.properties";
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    
    static {
        loadDatabaseProperties();
    }
    
    /**
     * Load database properties from file
     */
    private static void loadDatabaseProperties() {
        Properties props = new Properties();
        
        try (InputStream is = DatabaseUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (is != null) {
                props.load(is);
                dbUrl = props.getProperty("db.url");
                dbUser = props.getProperty("db.user");
                dbPassword = props.getProperty("db.password");
                
                // Ensure JDBC driver is loaded
                try {
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                } catch (ClassNotFoundException e) {
                    System.err.println("[DatabaseUtil] loadDatabaseProperties: Failed to load JDBC driver");
                    e.printStackTrace(System.err);
                }
            } else {
                System.err.println("[DatabaseUtil] loadDatabaseProperties: Properties file not found: " + PROPERTIES_FILE);
            }
        } catch (IOException e) {
            System.err.println("[DatabaseUtil] loadDatabaseProperties: Error loading properties file");
            e.printStackTrace(System.err);
        }
    }
    
    /**
     * Get a database connection
     * @return database connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (dbUrl == null || dbUser == null || dbPassword == null) {
            throw new SQLException("Database connection properties not loaded correctly");
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
    
    /**
     * Close database resources safely
     * @param rs ResultSet to close
     * @param stmt Statement to close
     * @param conn Connection to close
     */
    public static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("[DatabaseUtil] closeResources: Failed to close ResultSet");
                e.printStackTrace(System.err);
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("[DatabaseUtil] closeResources: Failed to close Statement");
                e.printStackTrace(System.err);
            }
        }
        
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[DatabaseUtil] closeResources: Failed to close Connection");
                e.printStackTrace(System.err);
            }
        }
    }
}
