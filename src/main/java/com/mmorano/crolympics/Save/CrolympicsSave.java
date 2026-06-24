package com.mmorano.crolympics.Save;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class CrolympicsSave implements Serializable {

    public boolean TrySave(SaveData data){
        boolean result = true;
        String userHome = System.getProperty("user.home");
        Path filePath = Path.of(userHome, "Desktop", "CrolympicsSave.ser");
        try (FileOutputStream fileOut = new FileOutputStream(filePath.toString());
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(data);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public SaveData TryLoad(){
        SaveData data = new SaveData();
        String userHome = System.getProperty("user.home");
        Path filePath = Path.of(userHome, "Desktop", "CrolympicsSave.ser");
        try (FileInputStream fileIn = new FileInputStream(filePath.toString());
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            data = (SaveData) in.readObject();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        return data;
    }
}

