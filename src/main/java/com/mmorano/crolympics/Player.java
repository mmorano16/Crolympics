package com.mmorano.crolympics;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private int points;

    public Player(){}
    public Player(String name, int points){
        this.name = name;
        this.points = points;
    }
    public Player(String name){
        this(name, 0);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
