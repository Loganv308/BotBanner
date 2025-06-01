package com.loganv308.botbanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseHandler {

    // User, Pass, URL class variables used for connecting to Database. 
    private static String username;
    private static String password;
    private static String url;
    
    // Connection used throughout this class in multiple functions.  
    private static Connection con;

    // Create tables SQL script in the Queries directory. 
    private static final String CREATEIPTABLES = "src/main/java/com/loganv308/botbanner/Queries/CREATEIPTABLES.sql";
    private static final String RESETGENIDS = "src/main/java/com/loganv308/botbanner/Queries/RESETGENIDS.sql";
    private static final String CREATEDATABASE = "src/main/java/com/loganv308/botbanner/Queries/CREATEDATABASE.sql";
    private static final String DATABASEEXISTS = "src/main/java/com/loganv308/botbanner/Queries/DATABASEEXISTS.sql";

    // Hardcoded table and database names
    // May take these out later honestly, this class should function as a "check for any existstence" of whatever database you want to pass into it. 
    private static final String DATABASENAME = "IPInformation";
    private static final String TABLENAME = "IPInfo";

    // Logger for the class, writes to app.log
    private static final Logger logger = LogManager.getLogger(DatabaseHandler.class);

    // Special library to load environment variables from .env file
    private static final Dotenv dotenv = Dotenv.load();

    // Constructor which grabs the ENV variables set for DB User, Password, and JDBC Connection URL. 
    public DatabaseHandler() {
        this.username = dotenv.get("BOTBANNER_USER");
        this.password = dotenv.get("BOTBANNER_PASSWORD");
        this.url = dotenv.get("BOTBANNER_URL"); 
    }

    // Connect to Database. Returns c (connection) 
    public static Connection connect() throws SQLException {
        try {
            con = DriverManager.getConnection(url, username, password);
            logger.info("Connection successful, moving on...");
        }  catch (SQLException e) {
            logger.error("SQL Exception (Connect): " + e.getMessage());
        }
        logger.debug("Connection: " + con);
        return con;
    }

    // Function to load the SQL File into the 
    public static String loadSQLFile(String path) throws IOException  {
        // Used to create and manipulate string. SB is mutable, which means it can be changed after creation. 
        StringBuilder sb = new StringBuilder();
        
        // Uses a BufferedReader wrapped around a FileReader to read the file efficiently.
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            if (path == null) throw new RuntimeException("SQL file not found: " + path);
            
            String line;

            while((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } 
        // System.out.println(sb.toString());
        return sb.toString();
    }

    // Check if database exists, returns true if found, else returns false. 
    public boolean databaseExists(String databaseName) {
        
        boolean exists = false;
        
        try (Connection conn = DriverManager.getConnection(url, username, password)){
             if (conn == null) {
                logger.error("Can't connect to Database.");
                return false;
            }
            
            try (Statement stmt = conn.createStatement()) {
            // Ensure this loads a valid SQL query string
                String databaseExists = "SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'";

                try (ResultSet resultSet = stmt.executeQuery(databaseExists)) {
                    if (resultSet.next()) {
                        exists = true;
                    }
                }
            }
            
            
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } 

        return exists;
    }

    public boolean tablesExist(String[] tables) {
        boolean exists = false;
        
        

        return exists;
    }

    // Creates the DB tables in the IPInformation table
    public void createTable() throws SQLException {
        
        boolean databaseExists = databaseExists(DATABASENAME);

        if(databaseExists == true) {
            logger.info("Tables already created, moving on..." + "\n");
        } else {
            try {
                if(con == null) {
                    logger.error("Connection is null, fix DB Connection.");
                } else {
                    
                    String sql = loadSQLFile(CREATEIPTABLES);

                    Statement stmt = con.createStatement();
    
                    stmt.executeQuery(sql);
                }
            } catch (IOException e) {
                logger.error("No file found: " + e.getMessage());
            } catch (SQLException e) {
                if (e.getErrorCode() == 1007) {
                    // Database already exists error
                    logger.info("Database already exists. Printing error msg." + e.getMessage());
                } else {
                    // Some other problems, e.g. Server down, no permission, etc
                    logger.error(e.getMessage());
                }
            }
        }
    }

    public void createDatabase() throws SQLException {

        try (Connection conn = DriverManager.getConnection(url, username, password) ) {
            String sql = loadSQLFile(CREATEDATABASE);

            Statement stmt = conn.createStatement();

            stmt.execute(sql);

        } catch (IOException e) {
            logger.error("No file found: " + e.getMessage());
        } catch (SQLException e) {
            logger.error("SQL Exception: " + e.getMessage());
            throw e;
        }
    }

    public void resetIncrementKeys() throws IOException, SQLException {
        try {
            String sql = loadSQLFile(RESETGENIDS);

            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.executeUpdate();
            
            ResultSet keys = stmt.getGeneratedKeys();
            
            if (keys.next()) {
                int id = keys.getInt(1); // <- The auto-incremented ID from the sequence
                logger.info("Last ID: " + id);
            }
        
        } catch (IOException e) {
            logger.error("No file found: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            logger.error("SQL Exception: " + e.getMessage());
            throw e;
        }
    }
}
