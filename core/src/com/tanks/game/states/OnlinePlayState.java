package com.tanks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tanks.game.TanksDemo;
import com.tanks.game.sprites.Bullet1;
import com.tanks.game.sprites.Button;
import com.tanks.game.sprites.GameSprite;
import com.tanks.game.sprites.Tank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Brent on 7/5/2015.
 */
public class OnlinePlayState extends State {

    static public int GAME_WIDTH = 500;

    static public int GAME_HEIGHT = 500;

    private final TextureRegion bgTextureRegion;

    private final Texture tankTexture;

    BitmapFont font = new BitmapFont();

    int ANDROID_WIDTH = Gdx.graphics.getWidth();

    int ANDROID_HEIGHT = Gdx.graphics.getHeight();

    HashMap<String, Tank> enemies;

    ArrayList<Bullet1> mBullet1s;

    private Socket socket;

    private Tank player;

    private Button mButton;

    private Texture bg;

    private String myId;

    public OnlinePlayState(GameStateManager gsm) {
        super(gsm);
        connectSocket();
        configSocketEvents();

        mButton = new Button((int) cam.position.x - 100, (int) cam.position.y - 150);
        enemies = new HashMap<String, Tank>();
        mBullet1s = new ArrayList<Bullet1>();
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        bg = new Texture("bg.png");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgTextureRegion = new TextureRegion(bg);
        bgTextureRegion.setRegion(0, 0, GAME_WIDTH + 50, GAME_HEIGHT + 50);
        tankTexture = new Texture("tank.png");
    }

    public void connectSocket() {
        try {
            socket = IO.socket("http://localhost:8080");
            socket.connect();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connected");



                player = new Tank((int) (Math.random() * GAME_WIDTH),
                        (int) (Math.random() * GAME_HEIGHT), tankTexture);
            }
        }).on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {

                    myId = data.getString("id");
                    Gdx.app.log("SocketIO", "My ID: " + myId);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting ID");
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    myId = data.getString("id");
                    Gdx.app.log("SocketIO", "New Player Connect: " + myId);
                    enemies.put(myId, new Tank(0, 0,tankTexture));

                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting New PlayerID");
                }
            }
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    myId = data.getString("id");
                    enemies.remove(myId);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray objects = (JSONArray) args[0];
                try {
                    for (int i = 0; i < objects.length(); i++) {
                        Tank enemy = new Tank(0, 0,tankTexture);
                        Vector2 position = new Vector2();
                        position.x = ((Double) objects.getJSONObject(i).getDouble("x"))
                                .floatValue();
                        position.y = ((Double) objects.getJSONObject(i).getDouble("y"))
                                .floatValue();
                        enemy.setPosition(position);

                        enemies.put(objects.getJSONObject(i).getString("myId"), enemy);
                    }
                } catch (JSONException e) {

                }
            }
        });
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
                if(player!=null) {
                    player.move(x - ANDROID_WIDTH / 2, -(y - ANDROID_HEIGHT / 2));
                }
            }
            if (x % 20 == 0) {
                shoot(x - ANDROID_WIDTH / 2, -(y - ANDROID_HEIGHT / 2));
            }

        }


    }

    @Override
    public void update(float dt) {
        handleInput();
if(player!=null) {
    player.update(dt);
}
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
            enemy.move(enemy.directionX, enemy.directionY);

            enemy.update(dt);
        }
        for (int i = 0; i < mBullet1s.size(); i++) {
            Bullet1 bullet1 = mBullet1s.get(i);

            if (isOurOfScreen(bullet1)) {
                mBullet1s.remove(i);
            } else {
                bullet1.update(dt);
                for (int j = 0; j < enemies.size(); j++) {
                    Tank enemy = enemies.get(j);
                    if (bullet1.collides(enemy.getBoundsPolygon())) {
                        enemies.remove(j);
                        mBullet1s.remove(i);
                    }
                }

            }

        }
        if(player!=null) {
            cam.position.x = player.getPosition().x
                    + player.getBoundsPolygon().getBoundingRectangle().height / 2;
            cam.position.y = player.getPosition().y
                    + player.getBoundsPolygon().getBoundingRectangle().width / 2;
        }
        mButton.setPosition(cam.position.x - 100, cam.position.y - 170);
        mButton.update(dt);
        cam.update();

    }

    private boolean isOurOfScreen(GameSprite gameSprite) {
        return cam.position.x - (cam.viewportWidth / 2) > gameSprite.getPosition().x + gameSprite
                .getSprite().getWidth() ||
                cam.position.x + (cam.viewportWidth / 2) < gameSprite.getPosition().x ||
                cam.position.y - (cam.viewportHeight / 2) > gameSprite.getPosition().y + gameSprite
                        .getSprite().getWidth() ||
                cam.position.y + (cam.viewportHeight / 2) < gameSprite.getPosition().y;
    }

    private void shoot(int directionx, int directiony) {
        if(player!=null) {
            if (mBullet1s.size() < 5) {
                Bullet1 bullet1 = new Bullet1((int) player.getPosition().x,
                        (int) player.getPosition().y,
                        player.getRotation(), directionx, directiony);
                mBullet1s.add(bullet1);
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bgTextureRegion, 0, 0);
        if(player!=null) {
            player.getSprite().draw(sb);
        }
        mButton.getSprite().draw(sb);

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).getSprite().draw(sb);
        }
        for (int i = 0; i < mBullet1s.size(); i++) {
            mBullet1s.get(i).getSprite().draw(sb);
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
        for (int i = 0; i < mBullet1s.size(); i++) {
            sr.polygon(mBullet1s.get(i).getBoundsPolygon().getTransformedVertices());
        }
        sr.polygon(mButton.getBoundsPolygon().getTransformedVertices());
        if(player!=null) {
            sr.polygon(player.getBoundsPolygon().getTransformedVertices());
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
        for (int i = 0; i < mBullet1s.size(); i++) {
            mBullet1s.get(i).dispose();
        }

    }


}
