package com.loganv308.botbanner;

public class IPAddress {

    private String hostname;
    private String city;
    private String region;
    private String country;
    private String coords;
    private String organization;
    private Integer zip;
    private String timezone;
    
    public IPAddress (
            String hostname, 
            String city, 
            String region, 
            String country, 
            String coords, 
            String organization, 
            Integer zip, 
            String timezone
    ) {
        this.hostname = hostname;
        this.city = city;
        this.region = region;
        this.country = country;
        this.coords = coords;
        this.organization = organization;
        this.zip = zip;
        this.timezone = timezone;
    }

    public String getHostname() { return this.hostname; }
    public String getCity() { return this.city; }
    public String getRegion() { return this.region; }
    public String getCountry() { return this.country; }
    public String getCoords() { return this.coords; }
    public String getOrganization() { return this.organization; }
    public Integer getZip() { return this.zip; }
    public String getTimezone() { return this.timezone; }

}
