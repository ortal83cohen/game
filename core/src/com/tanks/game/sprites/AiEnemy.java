package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.tanks.game.utils.Assets;
import com.tanks.game.utils.Type;

/**
 * Created by ortal on 6/15/2016.
 */
public class AiEnemy extends Tank {

    public AiEnemy(World world, String id, int x, int y) {
        super(world, id,"tank.png" , x, y, Type.AI_ENEMY);

        getSprite().scale(-0.5f);
        birdAnimation = new Animation(new TextureRegion(
                Assets.getInstance().getManager().get(textureFileName, Texture.class)), 3, 0.5f);

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
//        collisionManager.update(this);
        super.update(dt);

        return alive;
    }

//
//    @Override
//    public void collideWith(Collisionable collisionable) {
//        if (collisionable != null) {
//            switch (collisionable.getType()) {
//                case TOP_WALL:
//                    directionY = -Math.abs(directionY);
//                    break;
//                case BOTTOM_WALL:
//                    directionY = Math.abs(directionY);
//                    break;
//                case LEFT_WALL:
//                    directionX = Math.abs(directionX);
//                    break;
//                case RIGHT_WALL:
//                    directionX = -Math.abs(directionX);
//                    break;
//                case PLAYER_BULLET:
//                    dispose();
//                    alive = false;
//            }
//
//        }
//    }

}

