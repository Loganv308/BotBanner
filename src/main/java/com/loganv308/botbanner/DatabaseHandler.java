package com.loganv308.botbanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseHandler {

    // User, Pass, URL class variables used for connecting to Database. 
    private final String username;
    private final String password;
    private final String url;
    
    // Connection used throughout this class in multiple functions.  
    private static Connection con;

    // Create tables SQL script in the Queries directory. 
    private static final String CREATEIPTABLES = "src/main/java/com/loganv308/botbanner/Queries/CREATEIPTABLES.sql";
    private static final String RESETGENIDS = "src/main/java/com/loganv308/botbanner/Queries/RESETGENIDS.sql";

    // Constructor which grabs the ENV variables set for DB User, Password, and JDBC Connection URL. 
    public DatabaseHandler() {
        this.username = System.getenv("BOTBANNER_USER");
        this.password = System.getenv("BOTBANNER_PASSWORD");
        this.url = System.getenv("BOTBANNER_URL"); 
    }

    // Connect to Database. Returns c (connection) 
    public Connection connect() throws SQLException {
        try {
            con = DriverManager.getConnection(url, username, password);
        }  catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            throw e;
        }

        return con;
    }

    // Function to load the SQL File into the 
    public static String loadSQLFile(String path) throws IOException  {
        try(InputStream input = DatabaseHandler.class.getClassLoader().getResourceAsStream(path)) {
            System.out.println("THIS IS THE INPUTSTREAM: " + input);
            if (input == null) throw new RuntimeException("SQL file not found: " + path);
            return new BufferedReader(new InputStreamReader(input))
                .lines()
                .collect(Collectors.joining("\n"));
        } 
    }

    // Check if table exists, returns true if found, else returns false. 
    public boolean tableExists(String tableName) {
        boolean exists = false;
        
        if (con == null) {
            System.out.println("Can't connect to Database.");
            return false;
        } else {
            try {
                DatabaseMetaData dbMeta = con.getMetaData();
                try (ResultSet rs = dbMeta.getTables(null, null, tableName, null)) {
                    while(rs.next()) {
                        String name = rs.getString("IPInformation");
                        if(tableName.equalsIgnoreCase(name)) {
                            exists = true;
                            System.out.println("Table exists, moving on...");
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Caught: " + e.getClass().getName() + "Error: " + e);
            }
        }

        return exists;
    }

    // Creates the DB tables in the IPInformation table
    public void createTables() throws SQLException {

        String tableName = "IPInformation";
        
        boolean tableExists = tableExists(tableName);

        if(tableExists == true) {
            System.out.println("Tables already created, moving on..." + "\n");
        } else {
            try {
                if(con == null) {
                    System.out.println("Connection is null, fix DB Connection.");
                } else {
                    String sql = loadSQLFile(CREATEIPTABLES);

                    Statement stmt = con.createStatement();
    
                    stmt.executeQuery(sql);
                }
            } catch (IOException e) {
                System.out.println("No file found: " + e);
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e);
                throw e;
            }
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
                System.out.println("Last ID: " + id);
            }
        
        } catch (IOException e) {
            System.out.println("No file found: " + e);
            throw e;
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e);
            throw e;
        }
    }
}
