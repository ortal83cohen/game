package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.tanks.game.utils.Assets;

/**
 *
 */
public class Tank extends Entity {

    protected final Label label;

    protected final String textureFileName;

    protected final Texture texture;

    private final String id;

    protected String playerName;

    private boolean alive = true;


    public Tank(World world, String id, String textureFileName, int x, int y,
            short type, String playerName) {
        super(world);
        this.id = id;
        this.playerName = playerName;
        this.textureFileName = textureFileName;
        texture = Assets.getInstance().getManager().get(textureFileName);
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        getSprite().scale(-0.5f);
        createBody(world, x, y, type);
        label = new Label(playerName, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        label.setFontScale(0.5f);
//        stage.addActor(label);
    }

    private void createBody(World world, int x, int y, short type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = false;

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
        if (body.getLinearVelocity().x !=0 && body.getLinearVelocity().y!=0) {
            body.setTransform(body.getPosition(), (float) (300 - Math
                    .atan2((double) body.getLinearVelocity().x, (double) body.getLinearVelocity().y)));
        }
        label.setPosition(body.getPosition().x - glowSprite.getWidth() / 2, body.getPosition().y + 10);
        glowSprite.setPosition(body.getPosition().x - glowSprite.getWidth() / 2,
                body.getPosition().y - glowSprite.getHeight() / 2);
        glowSprite.setRotation((getAngle() * 180) / (float) Math.PI);
        if (!alive) {
            dispose();
            return false;
        }
        return true;
    }

    public float getRotation() {
        return body.getAngle();
    }

    public void draw(SpriteBatch sb) {
        glowSprite.draw(sb);
        label.draw(sb, 0.7f);
    }

    public void dispose() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                body.setActive(false);
//                world.destroyBody(body);
            }
        });

    }

    public Vector2 getSpeed() {
        return body.getLinearVelocity();
    }

    public void hit(int damage) {
        resistant = resistant - damage;
        if (resistant <= 0) {
            alive = false;
        }
    }
}