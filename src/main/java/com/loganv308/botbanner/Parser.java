package com.loganv308.botbanner;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;

public class Parser {

    // Hardcoded file path to latest server logs file. 
    private static final File SERVERLOGS = new File("/home/anton/mcserver/logs/latest.log");

    // Initializing Firewall Handler class
    private static final FirewallHandler fwHandler = new FirewallHandler();

    // Initializing IP Handler class
    private static final IPHandler ipHandler = new IPHandler();

    private static final FileHandler fileHandler = new FileHandler();

    private static final DatabaseHandler dbHandler = new DatabaseHandler();
    
    private static final Connection con = dbHandler.connect();

    private static final Set<String> ipAddr = new HashSet<>();

    public void run() throws IOException, InterruptedException, ParseException {
        
        Set<String> whiteList = new HashSet<>(); // Declare and initialize before try-catch

        if(con != null) {
            System.out.println("Database connection successful!");
        } else {
            System.out.println("Database connection didn't work :(");
        }

        try {
            whiteList = fileHandler.getWhitelist();
            System.out.println("Whitelist loaded: " + whiteList);
        } catch (IOException | ParseException e) {
            System.err.println("Error loading whitelist: " + e.getMessage());
            throw e;
        }

        // Regex pattern for Bot Usernames in log file.
        String regexPattern = "(?:\\b(\\w+))?\\s*\\(/\\b(\\d{1,3}(?:\\.\\d{1,3}){3}):\\d+\\)";

        // Pattern object to compile the regex pattern (usernamePattern).
        Pattern pattern = Pattern.compile(regexPattern);

        // Try readlogs of the Scanner object, taking in the ServerLogs (Global Variable File Path)
        try (Scanner readLogs = new Scanner(SERVERLOGS)) {

            // While the file has a next line (this will run through the whole file until the final text)
            while (readLogs.hasNextLine()) {
                
                // The log line is assigned to the String data variable. 
                String data = readLogs.nextLine();

                // Matcher object is used to match the pattern against the data
                Matcher matcher = pattern.matcher(data);
                
                // If the matcher indicates a match, it will print it to the console.
                if (matcher.find()) {
                    String username = matcher.group(1);
                    String ipAddress = matcher.group(2);

                    ipAddr.add(ipAddress);

                    if (username != null && whiteList.contains(username)) {
                        System.out.println(username + " is in whitelist, moving on...");
                        System.out.println("\n");
                    } else {

                        boolean exists = ipHandler.ip_exists(ipAddress);
                        System.out.println("IP rule exists for " + ipAddress + ": " + exists);

                        if (!ipHandler.ip_exists(ipAddress)) {
                            System.out.println("Username: " + username + ", IP: " + ipAddress + " adding IP address to ban list...");
                            fwHandler.addFirewallRule(ipAddress);
                        }  else {
                            System.out.println("Username: " + username + ", IP: " + ipAddress + " already exists...");
                        }   
                    }  
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading whitelist file: " + e.getMessage());
            throw e;
        }

        System.out.println(ipAddr);
        fileHandler.writeToFile(ipAddr);
    }
}
