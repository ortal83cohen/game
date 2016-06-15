package com.tanks.game.utils;

import com.badlogic.gdx.math.Polygon;

/**
 * Created by cohenort on 14/06/2016.
 */
public interface Collisionable {

    public Polygon getCollisionBounds();

    public boolean intersects(Type type);

    public Type getType();

}