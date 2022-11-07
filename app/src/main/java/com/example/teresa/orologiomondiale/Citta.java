package com.example.teresa.orologiomondiale;

public class Citta {
    private String adminCode1;
    private String lng;
    private String geonameId;
    private String toponymName;
    private String countryId;
    private String fcl;
    private String population;
    private String countryCode;
    private String name;
    private String fclName;
    private String countryName;
    private String fcodeName;
    private String adminName1;
    private String lat;
    private String fcode;
    private String timezone;

    public String toString() {
        //Milan, Italy
        return (name+", "+countryName);
    }

    public String getName() {
        return name;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public void setTimezone(String tz) {
        this.timezone=tz;
    }

    public String getTimezone() {
        return timezone;
    }
}
