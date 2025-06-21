package com.loganv308.botbanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;

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
    private static final String TABLEEXISTS = "src/main/java/com/loganv308/botbanner/Queries/TABLEEXISTS.sql";    
    private static final String RESETGENIDS = "src/main/java/com/loganv308/botbanner/Queries/RESETGENIDS.sql";
    private static final String INSERTINTOTABLE = "src/main/java/com/loganv308/botbanner/Queries/INSERTTOIPTABLE.sql"; 

    // Logger for the class, writes to app.log
    private static final Logger logger = LogManager.getLogger(DatabaseHandler.class);

    // Special library to load environment variables from .env file
    private static final Dotenv dotenv = Dotenv.load();

    private static final IPAddress ipaddr = new IPAddress();

    // Constructor which grabs the ENV variables set for DB User, Password, and JDBC Connection URL. 
    public DatabaseHandler() {
        this.username = dotenv.get("BOTBANNER_USER");
        this.password = dotenv.get("BOTBANNER_PASSWORD");
        this.url = dotenv.get("BOTBANNER_URL"); 
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

    public boolean tableExists(String tableName) {
        
        boolean exists = false;

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            if (conn == null) {
                logger.error("Connection failed.");
                return false;
            }

            String tableExistsSql = new String(Files.readAllBytes(Paths.get(TABLEEXISTS)), StandardCharsets.UTF_8);

            PreparedStatement stmt = conn.prepareStatement(tableExistsSql);

            stmt.setString(1, tableName);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                exists = true;
            }
            

        } catch (SQLException | IOException e) {
            logger.error("Error checking if table exists: " + e.getMessage());
        }

        return exists;
    }

    // This is exclusive to PostgreSQL. Used to check for Schema existence.
    public boolean schemaExists() throws SQLException {
        Boolean exists = false;

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            if (conn == null) {
                logger.error("Connection failed.");
            } else {
                String schemaExistsSql = "SELECT schema_name FROM information_schema.schemata WHERE schema_name = 'ipinformation'";

                Statement stmt = conn.createStatement();

                try(ResultSet rs = stmt.executeQuery(schemaExistsSql)){
                    exists = rs.next();
                }    
            }

        } catch (SQLException e) {
            logger.error("Error checking if Schema exists (schemaExists): " + e.getMessage());
        }

        return exists;
    }

    // Creates the Schema within database
    public void createSchema(String schemaName) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            if(conn == null) {
                logger.error("Connection is null, fix DB Connection.");
            } else {
                // This needs to be hardcoded. Apparently PostgreSQL doesn't support prepared statements for schema based changes.
                String schemaExistsSql = "CREATE SCHEMA " + schemaName + ";";

                Statement stmt = conn.createStatement();
                
                ResultSet rs = stmt.executeQuery(schemaExistsSql); 

                if (rs.next()) {
                    logger.info("Schema " + schemaName + " has been created");
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception (createSchema): " + e.getMessage());
        }
    }

    // Creates the DB tables within postgresql database
    public void createTable() throws SQLException {
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            if(conn == null) {
                logger.error("Connection is null, fix DB Connection.");
            } else {
                
                String sql = loadSQLFile(CREATEIPTABLES);

                Statement stmt = conn.createStatement();

                stmt.executeQuery(sql);
            }
        } catch (IOException e) {
            logger.error("No file found: " + e.getMessage());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1007) {
                // table already exists error
                logger.info("Table already exists. Printing error msg." + e.getMessage());
            } else {
                // Some other problems, e.g. Server down, no permission, etc
                logger.error("other error: " + e.getMessage());
            }
        }   
    }

    // Insert the return IP data after formatting it
    public void insertData() {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            if(conn == null) {
                logger.error("Connection is null, fix DB Connection.");
            } else { 
                String sql = loadSQLFile(INSERTINTOTABLE);
                
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                stmt.setObject(1, OffsetDateTime.now()); 
                stmt.setString(2, ipaddr.getHost());
                stmt.setString(3, ipaddr.getCity());
                stmt.setString(4, ipaddr.getRegion());
                stmt.setString(5, ipaddr.getRegionName());
                stmt.setString(6, ipaddr.getCountry());
                stmt.setDouble(7, ipaddr.getLat());
                stmt.setDouble(8, ipaddr.getLong());
                stmt.setString(9, ipaddr.getOrganization());
                stmt.setString(10, ipaddr.getZip());
                stmt.setString(11, ipaddr.getTimezone());
                stmt.setString(12, ipaddr.getQuery());

                stmt.executeUpdate();

                logger.info("Executed: " + stmt.toString() );
            } 
        } catch (SQLException e) {
            logger.error("SQLException (insertData)" + e.getMessage());
        } catch (IOException e) {
            logger.error("No file found: " + e.getMessage());
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
