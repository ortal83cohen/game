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
public class Stone extends Entity implements Collisionable {

    public float directionX;//tmp for enemies

    public float directionY;//tmp for enemies

    protected boolean alive = true;

    protected int maxSpeed;

    protected Texture texture;

    protected float speed;


    public Stone(int x, int y, CollisionManager collisionManager) {
        super(collisionManager);
        collisionManager.register(this);

        position = new Vector2(x, y);
        this.texture = new Texture("stone.png");
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        setPolygon();
        boundsPoly.scale(-1.5f);
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
        boundsPoly.setPosition(position.x, position.y);
//        boundsPoly.setRotation(rotation);
        glowSprite.setPosition(getPosition().x, getPosition().y);
//        glowSprite.setRotation(rotation);
        collisionManager.update(this);
        collisionManager.checkCollision(this);
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
    public boolean intersects(Type type){
        return false;
    }

    @Override
    public Type getType() {
        return Type.STONE;
    }

    @Override
    public void collideWith(Collisionable collisionable) {

    }

}