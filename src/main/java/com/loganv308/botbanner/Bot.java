package com.loganv308.botbanner;

import java.io.IOException;

import org.json.simple.parser.ParseException;

public class Bot {
    // Main function
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        Parser parser = new Parser();

        while (true) { 
            parser.run();

            try {
                // 1 hour prod code
                // Thread.sleep(3600);

                // 6 Seconds test code
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } 
    }                     
}