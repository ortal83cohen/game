package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.tanks.game.utils.Type;

public class Enemy extends Tank {

    public Enemy(World world,  String id, int x, int y, String playerName) {
        super(world, id, "ship4red.png", x, y, Type.ENEMY, playerName);
    }



    public void move(Vector2 direction,Vector2 diff) {
        if (body.getLinearVelocity().len() < maxSpeed) {
            rotation = (float) Math.toRadians(direction.angle())- (float)Math.PI;
            body.setLinearVelocity(direction.add(diff));
        }


    }

    public void setPosition(Vector2 position) {
        try {
            body.setTransform(position, body.getAngle());
        } catch (Exception e) {

        }

    }


}
