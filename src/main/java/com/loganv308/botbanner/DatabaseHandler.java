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

import io.github.cdimascio.dotenv.Dotenv;

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
    private static final String CREATEDATABASE = "src/main/java/com/loganv308/botbanner/Queries/CREATEDATABASE.sql";
    private static final String DATABASEEXISTS = "src/main/java/com/loganv308/botbanner/Queries/DATABASEEXISTS.sql";

    // Hardcoded table and database names
    // May take these out later honestly, this class should function as a "check for any existstence" of whatever database you want to pass into it. 
    private static final String DATABASENAME = "IPInformation";
    private static final String TABLENAME = "IPInfo";

    // Special library to load environment variables from .env file
    private static final Dotenv dotenv = Dotenv.load();

    // Constructor which grabs the ENV variables set for DB User, Password, and JDBC Connection URL. 
    public DatabaseHandler() {
        this.username = dotenv.get("BOTBANNER_USER");
        this.password = dotenv.get("BOTBANNER_PASSWORD");
        this.url = dotenv.get("BOTBANNER_URL"); 
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
        System.out.println(sb.toString());
        return sb.toString();
    }

    // Check if database exists, returns true if found, else returns false. 
    public boolean databaseExists(String tableName) {
        boolean exists = false;
        
        if (con == null) {
            System.out.println("Can't connect to Database.");
            return false;
        }

        try (Statement stmt = con.createStatement();){

            String databaseExists = loadSQLFile(DATABASEEXISTS);

            ResultSet resultSet = stmt.executeQuery(databaseExists);

            if(resultSet.getInt(1) != 0) {
                exists = true;
                stmt.close();
                resultSet.close();
                return exists;
            } else {
                exists = false;
                stmt.close();
                resultSet.close();
                return exists;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Can't access file. ");
        }

        return exists;
    }

    // Creates the DB tables in the IPInformation table
    public void createTable() throws SQLException {

        // String tableName = "IPInformation";
        
        boolean databaseExists = databaseExists(DATABASENAME);

        if(databaseExists == true) {
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
                if (e.getErrorCode() == 1007) {
                    // Database already exists error
                    System.out.println("Database already exists. Printing error msg.");
                    System.out.println(e.getMessage());
                } else {
                    // Some other problems, e.g. Server down, no permission, etc
                    e.printStackTrace();
                }
            }
        }
    }

    public void createDatabase() throws SQLException {
        if (con == null) {
            System.out.println("Can't connect to Database.");
        } else {
            try {
                String sql = loadSQLFile(CREATEDATABASE);

                Statement stmt = con.createStatement();

                stmt.execute(sql);

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
