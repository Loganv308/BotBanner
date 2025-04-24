package com.loganv308.botbanner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IPHandler {

    private static final String APIURL = "http://ip-api.com/json/";
    
    // Takes in a set of IP addresses, calls the IP-API website above which obtains information of each. From there, it passes the information into the database via IP Address class. 
    // TODO: Add for loop to go through the set, passing each IP to the website for further analysis. 
    public static void getIPInformation(Set<String> ipAddr) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            
            // HTTP request for IP info website
            for(String ip : ipAddr) {
                
                String ipURL = APIURL + ip;

                HttpRequest IPRequest = HttpRequest.newBuilder()
                    .uri(new URI(ipURL))
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(IPRequest, HttpResponse.BodyHandlers.ofString());

                ObjectMapper mapper = new ObjectMapper();

                List<IPAddress> ipAddresses = mapper.readValue(response.body(), new TypeReference<List<IPAddress>>() {});

                ipAddresses.forEach(System.out::println);
                
            }  

        } catch (URISyntaxException e) {
            System.out.println("URL is not valid: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
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
