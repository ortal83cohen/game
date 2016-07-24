package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tanks.game.utils.Assets;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class AiEnemy extends Tank {

    private Vector2 direction;

    public AiEnemy(World world, String id, int x, int y) {
        super(world, id, "ship4red.png", x, y, Type.AI_ENEMY, "Robot");
        randomDirection();
    }

    private void randomDirection() {
        float high = 100;
        float low = 50;
        direction = new Vector2((float) (Math.random() % (high - low + 1) + low), (float) (Math.random() % (high - low + 1) + low));
        float speed = 100;
        body.setLinearVelocity(direction.scl(speed));
    }




    public void hit() {
        randomDirection();
    }

}

