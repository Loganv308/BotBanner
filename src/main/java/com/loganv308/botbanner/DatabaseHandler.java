package com.loganv308.botbanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {

    // Class variables
    private final String username;
    private final String password;
    private final String url;

    // Constructor
    public DatabaseHandler() {
        this.username = System.getenv("BOTBANNER_USER");
        this.password = System.getenv("BOTBANNER_PASSWORD");
        this.url = System.getenv("BOTBANNER_URL"); 
    }

    // Connect to Database. Returns c (connection) 
    public Connection connect() {
        Connection c = null;
        
        try {
            c = DriverManager.getConnection(url, username, password);
        }  catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }

        return c;
    }
}
