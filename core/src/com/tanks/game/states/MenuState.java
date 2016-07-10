package com.tanks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.tanks.game.TanksDemo;
import com.tanks.game.elements.Button;
import com.tanks.game.elements.PlayerNameTextField;
import com.tanks.game.utils.Assets;
import com.tanks.game.utils.Persistent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Brent on 6/26/2015.
 */
public class MenuState extends State {

    private static final List<String> requiredTextures = Arrays.asList(new String[]{
            "bg.png", "button.png"
    });

    private final Stage stage;

    private final PlayerNameTextField txtUsername;

    private final Persistent persistent;

    private Texture background;

    private Button mButton1;

    private Button mButton2;

    public MenuState(final GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        loadAssets();
//        playBtn = new Texture("button.png");
        persistent = new com.tanks.game.utils.Persistent();
        stage = new Stage();
        mButton1 = new Button(stage, 200, 200,"AI");
        mButton2 = new Button(stage, 200, 500,"ONLINE");

        mButton1.getButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String test = txtUsername.getText();
                HashMap map = new HashMap();
                map.put("playerName", test);
                persistent.saveStrign(map);
                System.out.println(test);
                gsm.set(new OnlinePlayState(gsm, true));
            }
        });
        mButton2.getButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String test = txtUsername.getText();
                HashMap map = new HashMap();
                map.put("playerName", test);
                persistent.saveStrign(map);
                System.out.println(test);
                gsm.set(new OnlinePlayState(gsm, false));
            }
        });

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin.addRegions(new TextureAtlas("uiskin.atlas"));
        txtUsername = new PlayerNameTextField(stage, persistent.LoadString("playerName"), skin);
        txtUsername.setPosition(200, 700);

    }

    private void loadAssets() {
        Assets.getInstance().loadSingleTypeAssetList(
                requiredTextures,
                Texture.class
        );
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
