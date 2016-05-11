package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Brent on 7/5/2015.
 */
public class Tank {

    private static final int GRAVITY = -15;

    private static final int MOVEMENT = 100;

    private final Sprite glowSprite;

    private Vector3 position;

    private Rectangle bounds;

    private Animation birdAnimation;

    private Texture texture;

    private Sound flap;

    private float rotation;

    public Tank(int x, int y) {
        position = new Vector3(x, y, 0);
        texture = new Texture("tank.png");
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);
        bounds = new Rectangle(x, y, texture.getWidth() / 3, texture.getHeight());
        flap = Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg"));
    }

    public void update(float dt) {
        birdAnimation.update(dt);
        bounds.setPosition(position.x, position.y);

        glowSprite.setPosition(getPosition().x, getPosition().y);
        glowSprite.setRotation(rotation);
    }

    public Vector3 getPosition() {
        return position;
    }

    public Sprite getSprite() {
        return glowSprite;
    }

    public void move(int x, int y) {
        position.x = position.x + x / 300;
        position.y = position.y + y / 300;

        rotation = (float) getAngle(x, y);
//        flap.play(0.5f);
    }

    public static double getAngle(int x, int y) {

        return (180 / Math.PI) * Math.atan2(y, x)
                - 180; //note the atan2 call, the order of paramers is y then x
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
        flap.dispose();
    }

}