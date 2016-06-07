package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Brent on 7/5/2015.
 */
public class Bullet extends GameSprite {

    private Texture texture;

    private Sound flap;

    private float rotation;

    private int directionX;

    private int directionY;

    public Bullet(int x, int y, float rotation, int directionX, int directionY,Texture texture) {
        position = new Vector2(x, y);

        this.texture = texture;

        glowSprite = new Sprite(texture);
        getSprite().scale(-0.5f);
        setPolygon();
        boundsPoly.scale(-0.65f);
        flap = Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg"));
        flap.play(0.5f);

        this.directionX = directionX;
        this.directionY = directionY;

        this.rotation = rotation;

    }

    public void update(float dt) {
        position.x = position.x + (float) directionX * dt;
        position.y = position.y + (float) directionY * dt;

        boundsPoly.setPosition(position.x, position.y);
        boundsPoly.setRotation(rotation);
        glowSprite.setPosition(getPosition().x, getPosition().y);
        glowSprite.setRotation(rotation);

    }


    public void dispose() {
        texture.dispose();
        flap.dispose();
    }

}