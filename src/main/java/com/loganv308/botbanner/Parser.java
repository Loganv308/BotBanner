package com.loganv308.botbanner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

public class Parser {

    // Initializing Firewall Handler class
    // private static final FirewallHandler fwHandler = new FirewallHandler();
    private static final Logger logger = LogManager.getLogger(Parser.class);
    // Initializing File Handler class
    private static final FileHandler fileHandler = new FileHandler();

    // Initializing Database Handler class
    private static final DatabaseHandler dbHandler = new DatabaseHandler();

    public void run() throws IOException, InterruptedException, ParseException {

        try {
            Set<String> whiteList = fileHandler.getWhitelist();
            logger.info("Whitelist loaded: " + whiteList);
        } catch (IOException | ParseException e) {
            logger.error("Error loading whitelist: " + e.getMessage());
            throw e;
        }

        fileHandler.getRegexIPs();

        try {
            //dbHandler.connect();
            //System.out.println(dbHandler.databaseExists("IPInformation"));
            
            if (!dbHandler.databaseExists("IPInformation")) {
                logger.info("Creating database...");
                dbHandler.createDatabase();
            } else {
                logger.info("Database is already created, moving on...");
            }
            
            // if(!dbHandler.createTable()) {

            // }

        } catch (SQLException e) {
            logger.error("SQL Exception: " + e);
        }
    }
}
