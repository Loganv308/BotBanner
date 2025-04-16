package com.loganv308.botbanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FileHandler {

    // Hardcoded file path to MC server whitelist.
    private static final File WHITELIST = new File("/home/anton/mcserver/whitelist.json");

    public void writeToFile(Set<String> ipAddresses) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/com/loganv308/botbanner/Output/ListOfIPs.txt"))){
            for(String element : ipAddresses) {
                writer.append(element);
                writer.newLine();
            }            
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
            throw e;
        }
    }

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
        } catch (IOException e) {
            System.err.println("Error reading whitelist file: " + e.getMessage());
            throw e;
        } catch (ParseException e) {
            System.err.println("Error parsing whitelist JSON: " + e.getMessage());
            throw e;
        } 

        return whiteList;
    }
}
