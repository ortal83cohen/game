package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.tanks.game.utils.CollisionManager;

/**
 * Created by ortalcohen on 03/06/2016.
 */
public abstract class Entity {

    protected final CollisionManager collisionManager;

    protected Sprite glowSprite;

    protected Vector2 position;

    protected Polygon boundsPoly;

    protected boolean movement = false;

    protected Entity(CollisionManager collisionManager) {
        this.collisionManager = collisionManager;
    }

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

    public boolean collides(Polygon polygon) {
        return Intersector.overlapConvexPolygons(boundsPoly, polygon);
    }

    public boolean hasMoved() {
        return movement;
    }
}
