package com.tanks.game.utils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tanks.game.sprites.Bullet;
import com.tanks.game.sprites.Tank;

/**
 * Created by chovel on 18/06/2016.
 */
public class BasicContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA == null || fixtureA.getUserData() == null || fixtureB == null
                || fixtureB.getUserData() == null) {
            return;
        }

        int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (cDef) {
            case Type.PLAYER_BULLET | Type.ENEMY:
            case Type.PLAYER_BULLET | Type.AI_ENEMY:

                if (fixtureA.getFilterData().categoryBits == Type.PLAYER_BULLET) {
                    ((Bullet) fixtureA.getUserData()).hit();
                    ((Tank) fixtureB.getUserData())
                            .hit(((Bullet) fixtureA.getUserData()).getDamaging());

                } else {
                    ((Tank) fixtureA.getUserData())
                            .hit(((Bullet) fixtureB.getUserData()).getDamaging());
                    ((Bullet) fixtureB.getUserData()).hit();
                }

                break;
            case Type.PLAYER_BULLET | Type.WALL:
            case Type.PLAYER_BULLET | Type.STONE:
            case Type.ENEMY_BULLET | Type.WALL:
            case Type.ENEMY_BULLET | Type.STONE:

                if (fixtureA.getUserData() instanceof Bullet) {
                    ((Bullet) fixtureA.getUserData()).hit();
                } else {
                    ((Bullet) fixtureB.getUserData()).hit();
                }

                break;
        }

//        if (fixtureA.getUserData() instanceof Collisionable && fixtureB
//                .getUserData() instanceof Collisionable) {
//            //temporarily keep collisionable behaviour
//            Collisionable collisionableA = (Collisionable) fixtureA.getUserData();
//            Collisionable collisionableB = (Collisionable) fixtureB.getUserData();
//            if (collisionableA.hasCollisionBehaviorWith(collisionableB.getType())) {
//                collisionableA.collideWith(collisionableB);
//            }
//            if (collisionableB.hasCollisionBehaviorWith(collisionableA.getType())) {
//                collisionableB.collideWith(collisionableA);
//            }
//        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
