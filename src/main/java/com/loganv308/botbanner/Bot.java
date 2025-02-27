package com.loganv308.botbanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bot {
    public static void main(String[] args) {
        try {
            // Change to log path in the MCServer Directory (/home/anton/mcserver/logs/latest.log)
            File logs = new File("/home/anton/mcserver/logs/latest.log");
            
            String ipv4Pattern = "\\b((25[0-5]|2[0-4][0-9]|1?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|1?[0-9][0-9]?)\\b";

            Pattern pattern = Pattern.compile(ipv4Pattern);

            Scanner readLogs = new Scanner(logs);
            
            while (readLogs.hasNextLine()) {
                String data = readLogs.nextLine();
                
                Matcher matcher = pattern.matcher(data);

                if(matcher.find()) {
                    System.out.println(matcher.group()); 
                }
            }
            
            readLogs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }   
    }
}