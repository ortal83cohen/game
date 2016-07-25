package com.tanks.game.utils;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.tanks.game.sprites.Bullet;

/**
 * Created by shlomi on 11/6/2016.
 */
public class BulletPool extends Pool<Bullet> {

    private final World world;

    private Texture bulletTexture;

    private Sound fireSound;

    public BulletPool(World world, Texture bulletTexture, Sound fireSound) {
        this.world = world;
        this.bulletTexture = bulletTexture;
        this.fireSound = fireSound;
    }

    @Override
    protected Bullet newObject() {
        return new Bullet(world, null, bulletTexture, fireSound);
    }

    public Bullet obtainAndFire(String ownerId, int x, int y, Vector2 direction, int speed) {
        Bullet bullet = obtain();
        bullet.fire(ownerId, x, y, direction.nor(), speed);
        return bullet;
    }

    public void dispose() {
        bulletTexture.dispose();
        fireSound.dispose();
    }

}
