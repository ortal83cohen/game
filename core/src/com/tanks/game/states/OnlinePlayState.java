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
import com.tanks.game.sprites.Bullet;
import com.tanks.game.sprites.Button;
import com.tanks.game.sprites.GameSprite;
import com.tanks.game.sprites.Tank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Brent on 7/5/2015.
 */
public class OnlinePlayState extends State {

    private static final float UPDATE_TIME = 1 / 40;

    static public int GAME_WIDTH = 300;

    static public int GAME_HEIGHT = 300;

    private final TextureRegion bgTextureRegion;

    private final Texture tankTexture;
    private final Texture bulletTexture;

    BitmapFont font = new BitmapFont();

    int ANDROID_WIDTH = Gdx.graphics.getWidth();

    int ANDROID_HEIGHT = Gdx.graphics.getHeight();

    HashMap<String, Tank> enemies;

    ArrayList<Bullet> mMyBullets;
    ArrayList<Bullet> mEnemyBullets;

    private Socket socket;

    private Tank player;

    private Button mButton;

    private Texture bg;

    private String myId;

    private float timer = 0;

    public OnlinePlayState(GameStateManager gsm) {
        super(gsm);
        connectSocket();
        configSocketEvents();

        mButton = new Button((int) cam.position.x - 100, (int) cam.position.y - 150);
        enemies = new HashMap<String, Tank>();
        mMyBullets = new ArrayList<Bullet>();
        mEnemyBullets = new ArrayList<Bullet>();
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        bg = new Texture("bg.png");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgTextureRegion = new TextureRegion(bg);
        bgTextureRegion.setRegion(0, 0, GAME_WIDTH + 50, GAME_HEIGHT + 50);
        tankTexture = new Texture("tank2.png");
        bulletTexture = new Texture("bullet.png");
    }

    public void playerMoved(float dt) {
        timer += dt;
        if (timer >= UPDATE_TIME && player != null) {
            JSONObject data = new JSONObject();
            try {
                data.put("x", player.getPosition().x);
                data.put("y", player.getPosition().y);
                socket.emit("playerMoved", data);
            } catch (JSONException e) {
                Gdx.app.log("SocketIO", "Error sending update data");
            }
        }
    }

    public void connectSocket() {
        try {
//            socket = IO.socket("http://localhost:8080");
            socket = IO.socket("http://ec2-52-37-181-55.us-west-2.compute.amazonaws.com:8080");
            socket.connect();
        } catch (Exception e) {
            Gdx.app.log("SocketIO", "Error");
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
                    String id = data.getString("id");
                    Gdx.app.log("SocketIO", "New Player Connect: " + myId);
                    enemies.put(id, new Tank(0, 0, tankTexture));

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
        }).on("playerHit", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    if (id.equals(myId)) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                gsm.set(new MenuState(gsm));
                            }
                        });

                    } else{
                        enemies.remove(id);
                    }
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error PlayerHit");
                }
            }
        }).on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String enemyId = data.getString("id");
                    double x = data.getDouble("x");
                    double y = data.getDouble("y");
                    if (enemies.containsKey(enemyId)) {
                        enemies.get(enemyId).setPosition(new Vector2((float) x, (float) y));
                    }
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
                }
            }
        }).on("playerSHoot", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject data = (JSONObject) args[0];
                try {
                    String enemyId = data.getString("id");
                    int x = data.getInt("x");
                    int y = data.getInt("y");
                    double rotation = data.getDouble("rotation");
                    int directionx = data.getInt("directionx");
                    int directiony= data.getInt("directiony");
                    Bullet bullet = new Bullet(x,
                           y,
                            (float)rotation, directionx, directiony,bulletTexture);
                    mEnemyBullets.add(bullet);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray objects = (JSONArray) args[0];

                try {
                    Gdx.app.log("SocketIO", "Get Players: " + objects.length());
                    for (int i = 0; i < objects.length(); i++) {
                        Tank enemy = new Tank(0, 0, tankTexture);
                        Vector2 position = new Vector2();
                        position.x = ((Double) objects.getJSONObject(i).getDouble("x"))
                                .floatValue();
                        position.y = ((Double) objects.getJSONObject(i).getDouble("y"))
                                .floatValue();
                        enemy.setPosition(position);

                        enemies.put(objects.getJSONObject(i).getString("id"), enemy);
                    }
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error Get Players");
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
                if (player != null) {
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
        playerMoved(dt);
        handleInput();
        if (player != null) {
            player.update(dt);
        }

        for (Map.Entry<String, Tank> entry : enemies.entrySet()) {
            Tank enemy = entry.getValue();
            enemy.update(dt);
        }
        for (int i = 0; i < mEnemyBullets.size(); i++) {
            Bullet bullet = mEnemyBullets.get(i);
            bullet.update(dt);
        }
        for (int i = 0; i < mMyBullets.size(); i++) {
            Bullet bullet = mMyBullets.get(i);

            if (isOurOfScreen(bullet)) {
                mMyBullets.remove(i);
            } else {
                bullet.update(dt);
                for (Map.Entry<String, Tank> entry : enemies.entrySet()) {

                    if (bullet.collides(entry.getValue().getBoundsPolygon())) {
                        JSONObject data = new JSONObject();
                        try {
                            data.put("id", entry.getKey());
                            socket.emit("playerHit", data);
                        } catch (JSONException e) {
                            Gdx.app.log("SocketIO", "Error sending update data");
                        }
                        enemies.remove(entry.getKey());
                        mMyBullets.remove(i);
                    }
                }

            }

        }
        if (player != null) {
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
        if (player != null) {
            if (mMyBullets.size() < 5) {
                Bullet bullet = new Bullet((int) player.getPosition().x,
                        (int) player.getPosition().y,
                        player.getRotation(), directionx, directiony,bulletTexture);
                mMyBullets.add(bullet);

                JSONObject data = new JSONObject();
                try {
                    data.put("x", player.getPosition().x);
                    data.put("y", player.getPosition().y);
                    data.put("rotation", player.getRotation());
                    data.put("directionx", directionx);
                    data.put("directiony", directiony);
                    socket.emit("playerSHoot", data);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error sending update data");
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bgTextureRegion, 0, 0);
        if (player != null) {
            player.getSprite().draw(sb);
        }
        mButton.getSprite().draw(sb);

        for (Map.Entry<String, Tank> entry : enemies.entrySet()) {
            entry.getValue().getSprite().draw(sb);
        }
        for (int i = 0; i < mMyBullets.size(); i++) {
            mMyBullets.get(i).getSprite().draw(sb);
        }
        for (int i = 0; i < mEnemyBullets.size(); i++) {
            mEnemyBullets.get(i).getSprite().draw(sb);
        }

//        font.draw(sb, String.valueOf(player.getSprite().getRotation()), player.getPosition().x - 10,
//                player.getPosition().y - 10);
//        font.draw(sb, String.valueOf(Gdx.input.getX() - ANDROID_WIDTH / 2), cam.position.x,
//                cam.position.y - 150);
//        font.draw(sb, String.valueOf(Gdx.input.getY() - ANDROID_HEIGHT / 2), cam.position.x,
//                cam.position.y - 165);
        font.draw(sb, "enemies " + enemies.size(), cam.position.x - 35, cam.position.y - 170);
        font.draw(sb, "my id  " + myId, cam.position.x - 100, cam.position.y - 185);

        sb.end();
    }

    @Override
    public void render(ShapeRenderer sr) {
        sr.setProjectionMatrix(cam.combined);
        sr.setAutoShapeType(true);
        sr.begin();
        sr.setColor(Color.BLACK);
        for (Map.Entry<String, Tank> entry : enemies.entrySet()) {
            sr.polygon(entry.getValue().getBoundsPolygon().getTransformedVertices());
        }
        for (int i = 0; i < mMyBullets.size(); i++) {
            sr.polygon(mMyBullets.get(i).getBoundsPolygon().getTransformedVertices());
        }
        for (int i = 0; i < mEnemyBullets.size(); i++) {
            sr.polygon(mEnemyBullets.get(i).getBoundsPolygon().getTransformedVertices());
        }
        sr.polygon(mButton.getBoundsPolygon().getTransformedVertices());
        if (player != null) {
            sr.polygon(player.getBoundsPolygon().getTransformedVertices());
        }
        sr.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        player.dispose();
        mButton.dispose();
        for (Map.Entry<String, Tank> entry : enemies.entrySet()) {
            entry.getValue().dispose();
        }
        for (int i = 0; i < mMyBullets.size(); i++) {
            mMyBullets.get(i).dispose();
        }
        for (int i = 0; i < mEnemyBullets.size(); i++) {
            mEnemyBullets.get(i).dispose();
        }

    }


}
