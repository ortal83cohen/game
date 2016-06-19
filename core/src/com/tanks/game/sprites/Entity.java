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

    protected Fixture fixture;

    protected Sprite glowSprite;

    protected Vector2 position;

    protected Polygon boundsPoly;

    protected boolean movement = false;

    public World world;


    public void setPolygon() {
        boundsPoly = new Polygon(new float[]{
                0, 0, glowSprite.getWidth(), 0, glowSprite.getWidth(), glowSprite.getHeight(), 0,
                glowSprite.getHeight()
        });
        boundsPoly.setOrigin(glowSprite.getWidth() / 2, glowSprite.getHeight() / 2);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Sprite getSprite() {
        return glowSprite;
    }

    public Polygon getBoundsPolygon() {
        return boundsPoly;
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
}
