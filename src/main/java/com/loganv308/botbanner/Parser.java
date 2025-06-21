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

    private static final String SCHEMANAME = "IPInformation";
    private static final String TABLENAME = "IPInfo";

    // Initializing class logger
    private static final Logger logger = LogManager.getLogger(Parser.class);
    
    // Initializing File Handler class
    private static final FileHandler fileHandler = new FileHandler();

    // Initializing Database Handler class
    private static final DatabaseHandler dbHandler = new DatabaseHandler();

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {

        try {
            Set<String> whiteList = fileHandler.getWhitelist();
            logger.info("Whitelist loaded: " + whiteList);
        } catch (IOException | ParseException e) {
            logger.error("Error loading whitelist: " + e.getMessage());
            throw e;
        }

        fileHandler.getRegexIPs();

        try {
            if(!dbHandler.schemaExists()) {
                logger.info("Creating Schema...");
                dbHandler.createSchema(SCHEMANAME);
            } else {
                logger.info("Schema is already created, moving on...");
            }

            if(!dbHandler.tableExists(TABLENAME)) {
                logger.info("Creating table...");
                dbHandler.createTable();
            } else {
                logger.info("Table is already created, moving on...");
            }

            Thread.sleep(16000);

        } catch (SQLException e) {
            logger.error("SQL Exception: " + e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }
}
