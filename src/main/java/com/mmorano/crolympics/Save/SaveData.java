package com.mmorano.crolympics.Save;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveData implements Serializable {
    private ArrayList<SportEvent> sportEvents;
    public SaveData(ArrayList<SportEvent> sportEvents){
        this.sportEvents = sportEvents;
    }
    public SaveData(){
        this.sportEvents = new ArrayList<>();
    }

    public ArrayList<SportEvent> getEvents() {
        return sportEvents;
    }

    public void setEvents(ArrayList<SportEvent> sportEvents) {
        this.sportEvents = sportEvents;
    }
}
