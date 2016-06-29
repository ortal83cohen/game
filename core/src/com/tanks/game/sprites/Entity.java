package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by ortalcohen on 03/06/2016.
 */
public abstract class Entity {

//    private String id;

    public World world;

    public int resistant = 100;

    protected Fixture fixture;

    protected Sprite glowSprite;

    protected Body body;

    public Entity(World world) {
        this.world = world;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public float getAngle() {
        return body.getAngle();
    }

    public Sprite getSprite() {
        return glowSprite;
    }


    public Body getBody() {
        return body;
    }

    abstract public void dispose();

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public int getResistant() {
        return resistant;
    }

    public void draw(SpriteBatch sb) {
        glowSprite.draw(sb);
    }
}
