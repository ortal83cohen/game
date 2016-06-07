package com.tanks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.tanks.game.TanksDemo;
import com.tanks.game.sprites.Button;

/**
 * Created by Brent on 6/26/2015.
 */
public class MenuState extends State {

    private final Polygon tutchPolygon;

    private Texture background;


    private Button mButton1;
    private Button mButton2;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        background = new Texture("bg.png");
//        playBtn = new Texture("button.png");
        mButton1 = new Button((int) cam.position.x , (int) cam.position.y);
        mButton2 = new Button((int) cam.position.x , (int) cam.position.y );
         tutchPolygon = new com.badlogic.gdx.math.Polygon(
                 new float[]{
                      0, 0,
                         0,0,
                         0, 0,
                        0,0
                 }
        );
    }

    @Override
    public void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            touchPos.set(x, y,
                    0); //when the screen is touched, the coordinates are inserted into the vector
            cam.unproject(touchPos);
            tutchPolygon.setVertices( new float[]{
                    x, y,
                    x, y + 20,
                    x + 20, y + 20,
                    x + 20, y
            });

//            if (mButton1.collides(tutchPolygon)) {
//                gsm.set(new PlayState(gsm));
//            }
//            if (mButton2.collides(tutchPolygon)) {
//                gsm.set(new OnlinePlayState(gsm));
//            }
            gsm.set(new OnlinePlayState(gsm));

        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        cam.update();
        mButton1.setPosition(cam.position.x , cam.position.y );
        mButton2.setPosition(cam.position.x , cam.position.y );
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0, 0);
        mButton1.getSprite().draw(sb);
        mButton2.getSprite().draw(sb);
        sb.end();
    }

    @Override
    public void render(ShapeRenderer sr) {
        sr.setProjectionMatrix(cam.combined);
        sr.setAutoShapeType(true);
        sr.begin();
        sr.setColor(Color.BLACK);
        sr.polygon(mButton1.getBoundsPolygon().getTransformedVertices());
        sr.polygon(mButton2.getBoundsPolygon().getTransformedVertices());
        sr.polygon(tutchPolygon.getTransformedVertices());

        sr.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        mButton1.dispose();
        mButton2.dispose();
        System.out.println("Menu State Disposed");
    }
}
