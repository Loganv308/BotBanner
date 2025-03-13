package com.loganv308.botbanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Parser {

    // Hardcoded file path to latest server logs file. 
    private static final File SERVERLOGS = new File("/home/anton/mcserver/logs/latest.log");

    // Hardcoded file path to server whitelist.
    private static final File WHITELIST = new File("/home/anton/mcserver/whitelist.json");

    // Method to get the current server Whitelist
    public Set<String> getWhitelist() throws IOException, ParseException {

        Set<String> whiteList = new HashSet<>();
        
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
        } catch (FileNotFoundException e) {
            System.err.println("Whitelist file not found: " + e.getMessage());
            throw e;  // Rethrowing for caller to handle
        } catch (IOException e) {
            System.err.println("Error reading whitelist file: " + e.getMessage());
            throw e;
        } catch (ParseException e) {
            System.err.println("Error parsing whitelist JSON: " + e.getMessage());
            throw e;
        } 

        return whiteList;
    }

    public void run() throws IOException, InterruptedException, ParseException {

        Set<String> whiteList = new HashSet<>(); // Declare and initialize before try-catch

        try {
            whiteList = getWhitelist();
            System.out.println("Whitelist loaded: " + whiteList);
        } catch (IOException | ParseException e) {
            System.err.println("Error loading whitelist: " + e.getMessage());
            throw e;
        }

        // Regex pattern for Bot Usernames in log file.
        String usernamePattern = "\\]:\\s*([\\w\\d_]+) \\(/([\\d\\.]+):";

        // Pattern object to compile the regex pattern (usernamePattern).
        Pattern pattern = Pattern.compile(usernamePattern);
        
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

                    if (whiteList.contains(username)) {
                        System.out.println(username + " is in whitelist, moving on...");
                        return;
                    } else {
                        String command = String.format("iptables -A INPUT -s %s -j DROP", ipAddress);

                        if (!ip_exists(ipAddress)) {
                            System.out.println("Username: " + username + ", IP: " + ipAddress + " adding IP address to ban list...");
                            runCommand(command);
                        }  else {
                            System.out.println("Username: " + username + ", IP: " + ipAddress + " already exists...");
                        }   
                    }     
                } 
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error reading whitelist file: " + e.getMessage());
            throw e;
        }
    }

    private static boolean ip_exists(String ip) {
        String[] cmd = {"/bin/sh", "-c", "iptables -C INPUT -s " + ip + " -j DROP"};
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0; // 0 means the rule exists
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private static void runCommand(String command) throws IOException, InterruptedException {
        // Get process runtime to execute commands
        Process proc = Runtime.getRuntime().exec(command);

        // Wait for command to finish
        proc.waitFor();
    } 

    // This checks periodically if new bots have tried joining the server.
    // private static void periodicCheck() {
        
    // }   
}
