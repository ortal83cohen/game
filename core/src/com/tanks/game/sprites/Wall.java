package com.tanks.game.sprites;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class Wall extends Entity {

    private final short type;

    private Body body;

    public Wall(World world, short type, Polygon polygon) {
        this.type = type;
        createBody(world, polygon.getX(), polygon.getY());
    }

    private void createBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();
        //bounds poly not initialized yet!
//        shape.set(boundsPoly.getVertices());
        shape.setAsBox( x * 0.5f,
                y * 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();
    }


    public void dispose() {

    }
}
