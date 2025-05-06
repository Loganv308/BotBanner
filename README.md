
# Java Bot Banner

A Java-based tool designed to detect bot IP addresses from server logs and automatically ban them by adding firewall rules.

This will be used on my own Linux based Home Server since there's always port scanning bots trying to connect to various services I have running. 


## Authors

- [@Loganv308](https://www.github.com/Loganv308)


## Environment Variables

To run this project, you will need to add the following environment variables to your .env file

`BOTBANNER_URL` - Database URL

`BOTBANNER_USER` - Database Username

`BOTBANNER_PASSWORD` - Database Password


## Tech Stack

**Client:** Java, JDBC, HTTPClient (API), JSONParser, ObjectMapper

**Database:** PostgreSQL
