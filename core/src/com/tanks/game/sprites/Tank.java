package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
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

    protected boolean alive = true;

    protected int maxSpeed;

    protected Animation birdAnimation;

    protected Texture texture;

    protected float speed;

    protected Type type;

    public Tank(int x, int y, Type type, CollisionManager collisionManager) {
        super(collisionManager);
        this.collisionManager.register(this);
        this.type = type;
        position = new Vector2(x, y);
    }

    public boolean update(float dt) {

        position.x = position.x + (directionX * dt * speed);
        position.y = position.y + (directionY * dt * speed);
//        Gdx.app.log("SocketIO", "playerUpdate x" + position.x + " y" + position.y);
        float rotation = (float) MathUtil.getAngle(directionX, directionY);
        birdAnimation.update(dt);//animation example
        boundsPoly.setPosition(position.x, position.y);
        boundsPoly.setRotation(rotation);
        glowSprite.setPosition(getPosition().x, getPosition().y);
        glowSprite.setRotation(rotation);

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
        collisionManager.unregister(this);
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public Polygon getCollisionBounds() {
        return boundsPoly;
    }

    @Override
    public boolean intersects(Type type) {
        return type.equals(Type.TOP_WALL) || type.equals(Type.RIGHT_WALL) || type.equals(Type.LEFT_WALL) || type.equals(Type.BOTTOM_WALL);
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public void collideWith(Collisionable collisionable) {

    }

}