package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class Enemy extends Tank {

    public Enemy(World world,  String id, int x, int y, String playerName) {
        super(world, id, "tank3.png", x, y, Type.ENEMY, playerName);

        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);

    }

    public boolean update(float dt) {
        super.update(dt);

        return body.isActive();
    }

    public void move(float directionX, float directionY, float speed) {
        body.setLinearVelocity(directionX, directionY);
    }

    public void setPosition(Vector2 position) {
        try {
            body.setTransform(position, body.getAngle());
        } catch (Exception e) {

        }

    }


}
