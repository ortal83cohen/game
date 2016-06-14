package com.tanks.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by ortal on 6/14/2016.
 */
public class NaiveCollisionManager implements CollisionManager {

    ArrayList<Collisionable> collisionables = new ArrayList<Collisionable>();

    private CollisionManagerCallBack callBack;

    @Override
    public void setCallback(CollisionManagerCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void register(Collisionable c) {
        collisionables.add(c);
    }

    @Override
    public void unregister(Collisionable c) {
        try {
            collisionables.remove(c);
        } catch (Exception e) {
            Gdx.app.log("NaiveCollisionManager", "cant remove" + c.getType().toString());
        }
    }

    @Override
    public Collisionable checkCollision(Collisionable c) {
        Collisionable collision = null;
        for (Collisionable collisionable : collisionables) {
            if ((c.intersects(collisionable) && Intersector
                    .overlapConvexPolygons(collisionable.getCollisionBounds(),
                            c.getCollisionBounds())) || (collisionable.getType() == Type.GAME_WORLD
                    && !Intersector
                    .overlapConvexPolygons(collisionable.getCollisionBounds(),
                            c.getCollisionBounds()))) {
                collision = collisionable;
                callBack.collide(c, collisionable);
                continue;
            }
        }
        return collision;
    }

    @Override
    public LinkedList<Collisionable> checkCollisions(Collisionable c) {
        return null;
    }

    @Override
    public void update(Collisionable c) {

        try {
            collisionables.set(collisionables.indexOf(c), c);
        } catch (Exception e) {
            Gdx.app.log("NaiveCollisionManager", "cant update" + c.getType().toString());
        }
    }
}
