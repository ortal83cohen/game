package com.tanks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.tanks.game.TanksDemo;
import com.tanks.game.sprites.Bullet;
import com.tanks.game.sprites.Tank;

import org.lwjgl.util.Rectangle;

import java.util.ArrayList;

/**
 * Created by Brent on 7/5/2015.
 */
public class PlayState extends State {

    private static final int GROUND_Y_OFFSET = -50;

    private final TextureRegion bgTextureRegion;

    private final Texture playBtn;

    BitmapFont font = new BitmapFont();

    int ANDROID_WIDTH = Gdx.graphics.getWidth();

    int ANDROID_HEIGHT = Gdx.graphics.getHeight();

    private Tank mTank;

    private Texture bg;

    private Vector2 groundPos1, groundPos2;

    ArrayList<Tank> enemies;

    ArrayList<Bullet> bullets;

    public PlayState(com.tanks.game.states.GameStateManager gsm) {
        super(gsm);

        mTank = new Tank(200, 200);
        enemies = new ArrayList<Tank>();
        bullets = new ArrayList<Bullet>();
        for (int i = 0; i < 20; i++) {
            enemies.add(i, new Tank(100 * i, 100 * i));
        }
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        bg = new Texture("bg.png");
        playBtn = new Texture("button.png");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgTextureRegion = new TextureRegion(bg);
        bgTextureRegion.setRegion(0, 0, bg.getWidth() * 10, bg.getHeight() * 10);
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.isTouched()) {
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            Rectangle textureBounds = new Rectangle((int) (cam.position.x - playBtn.getWidth() / 2),
                    (int) (cam.position.y), playBtn.getWidth(), playBtn.getHeight());

            if (textureBounds.contains(x, y)) {
                System.out.println("Is touched");
            } else {
                mTank.move(x - ANDROID_WIDTH / 2,
                        -(y - ANDROID_HEIGHT / 2));
            }
            if (x % 20 == 0) {
                shoot(x - ANDROID_WIDTH / 2,
                        -(y - ANDROID_HEIGHT / 2));
            }
        }


    }

    @Override
    public void update(float dt) {
        handleInput();

        mTank.update(dt);

        for (int i = 0; i < enemies.size(); i++) {
            Tank enemy = enemies.get(i);
            enemy.move(enemy.directionX, enemy.directionY);

            enemy.update(dt);
        }
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update(dt);
        }
        cam.position.x = mTank.getPosition().x + mTank.getBounds().height / 2;
        cam.position.y = mTank.getPosition().y + mTank.getBounds().width / 2;

        cam.update();

    }

    private void shoot(int directionx,int directiony) {
        Bullet bullet = new Bullet((int) mTank.getPosition().x, (int) mTank.getPosition().y,
                mTank.getRotation(),directionx,directiony);
        bullets.add(bullet);

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bgTextureRegion, 0, 0);
        mTank.getSprite().draw(sb);
        sb.setProjectionMatrix(cam.combined); //or your matrix to draw GAME WORLD, not UI

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).getSprite().draw(sb);
        }
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).getSprite().draw(sb);
        }
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

        //  sb.draw(playBtn, cam.position.x - playBtn.getWidth() / 2, cam.position.y);

        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        mTank.dispose();
        playBtn.dispose();
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).dispose();
        }
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).dispose();
        }

        System.out.println("Play State Disposed");
    }


}
