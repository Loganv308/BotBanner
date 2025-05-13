package com.loganv308.botbanner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IPHandler {

    // Specific class instances
    private static final String APIURL = "http://ip-api.com/json/";
    private static IPAddress ipAddress;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    // Takes in a set of IP addresses, calls the IP-API website above which obtains information of each. From there, it passes the information into the database via IP Address class.  
    public IPAddress getIPInformation(Set<String> ipAddr) {
        try {
            Iterator<String> ipIterator = ipAddr.iterator();

            HttpClient client = HttpClient.newHttpClient();

            System.out.println(ipAddr);

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

                System.out.println("URL: " + ipURL);
                System.out.println(ipAddress.toString() + "\n");
            }
 
        } catch (URISyntaxException e) {
            System.out.println("URL is not valid: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            System.out.println(exitCode);
            return exitCode == 0; // 1 means the rule exists
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
