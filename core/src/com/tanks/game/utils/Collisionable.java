package com.tanks.game.utils;


/**
 * Created by cohenort on 14/06/2016.
 */
public interface Collisionable {

    public boolean hasCollisionBehaviorWith(Type type);

    public Type getType();

    public void collideWith(Collisionable collisionable);

}