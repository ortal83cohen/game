package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class Player extends Tank {





    public Player(World world, String id, int x, int y, String playerName) {
        super(world, id, "ship4.png", x, y, Type.PLAYER, playerName);

        //linear damping to slow down when applying force
        body.setLinearDamping(0.2f);

    }


    public void move(int x, int y) {
//        body.setLinearVelocity(x, y);
        float SPEED_RATIO = 1;

        // calculte the normalized direction from the body to the touch position
        Vector2 direction = new Vector2(x, y);
//        direction.sub(body.getPosition());

//        if( body.getLinearVelocity().nor())
//

        Gdx.app.error("d", ((float) Math.toRadians(direction.angle()) + Math.PI/2 )+"");
        Gdx.app.error("d", rotation+"");
        Gdx.app.error("d", "_________________");
        if (body.getLinearVelocity().len() < maxSpeed) {
            body.setLinearVelocity(body.getLinearVelocity().add(direction.nor().scl(mTankCharacteristics.getAcceleration())));
            rotation = (float) (Math.toRadians(direction.angle()) + Math.PI);
        }


//        Gdx.app.log("acceleration", acceleration +"" );
//        Gdx.app.log("getAngularVelocity", body.getLinearVelocity().len()+"" );
//        Gdx.app.log("------------------", "---------------------------" );

//        body.applyLinearImpulse(new Vector2((x-body.getPosition().x)/SPEED_RATIO, (y-body.getPosition().y)/SPEED_RATIO), body.getWorldCenter(), true);



    }


    public void gift() {
        mTankCharacteristics.addResistant(5);
        mTankCharacteristics.addShield(0.5f);
    }
}
