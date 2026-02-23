package com.hotelmanager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Authentication Database Connection - dedicated SQLite connection for login and registration only
 * This provides local authentication capability independent of the main business database
 */
public class AuthDBConnection {
    private static AuthDBConnection instance;
    private Connection connection;
    private String url;
    private String sqlitePath;
    
    private AuthDBConnection() {
        loadConfiguration();
    }
    
    public static AuthDBConnection getInstance() {
        if (instance == null) {
            synchronized (AuthDBConnection.class) {
                if (instance == null) {
                    instance = new AuthDBConnection();
                }
            }
        }
        return instance;
    }
    
    private void loadConfiguration() {
        Properties props = new Properties();
        try {
            String configPath = System.getProperty("user.dir") + "/config.properties";
            FileInputStream fis = new FileInputStream(configPath);
            props.load(fis);
            fis.close();
        } catch (IOException e) {
            System.out.println("Using default authentication database configuration");
        }
        
        // Use auth-specific SQLite configuration
        sqlitePath = props.getProperty("auth.db.sqlite.path", "./hotel_manager_auth.db");
        
        // SQLite configuration for authentication
        String dbPath = new File(sqlitePath).getAbsolutePath();
        url = "jdbc:sqlite:" + dbPath;
    }
    
    /**
     * Initialize the authentication database connection
     */
    public static void initialize() {
        getInstance();
    }
    
    /**
     * Initialize authentication database tables (SQLite)
     */
    public void initializeDatabase() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            
            // Create users table for authentication
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password_hash TEXT NOT NULL, " +
                "email TEXT, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "phone TEXT, " +
                "role_id INTEGER DEFAULT 1, " +
                "is_active INTEGER DEFAULT 1, " +
                "security_question TEXT, " +
                "security_answer_hash TEXT, " +
                "recovery_token TEXT, " +
                "recovery_token_expires TIMESTAMP, " +
                "last_login TIMESTAMP, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            
            stmt.close();
            Logger.info("Authentication database (SQLite) initialized successfully");
        } catch (SQLException e) {
            Logger.error("Error initializing authentication database", e);
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(url);
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC Driver not found", e);
            }
        }
        return connection;
    }
    
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn != null && !testConn.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public String getDbType() {
        return "sqlite";
    }
    
    public boolean isSqlite() {
        return true;
    }
    
    public String getUrl() {
        return url;
    }
}

