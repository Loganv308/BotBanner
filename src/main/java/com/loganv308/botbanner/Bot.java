package com.loganv308.botbanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Bot {
    // Hardcoded file path to latest server logs file. 
    public static final File SERVERLOGS = new File("/home/anton/mcserver/logs/latest.log");

    // Hardcoded file path to server whitelist.
    public static final File WHITELIST = new File("/home/anton/mcserver/whitelist.json");

    public static List<String> whiteList = new ArrayList<>();

    // Main function
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {

        // Java JSONParser to go through the whitelist.
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

            // Prints to CLI whitelisted usernames.
            System.out.println(whiteList);
            
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
                        
                        System.out.println(username + " " + ipAddress);

                        if (whiteList.contains(username)) {
                            System.out.println(username + " is in whitelist.");
                            return;
                        } else {
                            String command = String.format("iptables -A INPUT -s %s -j DROP", ipAddress);
                            System.out.println("Username: " + username + ", IP: " + ipAddress);
                            runCommand(command);
                        }     
                    } 
                }
            }
            // If the file is not found, it will go to this error and print it via CLI. 
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }   
    }
                        
    private static void runCommand(String command) throws IOException, InterruptedException {
        // Get process runtime to execute commands
        Process proc = Runtime.getRuntime().exec(command);

        // Wait for command to finish
        proc.waitFor();

    } 

}