package com.mmorano.crolympics.Save;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveData implements Serializable {
    private ArrayList<Event> events;
    public SaveData(ArrayList<Event> events){
        this.events = events;
    }
    public SaveData(){
        this.events = new ArrayList<>();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
