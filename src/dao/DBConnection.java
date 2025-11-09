/**
 * Database Connection Manager
 * 
 * This class manages database connections using a connection pool pattern.
 * Features include:
 * - Connection pooling for improved performance
 * - Automatic connection validation and recovery
 * - Thread-safe connection management
 * - Graceful shutdown handling
 * - Comprehensive error handling and logging
 * 
 * Configuration:
 * - Pool size: 10 connections
 * - Connection timeout: 30 seconds
 * - Auto-reconnect enabled
 * - MySQL JDBC driver with SSL disabled for development
 * 
 * @author College Admission System Team
 * @version 2.0
 */

package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DBConnection {
    // Application shutdown flag for graceful termination
    private static boolean shutdown = false;
    
    // Database connection configuration - UPDATE THESE VALUES AS NEEDED
    private static final String URL =
        "jdbc:mysql://localhost:3306/college_admission_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Mysql123@##";  // ⚠️ IMPORTANT: Update this with your MySQL root password
    
    // Connection pool configuration for optimal performance
    private static final int POOL_SIZE = 10;                    // Number of connections in pool
    private static final int CONNECTION_TIMEOUT = 30;          // Timeout in seconds
    private static final BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);
    private static boolean poolInitialized = false;            // Flag to prevent double initialization

    /**
     * Static initializer block - runs when class is first loaded
     * Loads MySQL JDBC driver and initializes the connection pool
     * Throws DAOException if driver is not found in classpath
     */
    static {
        try {
            // Load MySQL JDBC driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Initialize connection pool on startup
            initializePool();
        } catch (ClassNotFoundException e) {
            throw new DAOException("MySQL JDBC driver not found - ensure mysql-connector-j-9.5.0.jar is in classpath", e);
        }
    }

    /**
     * Initializes the database connection pool with pre-configured connections
     * Uses double-checked locking pattern for thread safety
     * Creates POOL_SIZE number of connections and adds them to the pool
     * 
     * Configuration:
     * - Auto-reconnect enabled for connection resilience
     * - Maximum 3 reconnection attempts
     * - Auto-commit enabled for simplicity
     * 
     * @throws DAOException if database connection fails
     */
    private static void initializePool() {
        // Double-checked locking for thread safety
        if (poolInitialized) return;
        
        synchronized (DBConnection.class) {
            // Check again inside synchronized block
            if (poolInitialized) return;
            
            try {
                // Configure connection properties for optimal performance
                Properties props = new Properties();
                props.setProperty("user", USER);
                props.setProperty("password", PASSWORD);
                props.setProperty("autoReconnect", "true");     // Enable auto-reconnect
                props.setProperty("maxReconnects", "3");         // Maximum reconnection attempts
                
                // Create and populate connection pool
                for (int i = 0; i < POOL_SIZE; i++) {
                    Connection conn = DriverManager.getConnection(URL, props);
                    conn.setAutoCommit(true); // Enable auto-commit for simplicity
                    connectionPool.offer(conn);
                }
                
                // Register shutdown hook for graceful termination
                Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
                
                poolInitialized = true;
                System.out.println("✅ Connection pool initialized successfully with " + POOL_SIZE + " connections!");
                
            } catch (SQLException e) {
                String errorMsg = "Failed to initialize connection pool. Check: 1) MySQL is running, 2) Database exists, 3) Credentials are correct";
                System.err.println("❌ " + errorMsg);
                throw new DAOException(errorMsg + ": " + e.getMessage(), e);
            }
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = connectionPool.poll(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
            if (conn == null) {
                throw new DAOException("Timeout waiting for database connection");
            }
            
            if (!conn.isValid(1)) {
                // Connection is stale, create new one
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                conn.setAutoCommit(true);
            }
            
            return conn;
        } catch (SQLException | InterruptedException e) {
            System.err.println("❌ Failed to obtain database connection. Check that MySQL is running and the URL/credentials in DBConnection.java are correct.");
            throw new DAOException("Failed to obtain database connection: " + e.getMessage(), e);
        }
    }

    public static void closeAll(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    if (resource instanceof Connection) {
                        // Return connection to pool if it's not shut down
                        if (!shutdown) {
                            connectionPool.offer((Connection) resource);
                        } else {
                            resource.close();
                        }
                    } else {
                        resource.close();
                    }
                } catch (Exception e) {
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }

    public static void close(ResultSet rs, PreparedStatement ps, Connection conn) {
        closeAll(rs, ps, conn);
    }

    public static void shutdown() {
        shutdown = true;
        Connection conn;
        while ((conn = connectionPool.poll()) != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection during shutdown: " + e.getMessage());
            }
        }
    }
}
