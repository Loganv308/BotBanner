package com.loganv308.botbanner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IPHandler {

    // Specific class instances
    private static final String APIURL = "http://ip-api.com/json/";
    private static IPAddress ipAddress;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(IPHandler.class);
    
    // Takes in a set of IP addresses, calls the IP-API website above which obtains information of each. From there, it passes the information into the database via IP Address class.  
    public IPAddress getIPInformation(Set<String> ipAddr) {
        try {
            Iterator<String> ipIterator = ipAddr.iterator();

            HttpClient client = HttpClient.newHttpClient();

            logger.debug(ipAddr);

            // HTTP request for IP info website
            while(ipIterator.hasNext()) {
                String ipURL = APIURL + ipIterator.next();

                HttpRequest IPRequest = HttpRequest.newBuilder()
                    .uri(new URI(ipURL))
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(IPRequest, HttpResponse.BodyHandlers.ofString());

                ipAddress = mapper.readValue(response.body(), IPAddress.class);

                mapper.readTree(response.body());    

                logger.debug("URL: " + ipURL);
                logger.info(ipAddress.toString() + "\n");
            }
 
        } catch (URISyntaxException e) {
            logger.error("URL is not valid: " + e);
        } catch (IOException e) {
            logger.error("IOException: " + e);
        } catch (InterruptedException e) {
            logger.error("InterruptedException: " + e);
        }

        return ipAddress;
    }

    public static String getAPIURL() {
        return APIURL;
    }

    // Put IP Addresses in a list instead and check for duplicates that way. 
    public boolean ip_exists(String ipAddr) {
        String[] cmd = {"/bin/sh", "-c", "iptables -C INPUT -s " + ipAddr + " -j DROP"};
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            logger.debug("IP_Exists Exit code: " + exitCode);
            return exitCode == 0; // 1 means the rule exists
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
