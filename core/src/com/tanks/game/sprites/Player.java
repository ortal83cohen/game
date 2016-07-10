package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class Player extends Tank {

    private float maxSpeed =500;

    private float acceleration = 6;

    public Player(World world, String id, int x, int y, String playerName) {
        super(world, id, "tank2.png", x, y, Type.PLAYER, playerName);

        //linear damping to slow down when applying force
        body.setLinearDamping(3f);

    }

    @Override
    public boolean update(float dt) {
        super.update(dt);
        return true;
    }


    public void move(int x, int y) {
//        body.setLinearVelocity(x, y);
        float SPEED_RATIO = 1;

        // calculte the normalized direction from the body to the touch position
        Vector2 direction = new Vector2(x, y);
//        direction.sub(body.getPosition());

//        if( body.getLinearVelocity().nor())
//
        if (body.getLinearVelocity().len() < maxSpeed) {
            body.setLinearVelocity(body.getLinearVelocity().add(direction.nor().scl(acceleration)));
        }


        Gdx.app.log("acceleration", acceleration +"" );
        Gdx.app.log("getAngularVelocity", body.getLinearVelocity().len()+"" );
        Gdx.app.log("------------------", "---------------------------" );

//        body.applyLinearImpulse(new Vector2((x-body.getPosition().x)/SPEED_RATIO, (y-body.getPosition().y)/SPEED_RATIO), body.getWorldCenter(), true);



    }


}
