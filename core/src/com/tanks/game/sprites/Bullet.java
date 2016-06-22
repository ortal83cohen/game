package com.tanks.game.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.tanks.game.utils.Type;

/**
 * Created by Brent on 7/5/2015.
 */
public class Bullet extends Entity implements Pool.Poolable{

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
        super(world);
        position = new Vector2();
        this.ownerId = ownerId;
        this.texture = texture;

        glowSprite = new Sprite(texture);
        createBody( 0, 0);
        getSprite().scale(-0.8f);
        this.fireSound = fireSound;

        this.directionX = 0;
        this.directionY = 0;

        this.rotation = 0;
        speed = 90;
    }


    private void createBody( int x, int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(glowSprite.getWidth() * 0.5f, glowSprite.getHeight() * 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body = world.createBody(bodyDef);
            body.setBullet(true);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        //linear damping to slow down when applying force
        body.setLinearDamping(4f);
    }

    public void fire(String ownerId, int x, int y, float rotation, float directionX,
            float directionY) {

        this.ownerId = ownerId;
        position.set(x, y);
        body.setTransform(x, y, rotation);

        body.applyLinearImpulse(directionX * speed, directionY * speed, body.getWorldCenter().x,
                body.getWorldCenter().y, true);

        if (ownerId == "Player") {
            setCategoryFilter( Type.PLAYER_BULLET);
        }else {
            setCategoryFilter( Type.ENEMY_BULLET);
        }

        this.directionX = directionX;
        this.directionY = directionY;
        this.rotation = rotation;
        fireSound.play(0.5f);

    }

    public boolean update(float dt) {
        timer += dt;
        position.x = position.x + directionX * dt * speed;
        position.y = position.y + directionY * dt * speed;
        glowSprite.setPosition(getPosition().x, getPosition().y);
        glowSprite.setRotation(rotation);

        if (timer > maxTime) {
            dispose();
            return false;
        }
        return alive;
    }

    public void dispose() {
//        world.destroyBody(body);
        body.setAwake(false);
    }

    @Override
    public void reset() {
        //reset methods invoked when bullet is freed by pool
        timer = 0f;
        alive = true;
        position.set(0, 0);
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

    public void hit() {
        dispose();
        alive = false;
    }

    public int getDamaging(){
        return 10;
    }
}