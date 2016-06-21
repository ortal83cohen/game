package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.tanks.game.utils.MathUtil;

/**
 * Created by Brent on 7/5/2015.
 */
public class Stone extends Entity {

    public float directionX;//tmp for enemies

    public float directionY;//tmp for enemies

    protected boolean alive = true;

    protected int maxSpeed;

    protected Texture texture;

    protected float speed;


    public Stone(int x, int y) {
        super();

        position = new Vector2(x, y);
        this.texture = new Texture("stone.png");
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);

        getSprite().scale(-1.5f);

        directionX = 1;
        directionY = 1;

        speed = 0f;
        maxSpeed = 5;

    }

    public boolean update(float dt) {

//        position.x = position.x + (directionX * dt * speed);
//        position.y = position.y + (directionY * dt * speed);
//        float rotation = (float) MathUtil.getAngle(directionX, directionY);
        glowSprite.setPosition(getPosition().x, getPosition().y);
//        glowSprite.setRotation(rotation);
        return true;
    }

    @Override
    public boolean hasMoved() {
        return speed > 0;
    }

    public float getRotation() {
        return (float) MathUtil.getAngle(directionX, directionY);
    }


    public void dispose() {
        alive = false;
        texture.dispose();
    }

    public float getSpeed() {
        return speed;
    }


}