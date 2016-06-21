package com.tanks.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tanks.game.utils.Assets;
import com.tanks.game.utils.CollisionManager;
import com.tanks.game.utils.Collisionable;
import com.tanks.game.utils.MathUtil;
import com.tanks.game.utils.Type;

/**
 *
 */
public class Tank extends Entity implements Collisionable {

    private final String id;
    protected final String textureFileName;
    protected final Texture texture;

    public float directionX;//tmp for enemies

    public float directionY;//tmp for enemies

    protected boolean alive = true;

    protected int maxSpeed;

    protected Animation birdAnimation;

    protected float speed;

    protected Type type;

    protected Body body;

    public Tank(World world, String id, String textureFileName, int x, int y, Type type, CollisionManager collisionManager) {
//        super(id, collisionManager);
        super();
        this.id = id;
        this.textureFileName = textureFileName;
        texture = Assets.getInstance().getManager().get(textureFileName);
        glowSprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
        setPolygon();
//        this.collisionManager.register(this);
        this.type = type;
        position = new Vector2(x, y);
        createBody(world, x, y);
    }

    private void createBody(World world, int x, int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();
        //bounds poly not initialized yet!
//        shape.set(boundsPoly.getVertices());
        shape.setAsBox(glowSprite.getWidth() * 0.5f , glowSprite.getHeight() * 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();

        //linear damping to slow down when applying force
        body.setLinearDamping(4f);
    }

    public boolean update(float dt) {

        position.x = body.getPosition().x;
        position.y = body.getPosition().y;
//        position.x = position.x + (directionX * dt * speed);
//        position.y = position.y + (directionY * dt * speed);
//        Gdx.app.log("SocketIO", "playerUpdate x" + position.x + " y" + position.y);
        float rotation = (float) MathUtil.getAngle(directionX, directionY);
        birdAnimation.update(dt);//animation example
        boundsPoly.setPosition(position.x, position.y);
        boundsPoly.setRotation(rotation);
        glowSprite.setPosition(getPosition().x, getPosition().y);
        glowSprite.setRotation(rotation);

        return true;
    }

    @Override
    public boolean hasMoved() {
        return speed > 0;
    }

    public float getRotation() {
        return (float) MathUtil.getAngle(directionX, directionY);
    }


    public void dispose() {
        alive = false;
//        collisionManager.unregister(this);
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public boolean hasCollisionBehaviorWith(Type type) {
        return type.equals(Type.TOP_WALL) || type.equals(Type.RIGHT_WALL) || type.equals(Type.LEFT_WALL) || type.equals(Type.BOTTOM_WALL);
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public void collideWith(Collisionable collisionable) {

    }

}