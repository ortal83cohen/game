package com.tanks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.tanks.game.TanksDemo;
import com.tanks.game.sprites.Tank;

/**
 * Created by Brent on 7/5/2015.
 */
public class PlayState extends State {

    private static final int GROUND_Y_OFFSET = -50;

    private final TextureRegion bgTextureRegion;

    BitmapFont font = new BitmapFont();

    int ANDROID_WIDTH = Gdx.graphics.getWidth();

    int ANDROID_HEIGHT = Gdx.graphics.getHeight();

    private Tank mTank;

    private Texture bg;

    private Vector2 groundPos1, groundPos2;

    public PlayState(com.tanks.game.states.GameStateManager gsm) {
        super(gsm);

        mTank = new Tank(200, 200);
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        bg = new Texture("bg.png");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgTextureRegion = new TextureRegion(bg);
        bgTextureRegion.setRegion(0, 0, bg.getWidth() * 10, bg.getHeight() * 10);
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.isTouched()) {
            mTank.move(Gdx.input.getX() - ANDROID_WIDTH / 2,
                    -(Gdx.input.getY() - ANDROID_HEIGHT / 2));
        }

    }

    @Override
    public void update(float dt) {
        handleInput();

        mTank.update(dt);

        cam.position.x = mTank.getPosition().x + mTank.getBounds().height / 2;
        cam.position.y = mTank.getPosition().y + mTank.getBounds().width / 2;

        cam.update();

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bgTextureRegion, 0, 0);
        mTank.getSprite().draw(sb);
        sb.setProjectionMatrix(cam.combined); //or your matrix to draw GAME WORLD, not UI

//        //draw background, objects, etc.
//        for( View view: views )
//        {
//            view.draw(batch, dt);
//        }

        font.draw(sb, String.valueOf(mTank.getSprite().getRotation()), mTank.getPosition().x - 10,
                mTank.getPosition().y - 10);
        font.draw(sb, String.valueOf(Gdx.input.getX() - ANDROID_WIDTH / 2), cam.position.x,
                cam.position.y - 150);
        font.draw(sb, String.valueOf(Gdx.input.getY() - ANDROID_HEIGHT / 2), cam.position.x,
                cam.position.y - 165);

        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        mTank.dispose();

        System.out.println("Play State Disposed");
    }


}
