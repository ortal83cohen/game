package com.tanks.game.data;

/**
 * Created by cohenort on 12/07/2016.
 */
public class TankCharacteristics {
    private int resistant = 100;
    private int bulletSpeed = 100;
    private int bulletsNumber = 5;
    private float bulletsCoolDown = 0.4f;

    public int getBulletsNumber() {
        return bulletsNumber;
    }

    public void setBulletsNumber(int bulletsNumber) {
        this.bulletsNumber = bulletsNumber;
    }

    public float getBulletsCoolDown() {
        return bulletsCoolDown;
    }

    public void setBulletsCoolDown(float bulletsCoolDown) {
        this.bulletsCoolDown = bulletsCoolDown;
    }

    public int getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(int bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }
    public void addBouletSpeed(int bulletSpeed) {
        this.bulletSpeed += bulletSpeed;
    }

    public int getResistant() {
        return resistant;
    }
    public void setResistant(int resistant) {
        this.resistant = resistant;
    }
    public void addResistant(int resistant) {
        this.resistant += resistant;
    }
}
