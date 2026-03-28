package com.scheduler.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    //Database Credentials
    private static final String URL = "jdbc:mysql://localhost:3306/meeting_scheduler";
    private static final String USER = "java_user";
    private static final String PASSWORD = "password123"; 
    
    // Add this static flag to track the first connection
    private static boolean hasConnected = false;

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            
            // Check the flag before printing
            if (!hasConnected) {
                System.out.println("Database connection successful!");
                hasConnected = true; // Set to true so it never prints again
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: Failed to connect to the database.");
            e.printStackTrace();
        }
        return connection;
    }
}
