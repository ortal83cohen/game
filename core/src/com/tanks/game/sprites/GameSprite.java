package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Polygon;
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


    protected Polygon boundsPoly;

    public void setPolygon(int x, int y) {
        boundsPoly = new Polygon(new float[] {
                x, y,
                x, y + glowSprite.getHeight()/2,
                x + glowSprite.getWidth() /2, y + glowSprite.getHeight()/2,
                x + glowSprite.getWidth() /2, y
        });
        boundsPoly.setOrigin(glowSprite.getWidth()/2, glowSprite.getHeight()/2);
    }

    public Vector3 getPosition() {
        return position;
    }

    public Sprite getSprite() {
        return glowSprite;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Polygon getBoundsPolygon() {
        return boundsPoly;
    }
    public boolean collides(Polygon polygon){
        return Intersector.overlapConvexPolygons(boundsPoly, polygon);
    }
}
