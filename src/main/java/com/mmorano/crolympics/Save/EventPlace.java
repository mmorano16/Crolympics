package com.mmorano.crolympics.Save;

import com.mmorano.crolympics.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class EventPlace implements Serializable {
    private String placeName;
    private ArrayList<Player> winners = new ArrayList<>();
    private int points;

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public ArrayList<Player> getWinners() {
        return winners;
    }

    public void setWinners(ArrayList<Player> winners) {
        this.winners = winners;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
