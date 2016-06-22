package com.tanks.game.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.tanks.game.TanksDemo;
import com.tanks.game.sprites.Button;
import com.tanks.game.utils.Assets;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Brent on 6/26/2015.
 */
public class MenuState extends State {

    private static final List<String> requiredTextures = Arrays.asList(new String[]{
            "bg.png",
    });

    private final Stage stage;

    private Texture background;

    private Button mButton1;

    private Button mButton2;

    public MenuState(final GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        loadAssets();
//        playBtn = new Texture("button.png");

        stage = new Stage();
        mButton1 = new Button(stage, 200, 200);
        mButton2 = new Button(stage, 200, 500);

        mButton1.getButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new OnlinePlayState(gsm, true));
            }
        });
        mButton2.getButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new OnlinePlayState(gsm, false));
            }
        });

    }

    private void loadAssets() {
        Assets.getInstance().getManager().load("bg.png", Texture.class);
        //load all assets in queue, block until finished
        Assets.getInstance().getManager().finishLoading();
        background = Assets.getInstance().getManager().get("bg.png");
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        handleInput();
        cam.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0, 0);

        stage.draw();

        sb.end();
    }

    @Override
    public void render(ShapeRenderer sr) {
//        sr.setProjectionMatrix(cam.combined);
//        sr.setAutoShapeType(true);
//        sr.begin();
//        sr.setColor(Color.BLACK);
//        sr.polygon(mButton1.getBoundsPolygon().getTransformedVertices());
//        sr.polygon(mButton2.getBoundsPolygon().getTransformedVertices());
//        sr.polygon(touchPolygon.getTransformedVertices());
//
//        sr.end();

    }

    @Override
    public void dispose() {
        background.dispose();
        mButton1.dispose();
        mButton2.dispose();
        System.out.println("Menu State Disposed");
    }
}
