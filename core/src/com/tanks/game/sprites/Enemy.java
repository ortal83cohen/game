package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class Enemy extends Tank {

    public Enemy(World world, String id, int x, int y) {
        super(world, id, "tank.png", x, y, Type.ENEMY);
        position = new Vector2(x, y);

        getSprite().scale(-0.5f);
        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);

        directionX = 1;
        directionY = 1;

    }

    public boolean update(float dt) {
        super.update(dt);

        return alive;
    }

    public void move(float directionX, float directionY, float speed) {
        this.speed = speed;
        this.directionX = directionX;
        this.directionY = directionY;

    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }


}
