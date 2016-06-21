package com.tanks.game.utils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by chovel on 18/06/2016.
 *
 */
public class BasicContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA == null || fixtureA.getUserData() == null || fixtureB == null || fixtureB.getUserData() == null) {
            return;
        }

        if (fixtureA.getUserData() instanceof Collisionable && fixtureB.getUserData() instanceof Collisionable) {
            //temporarily keep collisionable behaviour
            Collisionable collisionableA = (Collisionable) fixtureA.getUserData();
            Collisionable collisionableB = (Collisionable) fixtureB.getUserData();
            if (collisionableA.hasCollisionBehaviorWith(collisionableB.getType())) {
                collisionableA.collideWith(collisionableB);
            }
            if (collisionableB.hasCollisionBehaviorWith(collisionableA.getType())) {
                collisionableB.collideWith(collisionableA);
            }
        }

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
