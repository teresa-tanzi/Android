package com.example.teresa.orologiomondiale;

public class CittaRicercate {
    private int totalResoultCount;
    private Citta[] geonames;

    public String toString() {
        String s="";
        for (Citta c: geonames) {
            s+=c.toString();
        }
        return s;
    }

    public Citta[] getGeonames() {
        return geonames;
    }
}
