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


    private Body body;

    public Wall(World world, Polygon polygon) {
        super(world);

        createBody( polygon,Type.WALL);
    }

    private void createBody(Polygon polygon, short type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((int)polygon.getX(), (int) polygon.getY());
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();

        shape.set(polygon.getTransformedVertices());
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = type;
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();
    }


    public void dispose() {

    }
}
