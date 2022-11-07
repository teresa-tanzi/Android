package com.example.teresa.orologiomondiale;

public class CittaTz {
    private String sunrise;
    private String lng;
    private String countryCode;
    private String gmtOffset;
    private String rawOffser;
    private String sunset;
    private String timezoneId;
    private String dstOffset;
    private String countryName;
    private String time;
    private String lat;

    public String getTimezoneId() {
        return timezoneId;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getString() {
        return ("timezone: "+timezoneId);
    }
}
