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
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import com.tanks.game.TanksDemo;
import com.tanks.game.sprites.AiEnemy;
import com.tanks.game.sprites.Bullet;
import com.tanks.game.sprites.Button;
import com.tanks.game.sprites.Enemy;
import com.tanks.game.sprites.Player;
import com.tanks.game.sprites.Stone;
import com.tanks.game.sprites.Tank;
import com.tanks.game.sprites.Wall;
import com.tanks.game.utils.Assets;
import com.tanks.game.utils.BasicContactListener;
import com.tanks.game.utils.BulletPool;
import com.tanks.game.utils.Type;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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

    private final Stage stage;

    public static final List<String> textureFiles = Arrays
            .asList("tank.png", "tank2.png", "bullet.png");

    private static final float UPDATE_TIME = 1 / 30f;

    static public int GAME_WIDTH = 400;

    static public int GAME_HEIGHT = 400;

    private final TextureRegion bgTextureRegion;

    private final Texture bulletTexture;

    private final com.tanks.game.utils.Persistent persistent;

    BitmapFont font;

    int ANDROID_WIDTH = Gdx.graphics.getWidth();

    int ANDROID_HEIGHT = Gdx.graphics.getHeight();

    Map<String, Enemy> liveEnemies;

    List<Tank> aiEnemies = new ArrayList<Tank>();

    BulletPool bulletPool;

    List<Bullet> bullets;

    private World world;

    private Map<String, Stone> stones;

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

    private List<Wall> walls;

    private Box2DDebugRenderer b2dr;

    public OnlinePlayState(GameStateManager gsm, boolean addSmartPlayers) {
        super(gsm);

        loadAssets();
        Box2D.init();
        b2dr = new Box2DDebugRenderer();
        world = new World(new Vector2(0f, 0f), true);
        world.setContactListener(new BasicContactListener());
        configCollisionManager();
        connectSocket();
        configSocketEvents();

        stage = new Stage();
        mButton = new Button(stage,50, 50);
        mButton.getButton().addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        shoot(player.getPosition().x, player.getPosition().y, player.getRotation(),
                                player.directionX, player.directionY);
                    }
                }, connectionDelay);
            }
        });
        liveEnemies = new HashMap<String, Enemy>();
        stones = new HashMap<String, Stone>();
        bullets = new ArrayList<Bullet>();
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        bg = new Texture("bg.png");

        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgTextureRegion = new TextureRegion(bg);
        bgTextureRegion.setRegion(0, 0, GAME_WIDTH, GAME_HEIGHT);
        bulletTexture = Assets.getInstance().getManager().get("bullet.png");
        bulletPool = new BulletPool(world, bulletTexture,
                Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg")));

        persistent = new com.tanks.game.utils.Persistent();

        player = new Player(world, "Player", (int) (Math.random() * GAME_WIDTH),
                (int) (Math.random() * GAME_HEIGHT));

        if (addSmartPlayers) {
            for (int i = 0; i < 10; i++) {
                aiEnemies.add(i, new AiEnemy(world, "Enemy_" + i, (int) (Math.random() * GAME_WIDTH),
                        (int) (Math.random() * GAME_HEIGHT)));
            }
        }
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
    }

    private void loadAssets() {
        Assets.getInstance().loadSingleTypeAssetList(
                textureFiles,
                Texture.class
        );
        Assets.getInstance().getManager().finishLoading();
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
            } catch (Exception e) {
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
                        } catch (Exception e) {
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
                } catch (Exception e) {
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
                                liveEnemies.put(id, new Enemy(world, id, data.getInt("x"),
                                        data.getInt("y")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Vibrator)) {
                        Gdx.input.vibrate(new long[]{0, 2, 10, 2, 10, 2}, -1);
                    }
                } catch (Exception e) {
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
                } catch (Exception e) {
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
                } catch (Exception e) {
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
                } catch (Exception e) {
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
                } catch (Exception e) {
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
                                    liveEnemies.put(object.getString("id"),
                                            new Enemy(world, object.getString("id"),
                                                    object.getInt("x"),
                                                    object.getInt("y")));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                    if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Vibrator)) {
                        Gdx.input.vibrate(new long[]{0, 2, 10, 2, 10, 2}, -1);
                    }
                } catch (Exception e) {
                    Gdx.app.log("SocketIO", "Error Get Players");
                }
            }
        }).on("getStones", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONArray objects = (JSONArray) args[0];
                try {
                    Gdx.app.log("SocketIO", "Get Stone: " + objects.length());
                    for (int i = 0; i < objects.length(); i++) {
                        final JSONObject object = objects.getJSONObject(i);
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    stones.put(String.valueOf(object.getInt("id")), new Stone(object.getInt("x"),
                                            object.getInt("y")));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
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
                } catch (Exception e) {
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
//                    player.move(x, y);
                }
            }, connectionDelay);
        }
        if ( Gdx.input.isKeyPressed(Input.Keys.SPACE)) {

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    shoot(player.getPosition().x, player.getPosition().y, player.getRotation(),
                            player.directionX, player.directionY);
                }
            }, connectionDelay);
        }

    }

    @Override
    public void update(float dt) {
        int velocityIterations = 6;
        int positionIterations = 2;
        updateTimeLoopTimer += dt;
        timer += dt;
        if (player != null) {
            handleInput();
            player.update(dt);
            playerMoved(dt);
        }
        world.step(dt, velocityIterations, positionIterations);

        for (Map.Entry<String, Enemy> entry : liveEnemies.entrySet()) {
            if (!entry.getValue().update(dt)) {
                JSONObject data = new JSONObject();
                try {
                    data.put("id", entry.getKey());
                    socket.emit("playerHit", data);
                    HashMap map = new HashMap();
                    map.put("kill1", persistent.LoadInt("kill1") + 1);
                    persistent.saveInt(map);
                } catch (Exception e) {
                    Gdx.app.log("SocketIO", "Error sending playerHit data");
                }
                entry.getValue().dispose();
                liveEnemies.remove(entry.getKey());
            }
        }
        for (Map.Entry<String, Stone> entry : stones.entrySet()) {
            if (!entry.getValue().update(dt)) {
//                JSONObject data = new JSONObject();
//                try {
//                    data.put("id", entry.getKey());
//                    socket.emit("playerHit", data);
//                    HashMap map = new HashMap();
//                    map.put("kill1", persistent.LoadInt("kill1") + 1);
//                    persistent.saveInt(map);
//                } catch (JSONException e) {
//                    Gdx.app.log("SocketIO", "Error sending playerHit data");
//                }
//                entry.getValue().dispose();
//                liveEnemies.remove(entry.getKey());
            }
        }

        for (int i = 0; i < aiEnemies.size(); i++) {
            Tank enemy = aiEnemies.get(i);
            if (!enemy.update(dt)) {
                aiEnemies.remove(i);
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
                    + player.getSprite().getHeight() / 2;
            cam.position.y = player.getPosition().y
                    + player.getSprite().getWidth() / 2;
        }
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
                } catch (Exception e) {
                    Gdx.app.log("SocketIO", "Error sending update data");
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {

        //load all assets in queue
        Assets.getInstance().getManager().update();

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bgTextureRegion, 0, 0);
        if (player != null) {
            player.getSprite().draw(sb);
        }
//        mButton.getSprite().draw(sb);


        for (Map.Entry<String, Enemy> entry : liveEnemies.entrySet()) {
            entry.getValue().getSprite().draw(sb);
        }
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).getSprite().draw(sb);
        }
        for (int i = 0; i < aiEnemies.size(); i++) {
            aiEnemies.get(i).getSprite().draw(sb);
        }
        for (Map.Entry<String, Stone> entry : stones.entrySet()) {
            entry.getValue().getSprite().draw(sb);
        }
        b2dr.render(world, cam.combined);
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

        font.draw(sb, "liveEnemies " + liveEnemies.size(), cam.position.x - 35,
                cam.position.y - 170);
        font.draw(sb, "my id  " + myId, cam.position.x - 115, cam.position.y - 185);
        stage.draw();
        sb.end();
    }

    @Override
    public void render(ShapeRenderer sr) {
        sr.setProjectionMatrix(cam.combined);
        sr.setAutoShapeType(true);
        sr.begin();
        sr.setColor(Color.BLACK);
        if (player != null) {
            sr.circle(player.getPosition().x,player.getPosition().y,player.resistant/10);
        }
        for (Map.Entry<String, Enemy> entry : liveEnemies.entrySet()) {
            sr.circle(entry.getValue().getPosition().x,entry.getValue().getPosition().y,entry.getValue().resistant/10);;
        }

        for (int i = 0; i < aiEnemies.size(); i++) {
            sr.circle( aiEnemies.get(i).getPosition().x, aiEnemies.get(i).getPosition().y, aiEnemies.get(i).resistant/10);
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
        for (int i = 0; i < aiEnemies.size(); i++) {
            aiEnemies.get(i).dispose();
        }
        for (int i = 0; i < stones.size(); i++) {
            stones.get(i).dispose();
        }
        bulletPool.dispose();
        for (String textureFile : textureFiles) {
            Assets.getInstance().getManager().unload(textureFile);
        }
    }


    private void configCollisionManager() {

        walls = new ArrayList<Wall>();
        walls.add(new Wall(world, Type.WALL,
                new Polygon(new float[]{0, GAME_HEIGHT, GAME_WIDTH, GAME_WIDTH, 0, GAME_HEIGHT + 1,
                        GAME_WIDTH, GAME_WIDTH + 1})));
        walls.add(new Wall(world, Type.WALL,
                new Polygon(new float[]{0, 0, GAME_WIDTH, 0, -1, 0, GAME_WIDTH - 1, 0})));
        walls.add(new Wall(world, Type.WALL,
                new Polygon(new float[]{0, 0, 0, GAME_HEIGHT, -1, GAME_HEIGHT, -1, 0})));
        walls.add(new Wall(world, Type.WALL,
                new Polygon(new float[]{GAME_WIDTH, 0, GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH + 1,
                        GAME_HEIGHT, GAME_WIDTH + 1, 0})));

    }

}
