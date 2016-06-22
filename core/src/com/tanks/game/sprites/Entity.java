package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by ortalcohen on 03/06/2016.
 */
public abstract class Entity {

//    private String id;

    public Entity(World world) {
        this.world = world;
    }

    protected Fixture fixture;

    protected Sprite glowSprite;

    protected Vector2 position;

    protected boolean movement = false;

    public World world;

    public int resistant = 100;

    public Vector2 getPosition() {
        return position;
    }

    public Sprite getSprite() {
        return glowSprite;
    }



    public boolean hasMoved() {
        return movement;
    }

    abstract public void dispose();

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public int getResistant() {
        return resistant;
    }
}
