package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class Animation {

    private Array<Sprite> frames;

    private float maxFrameTime;

    private float currentFrameTime;

    private int frameCount;

    private int frame;

    public Animation(TextureRegion region, int frameCount, float cycleTime) {
        frames = new Array<Sprite>();
        int frameWidth = region.getRegionWidth() / frameCount;
        for (int i = 0; i < frameCount; i++) {
            Sprite sprite = new Sprite(new TextureRegion(region, i * frameWidth, 0, frameWidth,
                    region.getRegionHeight()));
            frames.add(sprite);
        }
        this.frameCount = frameCount;
        maxFrameTime = cycleTime / frameCount;
        frame = 0;
    }

    public void update(float dt) {
        currentFrameTime += dt;
        if (currentFrameTime > maxFrameTime) {
            frame++;
            currentFrameTime = 0;
        }
        if (frame >= frameCount) {
            frame = 0;
        }
    }

    public void setScale(float scail) {
        for (int i = 0; i < frames.size; i++) {
            Sprite f = frames.get(i);
            f.scale(scail);
            frames.set(i, f);
        }
    }

    public void setAlpha(float alpha) {
        for (int i = 0; i < frames.size; i++) {
            Sprite f = frames.get(i);
            f.setAlpha(alpha);
            frames.set(i, f);
        }
    }

    public void setRotation(float rotation) {
        for (int i = 0; i < frames.size; i++) {
            Sprite f = frames.get(i);
            f.rotate(rotation);
            frames.set(i, f);
        }
    }
    public void setPosition(float x, float y) {
        for (int i = 0; i < frames.size; i++) {
            Sprite f = frames.get(i);
            f.setPosition(x - f.getWidth()/2 ,y-f.getHeight()/2);
            frames.set(i, f);
        }
    }

    public Sprite getFrame() {
        return frames.get(frame);
    }
}
