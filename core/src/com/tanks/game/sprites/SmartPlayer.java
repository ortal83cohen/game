package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tanks.game.utils.CollisionManager;
import com.tanks.game.utils.Collisionable;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class SmartPlayer extends Tank {

    public SmartPlayer(int x, int y, CollisionManager collisionManager) {
        super(x, y, Type.SMART_PLAYER, collisionManager);

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
        movement = true;
        speed = 50;
        maxSpeed = 50;

    }


    @Override
    public boolean update(float dt) {
        collisionManager.update(this);
        super.update(dt);
        collisionManager.checkCollision(this);

        return alive;
    }


    @Override
    public boolean intersects(Type type) {
        return super.intersects(type) || type.equals(Type.PLAYER) || type.equals(Type.PLAYER_BULLET);
    }

    @Override
    public void collideWith(Collisionable collisionable) {
        if (collisionable != null) {
            switch (collisionable.getType()) {
                case TOP_WALL:
                    directionY = -Math.abs(directionY);
                    break;
                case BOTTOM_WALL:
                    directionY = Math.abs(directionY);
                    break;
                case LEFT_WALL:
                    directionX = Math.abs(directionX);
                    break;
                case RIGHT_WALL:
                    directionX = -Math.abs(directionX);
                    break;
                case PLAYER_BULLET:
                    dispose();
                    alive = false;
            }

        }
    }
}

