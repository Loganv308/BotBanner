package com.loganv308.botbanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FileHandler {

    private static final Logger logger = LogManager.getLogger(FileHandler.class);

    // Hardcoded file path to latest server logs file. 
    private static final File SERVERLOGS = new File("/opt/Homelab/MCServer/server/logs/latest.log");

    // Hardcoded file path to MC server whitelist.
    private static final File WHITELIST = new File("/opt/Homelab/MCServer/server/whitelist.json");

    // Initializing IP Handler class
    private static final IPHandler ipHandler = new IPHandler();

    // Pre-loads hashset to store the current Whitelisted users
    private static final Set<String> whiteList = new HashSet<>();

    private final Set<String> processedIPs = new HashSet<>();

    // This method is not necessary. A Set of IP's can be filtered from the log file, then passed as a set to other functions throughout the program if needed. 
    @Deprecated
    public void writeToFile(Set<String> ipAddresses) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/com/loganv308/botbanner/Output/ListOfIPs.txt"))){
            for(String element : ipAddresses) {
                writer.append(element);
                writer.newLine();
            }            
        } catch (IOException e) {
            logger.error("An error occurred while writing to the file: " + e.getMessage());
            throw e;
        }
    }

    // Method to get the current server Whitelist
    public Set<String> getWhitelist() throws IOException, ParseException {
        
        JSONParser parser = new JSONParser();

        try {
            // JSONArray obj, casted as a JSONArray -> (JSONARRAY) then parsed using FileReader.
            JSONArray obj = (JSONArray) parser.parse(new FileReader(WHITELIST));

            // For each Object (jsonObj) in obj (JSONArray).
            for(Object jsonObj : obj) {

                // Cast to JSONObject as JSONObject (jsonObj).
                JSONObject jsonObject = (JSONObject) jsonObj;

                // Username gets casted as a String to jsonObject (JSONObject) and retrieves the name from the JSON data. 
                String username = (String) jsonObject.get("name");

                whiteList.add(username);
            } 
        } catch (IOException e) {
            logger.error("Error reading whitelist file: " + e.getMessage());
            throw e;
        } catch (ParseException e) {
            logger.error("Error parsing whitelist JSON: " + e.getMessage());
            throw e;
        } 
        return whiteList;
    }

    // Method to match the Minecraft server logs via Regex, grabs IPs and Usernames. 
    public void getRegexIPs() {
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

                    if (username != null && whiteList.contains(username)) {
                        logger.info(username + " is in whitelist, moving on...");
                        logger.info("\n");
                    } 

                    if(!processedIPs.contains(ipAddress)) { 
                        ipHandler.getIPInformation(Set.of(ipAddress));
                
                        // Mark as processed
                        processedIPs.add(ipAddress);
                    } else {
                        logger.info("Already processed IP: " + ipAddress);
                        logger.info("\n");
                    }
                    // Commented out temporarily for testing purposes
                    //     boolean exists = ipHandler.ip_exists(ipAddress);
                    //     System.out.println("IP rule exists for " + ipAddress + ": " + exists);

                    //     if (!ipHandler.ip_exists(ipAddress)) {
                    //         System.out.println("Username: " + username + ", IP: " + ipAddress + " adding IP address to ban list...");
                    //         fwHandler.addFirewallRule(ipAddress);
                    //     }  else {
                    //         System.out.println("Username: " + username + ", IP: " + ipAddress + " already exists...");
                    //     }   
                    
                }
            }

        } catch (IOException e) {
            logger.error("Error reading whitelist file: " + e.getMessage());
        }
    }
}
