package com.hotelmanager.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe logging utility for Hotel Manager application.
 * Provides methods to log messages at different severity levels.
 */
public class Logger {
    
    private static final String LOG_FILE = "hotel_manager.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ReentrantLock lock = new ReentrantLock();
    
    /**
     * Log severity levels
     */
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
    
    /**
     * Thread-safe logging method with lock mechanism.
     * @param level The severity level
     * @param message The message to log
     */
    public static void log(LogLevel level, String message) {
        lock.lock();
        try {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String logEntry = String.format("[%s] [%s] %s", timestamp, level, message);
            
            System.out.println(logEntry);
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                writer.println(logEntry);
            } catch (IOException e) {
                System.err.println("Failed to write to log file: " + e.getMessage());
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Log debug message
     * @param message The debug message
     */
    public static void debug(String message) {
        log(LogLevel.DEBUG, message);
    }
    
    /**
     * Log info message
     * @param message The info message
     */
    public static void info(String message) {
        log(LogLevel.INFO, message);
    }
    
    /**
     * Log warning message
     * @param message The warning message
     */
    public static void warn(String message) {
        log(LogLevel.WARN, message);
    }
    
    /**
     * Log error message
     * @param message The error message
     */
    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }
    
    /**
     * Log error with exception stack trace
     * @param message The error message
     * @param throwable The exception to log
     */
    public static void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message + " - " + throwable.getMessage());
    }
}
