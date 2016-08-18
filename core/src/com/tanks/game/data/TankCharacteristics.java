package com.tanks.game.data;

/**
 * Created by cohenort on 12/07/2016.
 */
public class TankCharacteristics {
    private float resistant = 100;
    private float shield = 0;
    private int bulletSpeed = 100;
    private int bulletsNumber = 5;
    private float bulletsCoolDown = 0.4f;
    private float normalAcceleration = 0.9f;
    private float acceleration = normalAcceleration;
    private float engineHit = 0;

    public float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public float getEngineHit() {
        return engineHit;
    }

    public void setEngineHit(float engineHit) {
        this.engineHit = engineHit;
    }

    public float getShield() {
        return shield;
    }

    public void setShield(float shield) {
        this.shield = shield;
    }

    public void addShield(float shield) {
        this.shield += shield;
    }

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

    public float getResistant() {
        return resistant;
    }
    public void setResistant(float resistant) {
        this.resistant = resistant;
    }
    public void addResistant(float resistant) {
        this.resistant += resistant;
    }
}
