package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.tanks.game.utils.CollisionManager;
import com.tanks.game.utils.Collisionable;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class Enemy extends Tank {

    public Enemy(int x, int y, CollisionManager collisionManager) {
        super(x, y, Type.ENEMY, collisionManager);

        if (Math.random() < 0.5) {
            texture = new Texture("tank.png");
        } else {
            texture = new Texture("tank2.png");
        }
        position = new Vector2(x, y);
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        setPolygon();
        boundsPoly.scale(-0.5f);
        getSprite().scale(-0.5f);
        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);

        directionX = 1;
        directionY = 1;

    }

    public boolean update(float dt) {
        super.update(dt);
        collisionManager.checkCollision(this);

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

    @Override
    public boolean intersects(Type type) {
        return super.intersects(type) || type.equals(Type.PLAYER)||type.equals(Type.PLAYER_BULLET);
    }

    @Override
    public void collideWith(Collisionable collisionable) {
        switch (collisionable.getType()) {
            case PLAYER_BULLET:
                dispose();
                alive = false;

        }

    }
}
