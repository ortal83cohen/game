package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.tanks.game.utils.CollisionManager;
import com.tanks.game.utils.Collisionable;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class Player extends Tank {

    public Player(int x, int y, CollisionManager collisionManager) {
        super(x, y, Type.PLAYER, collisionManager);
        position = new Vector2(x, y);
        this.texture = new Texture("tank2.png");
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        setPolygon();
        boundsPoly.scale(-0.5f);
        getSprite().scale(-0.5f);
        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);

        directionX = 1;
        directionY = 1;

        speed = 0.1f;
        maxSpeed = 50;
    }

    @Override
    public boolean update(float dt) {
        if (speed > 0) {
            collisionManager.update(this);
            movement = true;
            speed = speed - dt * 20;
        }
        super.update(dt);
        Collisionable collision = collisionManager.checkCollision(this);
        if (collision != null) {
            switch (collision.getType()) {
                case TOP_WALL:
                    position.y = collision.getCollisionBounds().getBoundingRectangle().getY() - boundsPoly.getBoundingRectangle().getHeight() * 3 / 2;
                    break;
                case BOTTOM_WALL:
                    position.y = collision.getCollisionBounds().getBoundingRectangle().getY();
                    break;
                case LEFT_WALL:
                    position.x = collision.getCollisionBounds().getBoundingRectangle().getX();
                    break;
                case RIGHT_WALL:
                    position.x = collision.getCollisionBounds().getBoundingRectangle().getX() - boundsPoly.getBoundingRectangle().getWidth() * 3 / 2;
                    break;
            }
        }

//        Collisionable collision = collisionManager.checkCollision(this);
//        if (collision != null) {
//            dispose();
//            return false;
//        }
        return true;
    }



    public void move(int x, int y) {
        if (speed < maxSpeed) {
            speed = speed + 3f;
        }
        double length = Math.sqrt((x * x) + (y * y));
        this.directionX = (float) (x / length);
        this.directionY = (float) (y / length);

    }


    @Override
    public boolean intersects(Type type) {
        return  super.intersects(type) || type.equals(Type.ENEMY) || type.equals(Type.SMART_PLAYER) ;
    }

    @Override
    public void collideWith(Collisionable collisionable) {

    }

}
