package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.tanks.game.utils.MathUtil;

/**
 * Created by Brent on 7/5/2015.
 */
public class Tank extends GameSprite {


    public int directionX;//tmp for enemies

    public int directionY;//tmp for enemies

    private Animation birdAnimation;

    private Texture texture;


    private float rotation;

    public Tank(int x, int y,Texture texture) {
        position = new Vector2(x, y);

        this.texture = texture;
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        setPolygon();
        boundsPoly.scale(-0.5f);
        getSprite().scale(-0.5f);
        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);

        directionX = (int) (Math.random() * 500) - 250;
        directionY = (int) (Math.random() * 500) - 250;


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


    }


    public void update(float dt) {
        birdAnimation.update(dt);//animation example
        boundsPoly.setPosition(position.x, position.y);
        boundsPoly.setRotation(rotation);
        glowSprite.setPosition(getPosition().x, getPosition().y);
        glowSprite.setRotation(rotation);

    }


    public void move(int x, int y) {

        rotation = (float) MathUtil.getAngle(x, y);

        position.x = position.x + (float) x / 300;
        position.y = position.y + (float) y / 300;

    }

    public float getRotation() {
        return rotation;
    }


    public void dispose() {
        texture.dispose();
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}