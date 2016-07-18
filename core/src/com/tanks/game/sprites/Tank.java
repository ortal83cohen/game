package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.tanks.game.data.TankCharacteristics;
import com.tanks.game.utils.Assets;

/**
 *
 */
public class Tank extends Entity {

    protected final Label label;

    protected final String textureFileName;

    protected final Texture texture;

    private final String id;

    private final Sprite shieldSprite;

    protected String playerName;

    protected TankCharacteristics mTankCharacteristics;

    protected float maxSpeed =500;

    protected float rotation;

    protected boolean alive = true;


    public Tank(World world, String id, String textureFileName, int x, int y,
            short type, String playerName) {
        super(world);
        this.id = id;
        this.playerName = playerName;
        this.textureFileName = textureFileName;
        texture = Assets.getInstance().getManager().get(textureFileName);
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        shieldSprite = new com.badlogic.gdx.graphics.g2d.Sprite(new Texture("shield.png"));
        shieldSprite.setAlpha(0);
        getSprite().scale(-0.5f);
        createBody(world, x, y, type);
        label = new Label(playerName, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        label.setFontScale(0.5f);
//        stage.addActor(label);
        mTankCharacteristics = new TankCharacteristics();
    }

    private void createBody(World world, int x, int y, short type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(glowSprite.getWidth() * 0.5f * glowSprite.getScaleX(),
                glowSprite.getHeight() * 0.5f * glowSprite.getScaleY());
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 100f;
        fixtureDef.filter.categoryBits = type;
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();


    }

    public boolean update(float dt) {
        if (!body.getLinearVelocity().isZero()) {
            body.setTransform(body.getPosition(),getRotation());
               //     (float) (300 - Math   .atan2((double) body.getLinearVelocity().x, (double) body.getLinearVelocity().y)));
        }
        label.setPosition(body.getPosition().x - glowSprite.getWidth() / 2, body.getPosition().y + 10);
        glowSprite.setPosition(body.getPosition().x - glowSprite.getWidth() / 2,
                body.getPosition().y - glowSprite.getHeight() / 2);
        glowSprite.setRotation((getAngle() * 180) / (float) Math.PI);
        shieldSprite.setAlpha(getTankCharacteristics().getShield()/5);
        shieldSprite.setPosition(body.getPosition().x - shieldSprite.getWidth() / 2,
                body.getPosition().y - shieldSprite.getHeight() / 2);
        shieldSprite.setRotation((getAngle() * 180) / (float) Math.PI);
        if (!alive) {
            if(body.getFixtureList().size !=0) {
                body.destroyFixture(body.getFixtureList().first());
            }
            dispose();
            return false;
        }
        return true;
    }

    public float getRotation() {
        return rotation;
    }

    public void draw(SpriteBatch sb) {
        glowSprite.draw(sb);
        label.draw(sb, 0.7f);
        shieldSprite.draw(sb);
    }

    public void dispose() {
                body.setActive(false);

    }

    public Vector2 getSpeed() {
        return body.getLinearVelocity();
    }

    public void hit(int damage) {
        mTankCharacteristics.addResistant(-damage + mTankCharacteristics.getShield());
        if (mTankCharacteristics.getResistant() <= 0) {
            alive = false;
        }
    }

    public TankCharacteristics getTankCharacteristics() {
        return mTankCharacteristics;
    }
}