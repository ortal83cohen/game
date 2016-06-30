package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class Player extends Tank {

    public Player(World world, String id, int x, int y, String playerName) {
        super(world, id, "tank2.png", x, y, Type.PLAYER, playerName);

        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);

        speed = 0.1f;
        maxSpeed = 50;
    }

    @Override
    public boolean update(float dt) {
        if (speed > 0) {
            speed = speed - dt * 20;
        }
        super.update(dt);

        return true;
    }


    public void move(int x, int y) {
//        body.setLinearVelocity(x, y);
        float SPEED_RATIO = 1;

        // calculte the normalized direction from the body to the touch position
        Vector2 direction = new Vector2(x, y);
//        direction.sub(body.getPosition());
        direction.nor();

        float speed = 100;
        body.setLinearVelocity(direction.scl(speed));

//        body.applyLinearImpulse(new Vector2((x-body.getPosition().x)/SPEED_RATIO, (y-body.getPosition().y)/SPEED_RATIO), body.getWorldCenter(), true);

//        if (speed < maxSpeed) {
//            speed = speed + 3f;
//        }

    }


}
