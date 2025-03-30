package com.loganv308.botbanner;

import java.io.IOException;
import java.util.Scanner;

public class FirewallHandler {
    public boolean addFirewallRule(String ipAddr) throws IOException, InterruptedException {
        String[] cmd = {"iptables", "-A", "INPUT", "-s", ipAddr, "-j", "DROP"};

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
    
            if (exitCode != 0) {
                Scanner errScanner = new Scanner(process.getErrorStream());
                System.err.println("Failed to add iptables rule:");
                while (errScanner.hasNextLine()) {
                    System.err.println(errScanner.nextLine());
                }
                errScanner.close();
            }
    
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception: " + e.getMessage());
            return false;
        }
    }  
}
