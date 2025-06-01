package com.loganv308.botbanner;

import java.io.IOException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FirewallHandler {

    private static final Logger logger = LogManager.getLogger(FirewallHandler.class);
    
    public boolean addFirewallRule(String ipAddr) throws IOException, InterruptedException {
        String[] cmd = {"iptables", "-A", "INPUT", "-s", ipAddr, "-j", "DROP"};

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
    
            if (exitCode != 0) {
                Scanner errScanner = new Scanner(process.getErrorStream());
                logger.error("Failed to add iptables rule:");
                while (errScanner.hasNextLine()) {
                    logger.error(errScanner.nextLine());
                }
                errScanner.close();
            }
    
            return exitCode == 0;
        } catch (IOException e) {
            logger.error("Exception: " + e.getMessage());
            return false;
        }
    }  
}
