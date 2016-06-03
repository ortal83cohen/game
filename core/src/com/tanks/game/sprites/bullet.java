package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.tanks.game.utils.MathUtil;

/**
 * Created by Brent on 7/5/2015.
 */
public class Bullet extends GameSprite{

    private Texture texture;

    private Sound flap;

    private float rotation;

    private int directionX;

    private int directionY;

    public Bullet(int x, int y,float rotation,int directionX,int directionY) {
        position = new Vector3(x, y, 0);

         texture = new Texture("bullet.png");

        glowSprite = new Sprite(texture);
        getSprite().scale(-0.5f);
        bounds = new Rectangle(x, y, glowSprite.getWidth() , glowSprite.getHeight());
        flap = Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg"));
        flap.play(0.5f);

        this.directionX=directionX;
        this.directionY = directionY;

        this.rotation = rotation;

    }

    public void update(float dt) {
        position.x = position.x + (float) directionX *dt;
        position.y = position.y + (float) directionY *dt;

        bounds.setPosition(position.x, position.y);

        glowSprite.setPosition(getPosition().x, getPosition().y);
        glowSprite.setRotation(rotation);

    }



    public void dispose() {
        texture.dispose();
        flap.dispose();
    }

}