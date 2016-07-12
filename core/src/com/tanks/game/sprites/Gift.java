package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tanks.game.utils.Type;


public class Gift extends Entity {

    protected Texture texture;

    protected boolean alive = true;

    public Gift(World world, float x, float y) {
        super(world);

        this.texture = new Texture("gift.png");
//        this.texture = Assets.getInstance().getManager().get("stone.png");
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        glowSprite.setPosition(x, y);
        createBody(x, y,Type.GIFT);

    }

    private void createBody(float x, float y, short type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x + glowSprite.getWidth() * 0.5f, y + glowSprite.getHeight() * 0.5f);
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(glowSprite.getWidth() /2 ,
                glowSprite.getHeight() /2 );
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = type;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

    }

    public boolean update(float dt) {
        if(!alive){
            body.setActive(false);
            return false;
        }
        return true;
    }

    public void dispose() {
        texture.dispose();
    }

    public void hit() {
        alive = false;
    }
}