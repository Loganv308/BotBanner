package com.loganv308.botbanner;

import java.io.IOException;

public class IPHandler {
    
    public static void getIPInformation(String ipAddr) {
        
    }

    // Put IP Addresses in a list instead and check for duplicates that way. 
    public boolean ip_exists(String ipAddr) {
        String[] cmd = {"/bin/sh", "-c", "iptables -C INPUT -s " + ipAddr + " -j DROP"};
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println(exitCode);
            return exitCode == 0; // 1 means the rule exists
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
