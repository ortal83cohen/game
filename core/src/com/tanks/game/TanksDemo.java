package com.tanks.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tanks.game.states.MenuState;

public class TanksDemo extends ApplicationAdapter {

    public static final int WIDTH = 480;

    public static final int HEIGHT = 800;

    public static final String TITLE = "Tanks.io";

    private com.tanks.game.states.GameStateManager gsm;

    private SpriteBatch batch;
    private ShapeRenderer mShapeRenderer;

    private Music music;


    @Override
    public void create() {
        batch = new SpriteBatch();
        mShapeRenderer = new ShapeRenderer();
        gsm = new com.tanks.game.states.GameStateManager();
//        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
//        music.setLooping(true);
//        music.setVolume(0.1f);
//        music.play();
        Gdx.gl.glClearColor(1, 0, 0, 1);
        gsm.push(new MenuState(gsm));
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(batch);
        gsm.render(mShapeRenderer);
    }

    @Override
    public void dispose() {
        super.dispose();
//        music.dispose();
    }

}
