package com.loganv308.botbanner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class FileHandler {
    public void writeToFile(Set<String> ipAddresses) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/com/loganv308/botbanner/Output/ListOfIPs.txt"))){
            for(String element : ipAddresses) {
                writer.append(element);
                writer.newLine();
            }            
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
