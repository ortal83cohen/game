package com.tanks.game.utils;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;
import com.tanks.game.sprites.Bullet;

/**
 * Created by shlomi on 11/6/2016.
 */
public class BulletPool extends Pool<Bullet> {

    private final CollisionManager collisionManager;

    private Texture bulletTexture;

    private Sound fireSound;

    public BulletPool(Texture bulletTexture, Sound fireSound, CollisionManager collisionManager) {
        this.bulletTexture = bulletTexture;
        this.fireSound = fireSound;
        this.collisionManager = collisionManager;
    }

    @Override
    protected Bullet newObject() {
        return new Bullet(null, bulletTexture, fireSound, collisionManager);
    }

    public Bullet obtainAndFire(String ownerId, int x, int y, float rotation, float directionX,
            float directionY) {
        Bullet bullet = obtain();
        bullet.fire(ownerId, x, y, rotation, directionX, directionY);
        return bullet;
    }

    public void dispose() {
        bulletTexture.dispose();
        fireSound.dispose();
    }

}
