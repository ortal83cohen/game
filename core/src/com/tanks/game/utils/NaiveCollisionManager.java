package com.tanks.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by ortal on 6/14/2016.
 */
public class NaiveCollisionManager implements CollisionManager {

    private ArrayList<Collisionable> collisionables = new ArrayList<Collisionable>();

    private HashMap<Type, CollisionManagerCallBack> callBacks = new HashMap<Type, CollisionManagerCallBack>();

    @Override
    public void AddCallback(Type type, CollisionManagerCallBack callBack) {
        this.callBacks.put(type, callBack);
    }

    @Override
    public void register(Collisionable c) {
        if (!collisionables.contains(c)) {
            collisionables.add(c);
        }
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
    public void checkCollision(Collisionable c) {
        try {
            for (Collisionable collisionable : collisionables) {
//                if ((Intersector.overlapConvexPolygons(collisionable.getCollisionBounds(), c.getCollisionBounds()))) {
//                    if (callBacks.containsKey(c.getType())) {
//                        callBacks.get(c.getType()).collide(c, collisionable);
//                    }
//                    if (collisionable.hasCollisionBehaviorWith(c.getType())) {
//                        collisionable.collideWith(c);
//                    }
//                    if (c.hasCollisionBehaviorWith(collisionable.getType())) {
//                        c.collideWith(collisionable);
//                    }
//                }
            }
        } catch (ConcurrentModificationException e) {
            Gdx.app.log("NaiveCollisionManager", "collisionables ConcurrentModificationException");
        }
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
            Gdx.app.log("NaiveCollisionManager", "cant update " + c.getType().toString());
        }
    }
}
