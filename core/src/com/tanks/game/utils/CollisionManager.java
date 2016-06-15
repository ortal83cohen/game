package com.tanks.game.utils;

import java.util.LinkedList;

/**
 * Created by cohenort on 14/06/2016.
 */
public interface CollisionManager {


    public void AddCallback(Type type, CollisionManagerCallBack callBack);


    public void register(Collisionable c);

    public void unregister(Collisionable c);

    public Collisionable checkCollision(Collisionable c);

    public LinkedList<Collisionable> checkCollisions(Collisionable c);

    public void update(Collisionable c);

    public interface CollisionManagerCallBack {

        public void collide(Collisionable callbackRequest, Collisionable collideWith);

    }
}