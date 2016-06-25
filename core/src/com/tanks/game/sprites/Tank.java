package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tanks.game.utils.Assets;

/**
 *
 */
public class Tank extends Entity {

    protected final String textureFileName;

    protected final Texture texture;

    private final String id;

    protected int maxSpeed;

    protected Animation birdAnimation;

    protected float speed;

    private boolean alive = true;


    public Tank(World world, String id, String textureFileName, int x, int y, short type) {
        super(world);
        this.id = id;
        this.textureFileName = textureFileName;
        texture = Assets.getInstance().getManager().get(textureFileName);
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        getSprite().scale(-0.5f);
        createBody(world, x, y, type);


    }

    private void createBody(World world, int x, int y, short type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(glowSprite.getWidth() * 0.5f * glowSprite.getScaleX(),
                glowSprite.getHeight() * 0.5f * glowSprite.getScaleY());
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = type;
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();

        //linear damping to slow down when applying force
        body.setLinearDamping(4f);
    }

    public boolean update(float dt) {

        birdAnimation.update(dt);//animation example
        glowSprite.setPosition(body.getPosition().x - glowSprite.getWidth() / 2,
                body.getPosition().y - glowSprite.getHeight() / 2);
        glowSprite.setRotation((getAngle() * 180) / (float) Math.PI);
        if (!alive) {
            dispose();
            body.setActive(false);
            return false;
        }
        return true;
    }

    public float getRotation() {
        return body.getAngle();
    }


    public void dispose() {
//        world.destroyBody(body);
    }

    public float getSpeed() {
        return speed;
    }

    public void hit(int damage) {
        resistant = resistant - damage;
        if (resistant <= 0) {
            alive = false;
        }
    }
}