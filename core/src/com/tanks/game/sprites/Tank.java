package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.tanks.game.utils.CollisionManager;
import com.tanks.game.utils.Collisionable;
import com.tanks.game.utils.MathUtil;
import com.tanks.game.utils.Type;

/**
 * Created by Brent on 7/5/2015.
 */
public class Tank extends Entity implements Collisionable {


    public float directionX;//tmp for enemies

    public float directionY;//tmp for enemies

    private int maxSpeed;

    private Animation birdAnimation;

    private Texture texture;

    private float speed;

    private boolean deceleration = false;

    private Type type;

    public Tank(int x, int y, Texture texture, CollisionManager collisionManager) {
        super(collisionManager);
        this.collisionManager.register(this);
        position = new Vector2(x, y);
        this.type = Type.PLAYER;
        this.texture = texture;
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        setPolygon();
        boundsPoly.scale(-0.5f);
        getSprite().scale(-0.5f);
        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);

        directionX = 1;
        directionY = 1;

        speed = 0.1f;
        maxSpeed = 50;
        deceleration = true;
    }

    public Tank(int x, int y, CollisionManager collisionManager) {
        super(collisionManager);
        this.collisionManager.register(this);
        this.type = Type.ENEMY;
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
        if (speed > 0) {
            collisionManager.update(this);
            movement = true;
            if (deceleration) {
                speed = speed - dt * 20;
            }
        }
        position.x = position.x + (directionX * dt * speed);
        position.y = position.y + (directionY * dt * speed);
//        Gdx.app.log("SocketIO", "playerUpdate x" + position.x + " y" + position.y);
        float rotation = (float) MathUtil.getAngle(directionX, directionY);
        birdAnimation.update(dt);//animation example
        boundsPoly.setPosition(position.x, position.y);
        boundsPoly.setRotation(rotation);
        glowSprite.setPosition(getPosition().x, getPosition().y);
        glowSprite.setRotation(rotation);
        Collisionable collision = collisionManager.checkCollision(this);
        if (collision != null) {
            collision.getCollisionBounds().getBoundingRectangle();
            directionX = -directionX;
            directionY = -directionY;
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

    @Override
    public boolean hasMoved() {
        return speed > 0;
    }

    public float getRotation() {
        return (float) MathUtil.getAngle(directionX, directionY);
    }


    public void dispose() {
        texture.dispose();
        collisionManager.unregister(this);
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public Polygon getCollisionBounds() {
        return boundsPoly;
    }

    @Override
    public boolean intersects(Collisionable c) {
        return false;
    }

    @Override
    public Type getType() {
        return this.type;
    }

}