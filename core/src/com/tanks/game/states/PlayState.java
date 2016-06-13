package com.tanks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.tanks.game.TanksDemo;
import com.tanks.game.sprites.Bullet;
import com.tanks.game.sprites.Button;
import com.tanks.game.sprites.GameSprite;
import com.tanks.game.sprites.Tank;
import com.tanks.game.utils.BulletPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brent on 7/5/2015.
 */
public class PlayState extends State {

    static public int GAME_WIDTH = 500;

    static public int GAME_HEIGHT = 500;

    private final TextureRegion bgTextureRegion;

    BitmapFont font = new BitmapFont();

    int ANDROID_WIDTH = Gdx.graphics.getWidth();

    int ANDROID_HEIGHT = Gdx.graphics.getHeight();

    ArrayList<Tank> enemies;

    List<Bullet> usedBullets;

    BulletPool bulletPool;

    private Tank mTank;

    private Button mButton;

    private Texture bg;

    public PlayState(com.tanks.game.states.GameStateManager gsm) {
        super(gsm);

        mTank = new Tank(GAME_WIDTH / 2, GAME_HEIGHT / 2, new Texture("tank2.png"));
        mButton = new Button((int) cam.position.x - 100, (int) cam.position.y - 150);
        enemies = new ArrayList<Tank>();
        usedBullets = new ArrayList<Bullet>();
        bulletPool = new BulletPool(
                new Texture("bullet.png"),
                Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg")));
        for (int i = 0; i < 20; i++) {
            enemies.add(i, new Tank((int) (Math.random() * GAME_WIDTH),
                    (int) (Math.random() * GAME_HEIGHT)));
        }
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        bg = new Texture("bg.png");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgTextureRegion = new TextureRegion(bg);
        bgTextureRegion.setRegion(0, 0, GAME_WIDTH + 50, GAME_HEIGHT + 50);
    }

    @Override
    protected void handleInput() {
        Vector3 touchPos = new Vector3();
        if (Gdx.input.isTouched()) {
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            touchPos.set(x, y,
                    0); //when the screen is touched, the coordinates are inserted into the vector
            cam.unproject(touchPos);

            if (mButton.collides(
                    new com.badlogic.gdx.math.Polygon(
                            new float[]{
                                    x, y,
                                    x, y + 20,
                                    x + 20, y + 20,
                                    x + 20, y
                            }))) {

            } else {
                mTank.move(x - ANDROID_WIDTH / 2, -(y - ANDROID_HEIGHT / 2));
            }
            if (x % 20 == 0) {
                shoot(x - ANDROID_WIDTH / 2, -(y - ANDROID_HEIGHT / 2));
            }

        }


    }

    @Override
    public void update(float dt) {
        handleInput();

        mTank.update(dt);

        for (int i = 0; i < enemies.size(); i++) {
            Tank enemy = enemies.get(i);
            if (enemy.getPosition().x < 0) {
                enemy.directionX = Math.abs(enemy.directionX);
            } else if (enemy.getPosition().x > GAME_WIDTH) {
                enemy.directionX = -Math.abs(enemy.directionX);
            } else if (enemy.getPosition().y < 0) {
                enemy.directionY = Math.abs(enemy.directionY);
            } else if (enemy.getPosition().y > GAME_HEIGHT) {
                enemy.directionY = -Math.abs(enemy.directionY);
            }
            enemy.update(dt);
        }
        for (int i = 0; i < usedBullets.size(); i++) {
            Bullet bullet = usedBullets.get(i);

            if (isOutOfScreen(bullet)) {
                usedBullets.remove(i);
                bulletPool.free(bullet);
            } else {
                bullet.update(dt);
                for (int j = 0; j < enemies.size(); j++) {
                    Tank enemy = enemies.get(j);
                    if (bullet.collides(enemy.getBoundsPolygon())) {
                        enemies.remove(j);
                        usedBullets.remove(i);
                        bulletPool.free(bullet);
                    }
                }

            }

        }
        cam.position.x = mTank.getPosition().x
                + mTank.getBoundsPolygon().getBoundingRectangle().height / 2;
        cam.position.y = mTank.getPosition().y
                + mTank.getBoundsPolygon().getBoundingRectangle().width / 2;

        mButton.setPosition(cam.position.x - 100, cam.position.y - 170);
        mButton.update(dt);
        cam.update();

    }

    private boolean isOutOfScreen(GameSprite gameSprite) {
        return cam.position.x - (cam.viewportWidth / 2) > gameSprite.getPosition().x + gameSprite
                .getSprite().getWidth() ||
                cam.position.x + (cam.viewportWidth / 2) < gameSprite.getPosition().x ||
                cam.position.y - (cam.viewportHeight / 2) > gameSprite.getPosition().y + gameSprite
                        .getSprite().getWidth() ||
                cam.position.y + (cam.viewportHeight / 2) < gameSprite.getPosition().y;
    }

    private void shoot(int directionx, int directiony) {
        if (usedBullets.size() < 5) {
            Bullet bullet = bulletPool.obtainAndFire("Player", (int) mTank.getPosition().x,
                    (int) mTank.getPosition().y,
                    mTank.getRotation(), directionx, directiony);
            usedBullets.add(bullet);
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bgTextureRegion, 0, 0);
        mTank.getSprite().draw(sb);
        mButton.getSprite().draw(sb);

        sb.setProjectionMatrix(cam.combined); //or your matrix to draw GAME WORLD, not UI

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).getSprite().draw(sb);
        }
        for (int i = 0; i < usedBullets.size(); i++) {
            usedBullets.get(i).getSprite().draw(sb);
        }

//        font.draw(sb, String.valueOf(mTank.getSprite().getRotation()), mTank.getPosition().x - 10,
//                mTank.getPosition().y - 10);
//        font.draw(sb, String.valueOf(Gdx.input.getX() - ANDROID_WIDTH / 2), cam.position.x,
//                cam.position.y - 150);
//        font.draw(sb, String.valueOf(Gdx.input.getY() - ANDROID_HEIGHT / 2), cam.position.x,
//                cam.position.y - 165);
        font.draw(sb, "enemies " + enemies.size(), cam.position.x - 35,
                cam.position.y - 175);

        sb.end();
    }

    @Override
    public void render(ShapeRenderer sr) {
        sr.setProjectionMatrix(cam.combined);
        sr.setAutoShapeType(true);
        sr.begin();
        sr.setColor(Color.BLACK);
        for (int i = 0; i < enemies.size(); i++) {
            sr.polygon(enemies.get(i).getBoundsPolygon().getTransformedVertices());
        }
        for (int i = 0; i < usedBullets.size(); i++) {
            sr.polygon(usedBullets.get(i).getBoundsPolygon().getTransformedVertices());
        }
        sr.polygon(mButton.getBoundsPolygon().getTransformedVertices());
        sr.polygon(mTank.getBoundsPolygon().getTransformedVertices());
        sr.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        mTank.dispose();
        mButton.dispose();
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).dispose();
        }
        bulletPool.dispose();

    }


}
