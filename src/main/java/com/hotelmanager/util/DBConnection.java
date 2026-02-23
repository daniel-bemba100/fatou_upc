package com.hotelmanager.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Business Database Connection - MariaDB for all operations except authentication
 * This handles all business logic: rooms, customers, reservations, payments, etc.
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;
    private String dbType;
    
    private DBConnection() {
        loadConfiguration();
    }
    
    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
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
            System.out.println("Using default database configuration");
        }
        
        // Use business database configuration (MariaDB)
        dbType = props.getProperty("business.db.type", "mariadb");
        
        if ("mariadb".equalsIgnoreCase(dbType)) {
            // MariaDB configuration for business operations
            url = props.getProperty("business.db.url", "jdbc:mariadb://localhost:3306/hotel_manager_db?serverTimezone=UTC");
            username = props.getProperty("business.db.username", "root");
            password = props.getProperty("business.db.password", "");
        } else {
            // MySQL configuration (legacy support)
            url = props.getProperty("business.db.url", "jdbc:mysql://localhost:3306/hotel_manager_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            username = props.getProperty("business.db.username", "root");
            password = props.getProperty("business.db.password", "");
        }
    }
    
    /**
     * Initialize the database connection
     */
    public static void initialize() {
        getInstance();
    }
    
    /**
     * Initialize database tables (for MariaDB/MySQL)
     * Note: For MariaDB, tables should be created via SQL scripts, not here
     */
    public void initializeDatabase() {
        // For MariaDB, we don't auto-create tables here
        // Tables should be set up via SQL scripts
        Logger.info("Business database (MariaDB) connection ready");
    }
    
    /**
     * Test connection with custom parameters
     */
    public boolean testConnection(String host, String port, String database, String username, String password, String dbType) {
        String testUrl;
        if ("mariadb".equalsIgnoreCase(dbType)) {
            testUrl = "jdbc:mariadb://" + host + ":" + port + "/" + database + "?serverTimezone=UTC";
        } else {
            testUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        }
        try {
            if ("mariadb".equalsIgnoreCase(dbType)) {
                Class.forName("org.mariadb.jdbc.Driver");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
            try (Connection conn = DriverManager.getConnection(testUrl, username, password)) {
                return conn != null && !conn.isClosed();
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                if ("mariadb".equalsIgnoreCase(dbType)) {
                    Class.forName("org.mariadb.jdbc.Driver");
                } else {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                }
                connection = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                if ("mariadb".equalsIgnoreCase(dbType)) {
                    throw new SQLException("MariaDB JDBC Driver not found", e);
                } else {
                    throw new SQLException("MySQL JDBC Driver not found", e);
                }
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
        return dbType;
    }
    
    public boolean isMariaDB() {
        return "mariadb".equalsIgnoreCase(dbType);
    }
    
    public boolean isMySQL() {
        return "mysql".equalsIgnoreCase(dbType);
    }
}

