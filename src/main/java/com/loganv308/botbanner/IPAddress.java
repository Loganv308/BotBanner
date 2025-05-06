package com.loganv308.botbanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IPAddress {

    // Class Arguments based on JSON response from "ip-api.com".
    private String host;
    private String city;
    private String region;
    private String regionName;
    private String country;
    private Long latitude;
    private Long longitude;
    private String organization;
    private Integer zip;
    private String timezone;
    private String query;
    
    // Contructor with arguments
    public IPAddress (
            @JsonProperty("as") String host,
            @JsonProperty("city") String city,
            @JsonProperty("region") String region,
            @JsonProperty("regionName") String regionName,
            @JsonProperty("country") String country,
            @JsonProperty("lat") Long latitude,
            @JsonProperty("lon") Long longitude,
            @JsonProperty("org") String organization,
            @JsonProperty("zip") Integer zip,
            @JsonProperty("timezone") String timezone,
            @JsonProperty("query") String query
    ) {
        this.host = host;
        this.city = city;
        this.region = region;
        this.regionName = regionName;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.organization = organization;
        this.zip = zip;
        this.timezone = timezone;
        this.query = query;
    }

    // Blank constructor for initialization
    public IPAddress() {
    }
    
    // Getters, no need for setters.
    public String getHost() { return this.host; }
    public String getCity() { return this.city; }
    public String getRegion() { return this.region; }
    public String getRegionName() { return this.regionName; }
    public String getCountry() { return this.country; }
    public Long getLat() { return this.latitude; }
    public Long getLong() { return this.longitude; }
    public String getOrganization() { return this.organization; }
    public Integer getZip() { return this.zip; }
    public String getTimezone() { return this.timezone; }
    public String getQuery() { return this.query; }

    // Prints object instance to console.
    @Override
    public String toString() {
        return 
            "Hostname: " + getHost() + " | " + 
            "City: " + getCity() + " | " +  
            "Region: " + getRegion() + " | " +
            "RegionName: " + getRegionName() + " | " +
            "Country: " + getCountry() + " | " +
            "Latitude: " + getLat() + " | " +
            "Longitude: " + getLong() + " | " +
            "Organization: " + getOrganization() + " | " +
            "Zip: " + getZip() + " | " +
            "Timezone: " + getTimezone() + " | " +
            "Query (IP Address): " + getQuery()
        ;
    }

}
