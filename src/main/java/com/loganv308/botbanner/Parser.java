package com.loganv308.botbanner;

import java.io.IOException;
import java.sql.Connection;
import java.util.Set;

import org.json.simple.parser.ParseException;

public class Parser {

    // Initializing Firewall Handler class
    // private static final FirewallHandler fwHandler = new FirewallHandler();

    // Initializing File Handler class
    private static final FileHandler fileHandler = new FileHandler();

    // Initializing Database Handler class
    private static final DatabaseHandler dbHandler = new DatabaseHandler();
    
    // Initializing Connection for Database class
    private static final Connection con = dbHandler.connect();

    public void run() throws IOException, InterruptedException, ParseException {

        // if(con != null) {
        //     System.out.println("Database connection successful!");
        // } else {
        //     System.out.println("Database connection didn't connect :(");
        // }

        try {
            Set<String> whiteList = fileHandler.getWhitelist();
            System.out.println("Whitelist loaded: " + whiteList);
        } catch (IOException | ParseException e) {
            System.err.println("Error loading whitelist: " + e.getMessage());
            throw e;
        }

        fileHandler.getRegexIPs();
    }
}
