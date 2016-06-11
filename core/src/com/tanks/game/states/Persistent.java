package com.tanks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.Map;


/**
 * Created by ortal on 6/11/2016.
 */
public class Persistent {


    private final Preferences preferences;

    public Persistent() {
        preferences = Gdx.app.getPreferences("Persistent");

    }

    public void saveStrign(Map<String, String> map) {

        preferences.put(map);
        preferences.flush();
    }
    public void saveInt(Map<String, Integer> map) {

        preferences.put(map);
        preferences.flush();
    }
    public int LoadInt(String key) {
        return preferences.getInteger(key);
    }
    public String LoadString(String key) {
        return preferences.getString(key);
    }

}
