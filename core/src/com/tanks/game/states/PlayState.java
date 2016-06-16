package com.tanks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.tanks.game.TanksDemo;
import com.tanks.game.sprites.Bullet;
import com.tanks.game.sprites.Button;
import com.tanks.game.sprites.Enemy;
import com.tanks.game.sprites.Entity;
import com.tanks.game.sprites.Player;
import com.tanks.game.sprites.SmartPlayer;
import com.tanks.game.sprites.Tank;
import com.tanks.game.sprites.Wall;
import com.tanks.game.utils.BulletPool;
import com.tanks.game.utils.CollisionManager;
import com.tanks.game.utils.Collisionable;
import com.tanks.game.utils.NaiveCollisionManager;
import com.tanks.game.utils.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brent on 7/5/2015.
 */
public class PlayState extends State {

    static public int GAME_WIDTH = 400;

    static public int GAME_HEIGHT = 400;

    private final TextureRegion bgTextureRegion;

    BitmapFont font = new BitmapFont();

    int ANDROID_WIDTH = Gdx.graphics.getWidth();

    int ANDROID_HEIGHT = Gdx.graphics.getHeight();

    ArrayList<Tank> enemies;

    List<Bullet> usedBullets;

    BulletPool bulletPool;

    private NaiveCollisionManager collisionManager;

    private float timer = 0;

    private Player player;

    private Button mButton;

    private Texture bg;

    private float lastShoot = 0;

    private ArrayList<Wall> walls;

    public PlayState(com.tanks.game.states.GameStateManager gsm) {
        super(gsm);
        configCollisionManager();
        player = new Player(GAME_WIDTH / 2, GAME_HEIGHT / 2, collisionManager);
        mButton = new Button((int) cam.position.x - 100, (int) cam.position.y - 150,
                collisionManager);
        enemies = new ArrayList<Tank>();
        usedBullets = new ArrayList<Bullet>();
        bulletPool = new BulletPool(new Texture("bullet.png"),
                Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg")), collisionManager);
        for (int i = 0; i < 10; i++) {
            enemies.add(i, new SmartPlayer((int) (Math.random() * GAME_WIDTH),
                    (int) (Math.random() * GAME_HEIGHT), collisionManager));
        }
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        bg = new Texture("bg.png");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgTextureRegion = new TextureRegion(bg);
        bgTextureRegion.setRegion(0, 0, GAME_WIDTH, GAME_HEIGHT);
    }

    private void configCollisionManager() {
        collisionManager = new NaiveCollisionManager();
        walls = new ArrayList<Wall>();
        walls.add(new Wall(collisionManager, Type.TOP_WALL,
                new Polygon(new float[]{0, GAME_HEIGHT, GAME_WIDTH, GAME_WIDTH, 0, GAME_HEIGHT + 1, GAME_WIDTH, GAME_WIDTH + 1})));
        walls.add(new Wall(collisionManager, Type.BOTTOM_WALL, new Polygon(new float[]{0, 0, GAME_WIDTH, 0, -1, 0, GAME_WIDTH - 1, 0})));
        walls.add(new Wall(collisionManager, Type.LEFT_WALL, new Polygon(new float[]{0, 0, 0, GAME_HEIGHT, -1, GAME_HEIGHT, -1, 0})));
        walls.add(new Wall(collisionManager, Type.RIGHT_WALL,
                new Polygon(new float[]{GAME_WIDTH, 0, GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH + 1, GAME_HEIGHT, GAME_WIDTH + 1, 0})));
        collisionManager.AddCallback(Type.PLAYER, new CollisionManager.CollisionManagerCallBack() {
            @Override
            public void collide(Collisionable c1, Collisionable c2) {

            }
        });
    }

    @Override
    protected void handleInput() {
        Vector3 touchPos = new Vector3();
        if (Gdx.input.isTouched(0)) {
            int x = Gdx.input.getX(0);
            int y = Gdx.input.getY(0);
            touchPos.set(x, y, 0);
            cam.unproject(touchPos);

            if (player != null) {
                player.move(x - ANDROID_WIDTH / 2, -(y - ANDROID_HEIGHT / 2));
            }
        }
        if (Gdx.input.isTouched(1) && lastShoot + 0.3 < timer) {
            lastShoot = timer;
            int x = Gdx.input.getX(1);
            int y = Gdx.input.getY(1);
            touchPos.set(x, y, 0);
            cam.unproject(touchPos);
            if (mButton.pressed(
                    new com.badlogic.gdx.math.Polygon(
                            new float[]{
                                    touchPos.x - 10, touchPos.y - 10,
                                    touchPos.x - 10, touchPos.y + 10,
                                    touchPos.x + 10, touchPos.y + 10,
                                    touchPos.x + 10, touchPos.y - 10
                            }))) {
                shoot(player.getPosition().x, player.getPosition().y, player.getRotation(),
                        player.directionX, player.directionY);

            }
        }
    }

    @Override
    public void update(float dt) {
        timer += dt;
        handleInput();

        player.update(dt);

        for (int i = 0; i < enemies.size(); i++) {
            Tank enemy = enemies.get(i);
            if(!enemy.update(dt)){
                enemies.remove(i);
            }
        }
        for (int i = 0; i < usedBullets.size(); i++) {
            Bullet bullet = usedBullets.get(i);

            if ( !bullet.update(dt)) {
                usedBullets.remove(i);
                bulletPool.free(bullet);
            }
//            else {
//               ;
//                for (int j = 0; j < enemies.size(); j++) {
////                    Tank enemy = enemies.get(j);
////                    if (bullet.collides(enemy.getBoundsPolygon())) {
//////                        enemies.remove(j);
//////                        usedBullets.remove(i);
//////                        bulletPool.free(bullet);
////                    }
//                }
//
//            }

        }
        cam.position.x = player.getPosition().x
                + player.getBoundsPolygon().getBoundingRectangle().height / 2;
        cam.position.y = player.getPosition().y
                + player.getBoundsPolygon().getBoundingRectangle().width / 2;

        mButton.setPosition(cam.position.x - 100, cam.position.y - 170);
        mButton.update(dt);
        cam.update();

    }

    private void shoot(float x, float y, float rotation, float directionX, float directionY) {
        if (usedBullets.size() < 5) {
            Bullet bullet = bulletPool.obtainAndFire("Player", (int) x, (int) y,
                    rotation, directionX, directionY);
            usedBullets.add(bullet);
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bgTextureRegion, 0, 0);
        player.getSprite().draw(sb);
        mButton.getSprite().draw(sb);

        sb.setProjectionMatrix(cam.combined); //or your matrix to draw GAME WORLD, not UI

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).getSprite().draw(sb);
        }
        for (int i = 0; i < usedBullets.size(); i++) {
            usedBullets.get(i).getSprite().draw(sb);
        }

//        font.draw(sb, String.valueOf(player.getSprite().getRotation()), player.getPosition().x - 10,
//                player.getPosition().y - 10);
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
        sr.polygon(player.getBoundsPolygon().getTransformedVertices());
        sr.setColor(Color.GREEN);
        for (int i = 0; i < walls.size(); i++) {
            sr.polygon(walls.get(i).getBoundsPolygon().getTransformedVertices());
        }
        sr.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        player.dispose();
        mButton.dispose();
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).dispose();
        }
        for (int i = 0; i < walls.size(); i++) {
            walls.get(i).dispose();
        }
        bulletPool.dispose();

    }

}
