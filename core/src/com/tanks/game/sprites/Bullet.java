package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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

    private static final float NO_ANIMATION = -1;

    private static final float START_ANIMATION = 0;

    private static final float END_ANIMATION = 0.7f;

    private final Animation explosionAnimation;

    private String ownerId;

    private Texture texture;

    private Sound fireSound;

    private float speed;

    private float timer = 0f;

    private float maxTime = 3f;

    private float boomAnimation = NO_ANIMATION;


    public Bullet(World world, String ownerId, Texture texture, Sound fireSound) {
        super(world);

        this.ownerId = ownerId;
        this.texture = texture;

        glowSprite = new Sprite(texture);
        getSprite().scale(-0.8f);
        createBody(0, 0);
        this.fireSound = fireSound;

        explosionAnimation = new Animation(new TextureRegion(new Texture(Gdx.files.internal("explosion.png"))), 5,
                END_ANIMATION);
        explosionAnimation.setScale(-1.5f);
        explosionAnimation.setAlpha(0.7f);
        speed = 90;
    }


    private void createBody(int x, int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(glowSprite.getWidth() / 16,
                glowSprite.getHeight() / 16);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0001f;

        body = world.createBody(bodyDef);
        body.setBullet(true);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        //linear damping to slow down when applying force
//        body.setLinearDamping(4f);
    }

    public void fire(String ownerId, int x, int y, float rotation, Vector2 direction) {

        this.ownerId = ownerId;
        body.setTransform(x + direction.x * 24, y + direction.y * 24, rotation);

        body.applyLinearImpulse(direction.scl(999999999), body.getWorldCenter(), true);

        if (ownerId == "Player") {
            setCategoryFilter(Type.PLAYER_BULLET);
        } else {
            setCategoryFilter(Type.ENEMY_BULLET);
        }

        fireSound.play(0.5f);

    }

    public boolean update(float dt) {

        if (boomAnimation == NO_ANIMATION) {
            timer += dt;
            glowSprite.setPosition(getPosition().x - glowSprite.getWidth() / 2,
                    getPosition().y - glowSprite.getHeight() / 2);
            glowSprite.setRotation((getAngle() * 180) / (float) Math.PI);

            if (timer > maxTime) {
                dispose();
                return false;
            }
        } else {
            boomAnimation += dt;
            explosionAnimation.update(dt);

            if (boomAnimation > END_ANIMATION) {
                dispose();
                return false;
            }
        }
        return true;
    }

    public void dispose() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                body.setTransform(-1, -1, -1);
            }
        });
        body.setAwake(false);
    }

    @Override
    public void reset() {
        //reset methods invoked when bullet is freed by pool
        timer = 0f;
        getPosition().set(0, 0);
        ownerId = null;
        boomAnimation = NO_ANIMATION;
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
        explosionAnimation.setRotation(body.getAngle());
        explosionAnimation.setPosition(body.getPosition().x, body.getPosition().y);
        dispose();
        boomAnimation = START_ANIMATION;
    }

    public int getDamaging() {
        return 10;
    }

    @Override
    public void draw(SpriteBatch sb) {
        if (NO_ANIMATION == boomAnimation) {
            super.draw(sb);
        } else {
            explosionAnimation.getFrame().draw(sb);
        }
    }
}