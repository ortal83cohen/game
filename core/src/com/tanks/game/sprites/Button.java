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
public class Button extends GameSprite{

    private Texture texture;

    public Button(int x, int y) {
        position = new Vector3(x, y, 0);

        texture =  new Texture("button.png");

        glowSprite = new Sprite(texture);
        bounds = new Rectangle(x, y, glowSprite.getWidth() , glowSprite.getHeight());
    }


    public void update(float dt) {

        bounds.setPosition(position.x, position.y);

        glowSprite.setPosition(getPosition().x, getPosition().y);

    }


    public void setPosition(float x, float y) {
        position.x =  x ;
        position.y = y ;

    }

    public void dispose() {
        texture.dispose();
    }

}