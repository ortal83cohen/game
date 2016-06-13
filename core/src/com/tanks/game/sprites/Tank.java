package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.tanks.game.utils.MathUtil;

/**
 * Created by Brent on 7/5/2015.
 */
public class Tank extends GameSprite {


    public float directionX;//tmp for enemies

    public float directionY;//tmp for enemies

    private int maxSpeed;

    private Animation birdAnimation;

    private Texture texture;

    private float speed;

    private boolean deceleration = false;

    public Tank(int x, int y, Texture texture) {
        position = new Vector2(x, y);

        this.texture = texture;
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        setPolygon();
        boundsPoly.scale(-0.5f);
        getSprite().scale(-0.5f);
        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);

        directionX = 0;
        directionY = 0;

        speed = 0.1f;
        maxSpeed = 50;
        deceleration = true;
    }

    public Tank(int x, int y) {
        position = new Vector2(x, y);
        if (Math.random() < 0.5) {
            texture = new Texture("tank.png");
        } else {
            texture = new Texture("tank2.png");
        }

        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        setPolygon();
        boundsPoly.scale(-0.5f);
        getSprite().scale(-0.5f);
        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);

        directionX = (int) (Math.random() * 500) - 250;
        directionY = (int) (Math.random() * 500) - 250;

        double length = Math.sqrt((directionX * directionX) + (directionY * directionY));

        this.directionX = (float) (directionX / length);
        this.directionY = (float) (directionY / length);

        speed = 50;
        maxSpeed = 50;
    }


    public void update(float dt) {
        if (deceleration && speed > 0) {
            movement = true;
            speed = speed - dt * 20;

        position.x = position.x + (directionX * dt * speed);
        position.y = position.y + (directionY * dt * speed);
        Gdx.app.log("SocketIO", "playerUpdate x"+position.x+" y"+position.y);
        float rotation = (float) MathUtil.getAngle(directionX, directionY);
        birdAnimation.update(dt);//animation example
        boundsPoly.setPosition(position.x, position.y);
        boundsPoly.setRotation(rotation);
        glowSprite.setPosition(getPosition().x, getPosition().y);
        glowSprite.setRotation(rotation);
        }
    }


    public void move(int x, int y) {
        if (speed < maxSpeed) {
            speed = speed + 3f;
        }
        double length = Math.sqrt((x * x) + (y * y));
        this.directionX = (float) (x / length);
        this.directionY = (float) (y / length);

    }

    public void move(float directionX, float directionY, float speed) {
        this.speed = speed;
        this.directionX = directionX;
        this.directionY = directionY;

    }

    public float getRotation() {
        return (float) MathUtil.getAngle(directionX, directionY);
    }


    public void dispose() {
        texture.dispose();
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getSpeed() {
        return speed;
    }
}