package com.tanks.game.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.tanks.game.utils.Type;

/**
 * Created by Brent on 7/5/2015.
 */
public class Bullet extends Entity implements Pool.Poolable {

    private String ownerId;

    private Texture texture;

    private Sound fireSound;

    private float speed;

    private float timer = 0f;

    private float maxTime = 3f;

    private boolean alive = true;


    public Bullet(World world, String ownerId, Texture texture, Sound fireSound) {
        super(world);

        this.ownerId = ownerId;
        this.texture = texture;

        glowSprite = new Sprite(texture);
        getSprite().scale(-0.8f);
        createBody(0, 0);
        this.fireSound = fireSound;

        speed = 90;
    }


    private void createBody(int x, int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(glowSprite.getWidth() * 0.3f * glowSprite.getScaleX(),
                glowSprite.getHeight() * 0.3f * glowSprite.getScaleY());
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
        getPosition().set(x, y);
        body.setTransform(x, y, rotation);

        body.applyLinearImpulse(directionX * speed, directionY * speed, body.getWorldCenter().x,
                body.getWorldCenter().y, true);

        if (ownerId == "Player") {
            setCategoryFilter(Type.PLAYER_BULLET);
        } else {
            setCategoryFilter(Type.ENEMY_BULLET);
        }

        fireSound.play(0.5f);

    }

    public boolean update(float dt) {
        timer += dt;
//        getPosition().x = getPosition().x + directionX * dt * speed;
//        getPosition().y = getPosition().y + directionY * dt * speed;
        glowSprite.setPosition(getPosition().x - glowSprite.getWidth() / 2,
                getPosition().y - glowSprite.getHeight() / 2);
        glowSprite.setRotation((getAngle() * 180) / (float) Math.PI);

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
        getPosition().set(0, 0);
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

        if (bullet.body != body) {
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
        return result;
    }

    public void hit() {
        dispose();
        alive = false;
    }

    public int getDamaging() {
        return 10;
    }
}