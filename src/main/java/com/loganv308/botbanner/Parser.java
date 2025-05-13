package com.loganv308.botbanner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import org.json.simple.parser.ParseException;

public class Parser {

    // Initializing Firewall Handler class
    // private static final FirewallHandler fwHandler = new FirewallHandler();

    // Initializing File Handler class
    private static final FileHandler fileHandler = new FileHandler();

    // Initializing Database Handler class
    private static final DatabaseHandler dbHandler = new DatabaseHandler();

    public void run() throws IOException, InterruptedException, ParseException {

        try {
            Set<String> whiteList = fileHandler.getWhitelist();
            System.out.println("Whitelist loaded: " + whiteList);
        } catch (IOException | ParseException e) {
            System.err.println("Error loading whitelist: " + e.getMessage());
            throw e;
        }

        fileHandler.getRegexIPs();

        // Main connect to database try/catch 
        // Connection con = dbHandler.connect()
        try {
            dbHandler.connect();
            dbHandler.createTables();
        } catch (SQLException e) {
            System.out.println("Unable to create tables: " + e);
        }
    }
}
