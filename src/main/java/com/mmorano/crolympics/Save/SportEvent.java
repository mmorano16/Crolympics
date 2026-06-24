package com.mmorano.crolympics.Save;

import java.io.Serializable;
import java.util.ArrayList;

public class SportEvent implements Serializable {
    private String name;
    private ArrayList<EventPlace> places = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<EventPlace> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<EventPlace> places) {
        this.places = places;
    }
}
