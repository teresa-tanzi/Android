package com.example.teresa.orologiomondiale;

import java.util.ArrayList;

public class ListOfCities extends ArrayList<Citta> {

    public ListOfCities() {
        super();
    }

    public void addCity (Citta c) {
        this.add(c);
    }

    public void removeAllCities() {
        this.clear();
    }

    public void removeCity(String lat, String lng) {
        for (int i=0; i<this.size(); i++) {
            Citta c=this.get(i);
            if (lat.equals(c.getLat()) && lng.equals(c.getLng())) {
                this.remove(c);
            }
        }
    }
}
