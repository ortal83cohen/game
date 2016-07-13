package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.tanks.game.utils.BodyEditorLoader;
import com.tanks.game.utils.Type;

/**
 * Created by Brent on 7/5/2015.
 */
public class Bullet extends Entity implements Pool.Poolable {

    private final Animation explosionAnimation;

    private String ownerId;

    private Texture texture;

    private Sound fireSound;

    private float speed;

    private float timer = 0f;

    private float maxTime = 3f;

    private boolean gotHit = false;


    public Bullet(World world, String ownerId, Texture texture, Sound fireSound) {
        super(world);

        this.ownerId = ownerId;
        this.texture = texture;

        glowSprite = new Sprite(texture);
        getSprite().scale(-0.8f);
        createBody(0, 0);
        this.fireSound = fireSound;

        explosionAnimation = new Animation(new TextureRegion(new Texture(Gdx.files.internal("explosion.png"))), 5,
                0.2f, 0.2f);
        explosionAnimation.setScale(-1.5f);
        explosionAnimation.setAlpha(0.6f);
        speed = 90;
    }


    private void createBody(int x, int y) {
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("bodies.json"));
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;

//        PolygonShape shape = new PolygonShape();
//
//        shape.setAsBox(glowSprite.getWidth() / 16,
//                glowSprite.getHeight() / 16);
        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = shape;
        fixtureDef.density = 0.001f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.7f;
//        fixtureDef.isSensor = false;
        body = world.createBody(bodyDef);
        loader.attachFixture(body, "bullet", fixtureDef, 20);
        body.setBullet(true);
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setUserData(this);
        }

//        shape.dispose();

        //linear damping to slow down when applying force
//        body.setLinearDamping(4f);
    }

    public void fire(String ownerId, int x, int y, float rotation, Vector2 direction,int speed) {
        body.setActive(true);
        this.ownerId = ownerId;
        body.setTransform(x + direction.x * 26, y + direction.y * 26, rotation);

        body.applyLinearImpulse(direction.scl(speed), body.getWorldCenter(), true);

        if (ownerId == "Player") {
            setCategoryFilter(Type.PLAYER_BULLET);
        } else {
            setCategoryFilter(Type.ENEMY_BULLET);
        }

        fireSound.play(0.5f);
        explosionAnimation.setRotation(rotation);
        explosionAnimation.setPosition(body.getPosition().x, body.getPosition().y);
        explosionAnimation.startAnimation();
    }

    public boolean update(float dt) {

        timer += dt;
        glowSprite.setPosition(body.getPosition().x - glowSprite.getWidth() / 2,
                body.getPosition().y - glowSprite.getHeight() / 2);
        glowSprite.setRotation((body.getAngle() * 180) / (float) Math.PI);
        explosionAnimation.update(dt);
        if (timer > maxTime) {
            return false;
        }
        if(gotHit){
            body.setActive(false);
        }

        return true;
    }



    @Override
    public void reset() {
        //reset methods invoked when bullet is freed by pool
        timer = 0f;
        getPosition().set(0, 0);
        ownerId = null;
        explosionAnimation.stopAnimation();
        gotHit = false;
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
        explosionAnimation.startAnimation();
        gotHit=true;
    }

    public int getDamaging() {
        return 10;
    }

    @Override
    public void dispose() {
        explosionAnimation.dispose();
        texture.dispose();
    }

    @Override
    public void draw(SpriteBatch sb) {
            super.draw(sb);
        if (explosionAnimation.isAwake()) {
            explosionAnimation.getFrame().draw(sb);
        }
    }
}