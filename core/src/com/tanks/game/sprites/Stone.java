package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tanks.game.utils.Type;

/**
 * Created by Brent on 7/5/2015.
 */
public class Stone extends Entity {

    protected boolean alive = true;

    protected int maxSpeed;

    protected Texture texture;

    protected float speed;

    public Stone(World world, int x, int y) {
        super(world);

        this.texture = new Texture("stone.png");
//        this.texture = Assets.getInstance().getManager().get("stone.png");
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        glowSprite.setPosition(x, y);
        glowSprite.scale(-1.5f);
        createBody(x, y, Type.STONE);

        speed = 0f;
        maxSpeed = 5;

    }

    private void createBody(int x, int y, short type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x + glowSprite.getWidth() * 0.5f, y + glowSprite.getHeight() * 0.5f);
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(glowSprite.getWidth() /4 ,
                glowSprite.getHeight() /4 );
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1000000;
        fixtureDef.filter.categoryBits = type;

        body = world.createBody(bodyDef);
        body.setBullet(true);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

    }

    public boolean update(float dt) {
        glowSprite.setRotation((getAngle() * 180) / (float) Math.PI);
        return true;
    }

    public void dispose() {
        alive = false;
        texture.dispose();
    }

    public float getSpeed() {
        return speed;
    }


}