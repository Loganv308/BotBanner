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

    // Class variables
    private final String username;
    private final String password;
    private final String url;

    private static final String createTablesSQL = "src/main/java/com/loganv308/botbanner/Queries/CREATEIPTABLES.sql";

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

    // Function to load the SQL File into the 
    public static String loadSQLFile(String path) throws IOException  {
        try(InputStream input = DatabaseHandler.class.getClassLoader().getResourceAsStream(path)) {
            if (input == null) throw new RuntimeException("SQL file not found: " + path);
            return new BufferedReader(new InputStreamReader(input))
                .lines()
                .collect(Collectors.joining("\n"));
        } 
    }

    // Check if table exists, returns true if found, else returns false. 
    public boolean tableExists(Connection con, String tableName) {
        boolean exists = false;
        try {
            DatabaseMetaData dbMeta = con.getMetaData();
            ResultSet rs = dbMeta.getTables(null, null, tableName, null);
            while(rs.next()) {
                String name = rs.getString("IPInformation");
                if(tableName.equals(name)) {
                    exists = true;
                    System.out.println("Table exists, moving on...");
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in Table_exists check: " + e);
        }

        return exists;
    }

    // Creates the DB tables in the IPInformation table
    public void createTables() throws IOException, SQLException {

        Connection con = connect();

        String tableName = "IPAddresses";
        
        boolean tableExists = tableExists(con, tableName);

        if(tableExists == true) {
            System.out.println("Tables already created, moving on..." + "\n");
        } else {
            try (con){

                String sql = loadSQLFile(createTablesSQL);

                Statement stmt = con.createStatement();

                stmt.executeQuery(sql);

            } catch (IOException e) {
                System.out.println("No file found: " + e);
                throw e;
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e);
                throw e;
            }
        }
    }

    public void resetKeys() throws IOException, SQLException {
        Connection con = connect();
        
        try {
            String sql = loadSQLFile("src/main/java/com/loganv308/botbanner/Queries/RESETGENIDS.sql");

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
