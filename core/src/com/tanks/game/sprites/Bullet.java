package com.tanks.game.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.tanks.game.utils.CollisionManager;
import com.tanks.game.utils.Collisionable;
import com.tanks.game.utils.Type;

/**
 * Created by Brent on 7/5/2015.
 */
public class Bullet extends Entity implements Pool.Poolable, Collisionable {

    private String ownerId;

    private Texture texture;

    private Sound fireSound;

    private float rotation;

    private float directionX;

    private float directionY;

    private float speed;

    private float timer = 0f;

    private float maxTime = 3f;

    private boolean alive = true;

    private Body body;

    public Bullet(World world, String ownerId, Texture texture, Sound fireSound) {
        super();
        position = new Vector2();
        this.ownerId = ownerId;
        this.texture = texture;

        glowSprite = new Sprite(texture);
        createBody(world, 0, 0);
        getSprite().scale(-0.8f);
        setPolygon();
        boundsPoly.scale(-1.1f);
        this.fireSound = fireSound;

        this.directionX = 0;
        this.directionY = 0;

        this.rotation = 0;
        speed = 90;
    }


    private void createBody(World world, int x, int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();
        //bounds poly not initialized yet!
//        shape.set(boundsPoly.getVertices());
        shape.setAsBox(glowSprite.getWidth() * 0.5f , glowSprite.getHeight() * 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body = world.createBody(bodyDef);
        body.setBullet(true);
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();

        //linear damping to slow down when applying force
        body.setLinearDamping(4f);
    }

    public void fire(String ownerId, int x, int y, float rotation, float directionX, float directionY) {
//        this.collisionManager.register(this);
        this.ownerId = ownerId;
        position.set(x, y);
        body.setTransform(x, y, rotation);

        body.applyLinearImpulse(directionX*speed, directionY*speed, body.getWorldCenter().x, body.getWorldCenter().y, true);

        this.directionX = directionX;
        this.directionY = directionY;
        this.rotation = rotation;
        fireSound.play(0.5f);

    }

    public boolean update(float dt) {
        timer += dt;
        position.x = position.x + directionX * dt * speed;
        position.y = position.y + directionY * dt * speed;
        boundsPoly.setPosition(position.x, position.y);
        boundsPoly.setRotation(rotation);
        glowSprite.setPosition(getPosition().x, getPosition().y);
        glowSprite.setRotation(rotation);
//        collisionManager.update(this);
        if (timer > maxTime) {
            dispose();
            return false;
        }
        return alive;
    }

    public void dispose() {
//        collisionManager.unregister(this);
        body.setAwake(false);
    }

    @Override
    public void reset() {
        //reset methods invoked when bullet is freed by pool
        timer = 0f;
        alive = true;
        position.set(0, 0);
        boundsPoly.setPosition(0, 0);
        ownerId = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Bullet bullet = (Bullet) o;

        if (Float.compare(bullet.rotation, rotation) != 0) {
            return false;
        }
        if (directionX != bullet.directionX) {
            return false;
        }
        if (directionY != bullet.directionY) {
            return false;
        }
        if (ownerId != null ? !ownerId.equals(bullet.ownerId) : bullet.ownerId != null) {
            return false;
        }
        if (texture != null ? !texture.equals(bullet.texture) : bullet.texture != null) {
            return false;
        }
        return fireSound != null ? fireSound.equals(bullet.fireSound) : bullet.fireSound == null;

    }

    @Override
    public int hashCode() {
        int result = ownerId != null ? ownerId.hashCode() : 0;
        result = 31 * result + (texture != null ? texture.hashCode() : 0);
        result = 31 * result + (fireSound != null ? fireSound.hashCode() : 0);
        result = 31 * result + (rotation != +0.0f ? Float.floatToIntBits(rotation) : 0);
        result = 31 * result + (int) directionX;
        result = 31 * result + (int) directionY;
        return result;
    }

    @Override
    public boolean hasCollisionBehaviorWith(Type type) {
        if (ownerId == "Player") {
            return type == Type.ENEMY || type == Type.SMART_PLAYER || type == Type.ENEMY_BULLET|| type == Type.STONE;
        } else {
            return false;//type == Type.ENEMY || type == Type.PLAYER || type == Type.SMART_PLAYER ||  type == Type.PLAYER_BULLET;
        }
    }

    @Override
    public Type getType() {
        if (ownerId == "Player") {
            return Type.PLAYER_BULLET;
        } else {
            return Type.ENEMY_BULLET;
        }

    }

    @Override
    public void collideWith(Collisionable collisionable) {
        dispose();
        alive = false;
    }

}