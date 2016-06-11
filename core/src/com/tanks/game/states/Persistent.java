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

    public void save(Map<String, String> map) {

        preferences.put(map);
        preferences.flush();
    }

    public String Load(String key) {

        return preferences.getString(key);
    }

}
