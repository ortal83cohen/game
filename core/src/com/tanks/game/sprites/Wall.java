package com.tanks.game.sprites;

import com.badlogic.gdx.math.Polygon;
import com.tanks.game.utils.CollisionManager;
import com.tanks.game.utils.Collisionable;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class Wall extends Entity implements Collisionable {

    private final Type type;

    public Wall(CollisionManager collisionManager, Type type, Polygon polygon) {
        super(collisionManager);
        this.type = type;
        this.boundsPoly = polygon;
        collisionManager.register(this);
    }

    @Override
    public Polygon getCollisionBounds() {
        return boundsPoly;
    }

    @Override
    public boolean intersects(Type type) {
        return true;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void collideWith(Collisionable collisionable) {

    }

    public void dispose() {
        collisionManager.unregister(this);
    }
}
