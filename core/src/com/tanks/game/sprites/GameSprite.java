package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;
import java.util.IllegalFormatWidthException;

/**
 * Created by ortalcohen on 03/06/2016.
 */
public abstract class GameSprite {

    protected  Sprite glowSprite;

    protected Vector3 position;

    protected com.badlogic.gdx.math.Rectangle bounds;


    public Vector3 getPosition() {
        return position;
    }

    public Sprite getSprite() {
        return glowSprite;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean collides(Rectangle rectangle){
        return rectangle.overlaps(bounds) ;
    }
}
