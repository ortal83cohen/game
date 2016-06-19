package com.tanks.game.utils;

import com.badlogic.gdx.assets.AssetManager;

import java.util.List;
import java.util.Map;

/**
 * Created by chovel on 18/06/2016.
 *
 */
public class Assets {

    private static final Assets INSTANCE = new Assets();

    private AssetManager assetManager;

    private Assets() {
        assetManager = new AssetManager();
    }

    public static Assets getInstance() {
        return INSTANCE;
    }

    public AssetManager getManager() {
        return assetManager;
    }

    public void loadSingleTypeAssetList(List<String> files, Class clazz) {
        for (String file : files) {
            assetManager.load(file, clazz);
        }
    }

    public void loadMultiTypeAssetList(Map<Class, List<String>> clazzToFileList) {
        for (Class clazz : clazzToFileList.keySet()) {
            loadSingleTypeAssetList(clazzToFileList.get(clazz), clazz);
        }
    }


}
