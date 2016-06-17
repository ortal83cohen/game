package com.tanks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.tanks.game.TanksDemo;
import com.tanks.game.sprites.Bullet;
import com.tanks.game.sprites.Button;
import com.tanks.game.sprites.Enemy;
import com.tanks.game.sprites.Player;
import com.tanks.game.sprites.SmartPlayer;
import com.tanks.game.sprites.Tank;
import com.tanks.game.sprites.Wall;
import com.tanks.game.utils.BulletPool;
import com.tanks.game.utils.CollisionManager;
import com.tanks.game.utils.NaiveCollisionManager;
import com.tanks.game.utils.Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Brent on 7/5/2015.
 */
public class OnlinePlayState extends State {

    private static final float UPDATE_TIME = 1 / 30f;

    static public int GAME_WIDTH = 400;

    static public int GAME_HEIGHT = 400;

    private final TextureRegion bgTextureRegion;

    private final Texture bulletTexture;

    private final com.tanks.game.utils.Persistent persistent;

    BitmapFont font;

    int ANDROID_WIDTH = Gdx.graphics.getWidth();

    int ANDROID_HEIGHT = Gdx.graphics.getHeight();

    HashMap<String, Enemy> liveEnemies;

    ArrayList<Tank> enemies = new ArrayList<Tank>();

    BulletPool bulletPool;

    List<Bullet> bullets;

    private CollisionManager collisionManager;

    private Socket socket;

    private Player player;

    private Button mButton;

    private Texture bg;

    private String myId;

    private float updateTimeLoopTimer = 0;

    private float lastConnectionSpeed = 0;

    private float timer = 0;

    private int skipCounter = 0;

    private float lastUpdate = 0;

    private float lastShoot;

    private float connectionDelay = 0;

    private ArrayList<Wall> walls;

    public OnlinePlayState(GameStateManager gsm, boolean addSmartPlayers) {
        super(gsm);
        configCollisionManager();
        connectSocket();
        configSocketEvents();

        mButton = new Button((int) cam.position.x - 100, (int) cam.position.y - 150,
                collisionManager);
        liveEnemies = new HashMap<String, Enemy>();
        bullets = new ArrayList<Bullet>();
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        bg = new Texture("bg.png");

        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgTextureRegion = new TextureRegion(bg);
        bgTextureRegion.setRegion(0, 0, GAME_WIDTH, GAME_HEIGHT);
        bulletTexture = new Texture("bullet.png");
        bulletPool = new BulletPool(bulletTexture,
                Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg")), collisionManager);

        persistent = new com.tanks.game.utils.Persistent();

        player = new Player((int) (Math.random() * GAME_WIDTH),
                (int) (Math.random() * GAME_HEIGHT), collisionManager);

        if (addSmartPlayers) {
            for (int i = 0; i < 10; i++) {
                enemies.add(i, new SmartPlayer((int) (Math.random() * GAME_WIDTH),
                        (int) (Math.random() * GAME_HEIGHT), collisionManager));
            }
        }
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
    }

    public void playerMoved(float dt) {
        if (updateTimeLoopTimer >= UPDATE_TIME && player != null && player.hasMoved()) {
            updateTimeLoopTimer = 0;
            JSONObject data = new JSONObject();
            try {
                data.put("x", player.getPosition().x);
                data.put("y", player.getPosition().y);
                data.put("dx", player.directionX);
                data.put("dy", player.directionY);
                data.put("s", player.getSpeed());
                socket.emit("playerMoved", data);
            } catch (JSONException e) {
                Gdx.app.log("SocketIO", "Error sending update data");
            }
        }
    }

    public void connectSocket() {
        try {
//            socket = IO.socket("http://localhost:8080");
            socket = IO.socket("http://104.155.63.29:9000");
//            socket = IO.socket("http://ec2-52-58-247-221.eu-central-1.compute.amazonaws.com:9000");

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
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = new JSONObject();
                        try {
                            data.put("x", player.getPosition().x);
                            data.put("y", player.getPosition().y);
                            socket.emit("newPlayer", data);
                        } catch (JSONException e) {
                            Gdx.app.log("SocketIO", "Error sending update data");
                        }
                    }
                });


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
                final JSONObject data = (JSONObject) args[0];
                try {
                    final String id = data.getString("id");
                    Gdx.app.log("SocketIO", "New Player Connect: " + id);
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                liveEnemies.put(id, new Enemy(data.getInt("x"), data.getInt("y"), collisionManager));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Vibrator)) {
                        Gdx.input.vibrate(new long[]{0, 2, 10, 2, 10, 2}, -1);
                    }
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting New PlayerID");
                }
            }
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    liveEnemies.remove(id);
                    Gdx.app.log("SocketIO", "player Disconnected: " + id);
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
                        HashMap map = new HashMap();
                        map.put("killed1", persistent.LoadInt("killed1") + 1);
                        persistent.saveInt(map);

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                gsm.set(new MenuState(gsm));
                            }
                        });

                    } else {
                        liveEnemies.remove(id);
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
                    if (lastUpdate == timer) {
                        Gdx.app.log("SocketIO", "SKIPED " + (skipCounter++) + " at " + timer);
//                        return;
                    }
                    lastUpdate = timer;
                    String enemyId = data.getString("id");
                    double x = data.getDouble("x");
                    double y = data.getDouble("y");
                    float dx = (float) data.getDouble("dx");
                    float dy = (float) data.getDouble("dy");
                    float s = (float) data.getDouble("s");
                    if (liveEnemies.containsKey(enemyId)) {
                        liveEnemies.get(enemyId).setPosition(new Vector2((float) x, (float) y));
                        liveEnemies.get(enemyId).move(dx, dy, s - 0.5f);
                        Gdx.app.log("SocketIO", "playerMoved x" + x + " y" + y + " s" + s);
                    }
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
                }
            }
        }).on("playerShoot", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                final JSONObject data = (JSONObject) args[0];
                try {
                    final String enemyId = data.getString("id");
                    final int x = data.getInt("x");
                    final int y = data.getInt("y");
                    final double rotation = data.getDouble("rotation");
                    final float directionX = (float) data.getDouble("directionX");
                    final float directionY = (float) data.getDouble("directionY");

                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Bullet bullet = bulletPool.obtainAndFire(enemyId, x, y,
                                    (float) rotation, directionX, directionY);
                            bullets.add(bullet);
                        }
                    });
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error playerShoot");
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONArray objects = (JSONArray) args[0];

                try {
                    Gdx.app.log("SocketIO", "Get Players: " + objects.length());
                    for (int i = 0; i < objects.length(); i++) {
                        final JSONObject object = objects.getJSONObject(i);
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    liveEnemies.put(object.getString("id"), new Enemy(object.getInt("x"),
                                            object.getInt("y"), collisionManager));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                    if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Vibrator)) {
                        Gdx.input.vibrate(new long[]{0, 2, 10, 2, 10, 2}, -1);
                    }
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error Get Players");
                }
            }
        }).on("connectionTest", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    float t = (float) data.getDouble("t");
                    connectionDelay = (connectionDelay + (timer - t)) / 2;
                    Gdx.app.log("SocketIO", "connectionDelay - " + connectionDelay);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error Get connectionTest");
                }
            }
        });
    }

    @Override
    protected void handleInput() {
        Vector3 touchPos = new Vector3();
        if (Gdx.input.isTouched(0)) {
            final int x = Gdx.input.getX(0);
            final int y = Gdx.input.getY(0);
            touchPos.set(x, y, 0);
            cam.unproject(touchPos);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    player.move(x - ANDROID_WIDTH / 2, -(y - ANDROID_HEIGHT / 2));
                }
            }, connectionDelay);
        }
        if ((Gdx.input.isTouched(1) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) && lastShoot + 0.3 < timer) {
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
                            })) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        shoot(player.getPosition().x, player.getPosition().y, player.getRotation(),
                                player.directionX, player.directionY);
                    }
                }, connectionDelay);
            }
        }

    }

    @Override
    public void update(float dt) {
        updateTimeLoopTimer += dt;
        timer += dt;
        if (player != null) {
            handleInput();
            player.update(dt);
            playerMoved(dt);
        }

        for (Map.Entry<String, Enemy> entry : liveEnemies.entrySet()) {
            if (!entry.getValue().update(dt)) {
                JSONObject data = new JSONObject();
                try {
                    data.put("id", entry.getKey());
                    socket.emit("playerHit", data);
                    HashMap map = new HashMap();
                    map.put("kill1", persistent.LoadInt("kill1") + 1);
                    persistent.saveInt(map);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error sending playerHit data");
                }
                entry.getValue().dispose();
                liveEnemies.remove(entry.getKey());
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            Tank enemy = enemies.get(i);
            if (!enemy.update(dt)) {
                enemies.remove(i);
            }
        }

        for (int i = 0; i < bullets.size(); i++) {
            if (!bullets.get(i).update(dt)) {
                bulletPool.free(bullets.get(i));
                bullets.remove(i);
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

        if (lastConnectionSpeed + 3 < timer && player != null && player.hasMoved()) {
            lastConnectionSpeed = timer;
            JSONObject data = new JSONObject();
            try {
                data.put("t", timer);
                socket.emit("connectionTest", data);
            } catch (Exception e) {
                Gdx.app.log("SocketIO", "Error sending connectionTest data");
            }
        }


    }


    private void shoot(float x, float y, float rotation, float directionX, float directionY) {
        if (player != null) {
            if (bullets.size() < 5) {
                Bullet bullet = bulletPool.obtainAndFire("Player", (int) x, (int) y,
                        player.getRotation(), directionX, directionY);
                bullets.add(bullet);

                JSONObject data = new JSONObject();
                try {
                    data.put("x", x);
                    data.put("y", y);
                    data.put("rotation", rotation);
                    data.put("directionX", directionX);
                    data.put("directionY", directionY);
                    socket.emit("playerShoot", data);
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

        for (Map.Entry<String, Enemy> entry : liveEnemies.entrySet()) {
            entry.getValue().getSprite().draw(sb);
        }
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).getSprite().draw(sb);
        }
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).getSprite().draw(sb);
        }
//        font.draw(sb, String.valueOf(player.getSprite().getRotation()), player.getPosition().x - 10,
//                player.getPosition().y - 10);
//        font.draw(sb, String.valueOf(Gdx.input.getX() - ANDROID_WIDTH / 2), cam.position.x,
//                cam.position.y - 150);
//        font.draw(sb, String.valueOf(Gdx.input.getY() - ANDROID_HEIGHT / 2), cam.position.x,
//                cam.position.y - 165);
        font.draw(sb, "kill     -" + persistent.LoadInt("kill1"), cam.position.x - 35,
                cam.position.y + 170);
        font.draw(sb, "killed -" + persistent.LoadInt("killed1"), cam.position.x - 35,
                cam.position.y + 185);

        font.draw(sb, "liveEnemies " + liveEnemies.size(), cam.position.x - 35, cam.position.y - 170);
        font.draw(sb, "my id  " + myId, cam.position.x - 115, cam.position.y - 185);

        sb.end();
    }

    @Override
    public void render(ShapeRenderer sr) {
        sr.setProjectionMatrix(cam.combined);
        sr.setAutoShapeType(true);
        sr.begin();
        sr.setColor(Color.BLACK);
        for (Map.Entry<String, Enemy> entry : liveEnemies.entrySet()) {
            sr.polygon(entry.getValue().getBoundsPolygon().getTransformedVertices());
        }
        for (int i = 0; i < bullets.size(); i++) {
            sr.polygon(bullets.get(i).getBoundsPolygon().getTransformedVertices());
        }
        sr.polygon(mButton.getBoundsPolygon().getTransformedVertices());
        if (player != null) {
            sr.polygon(player.getBoundsPolygon().getTransformedVertices());
        }
        for (int i = 0; i < enemies.size(); i++) {
            sr.polygon(enemies.get(i).getBoundsPolygon().getTransformedVertices());
        }
        sr.setColor(Color.GREEN);
        for (int i = 0; i < walls.size(); i++) {
            sr.polygon(walls.get(i).getBoundsPolygon().getTransformedVertices());
        }
        sr.end();
    }

    @Override
    public void dispose() {
        socket.off();
        socket.disconnect();
        socket.close();
        bg.dispose();
        player.dispose();
        mButton.dispose();
        for (Map.Entry<String, Enemy> entry : liveEnemies.entrySet()) {
            entry.getValue().dispose();
        }
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).dispose();
        }
        bulletPool.dispose();
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

//        collisionManager.AddCallback(Type.ENEMY_BULLET, new CollisionManager.CollisionManagerCallBack() {
//            @Override
//            public void collide(Collisionable c1, Collisionable c2) {
//                switch (c2.getType()) {
//                    case PLAYER:
//
//                        break;
//                    case ENEMY:
//
//                        break;
//                    default:
//                        Gdx.app.log("collide", "collide unknown " + c2.getType());
//                }
//            }
//        });
    }

}
