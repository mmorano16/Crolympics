package com.mmorano.crolympics;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    @Override
    public boolean equals(Object obj){
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        Method[] methods = this.getClass().getMethods();
        for(Method method : methods){
            if(method.getName().startsWith("get")){
                try{
                    Object x = method.invoke(this);
                    Object y = method.invoke(obj);
                    if(x == null){
                        if(y != null){
                            return false;
                        }
                    } else if (!x.equals((y))){
                        return false;
                    }
                } catch (IllegalAccessException | InvocationTargetException e){
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
