package com.tanks.io.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tanks.io.TanksIo;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = TanksIo.WIDTH;
        config.height = TanksIo.HEIGHT;
        config.title = TanksIo.TITLE;
        new LwjglApplication(new TanksIo(), config);
    }
}
