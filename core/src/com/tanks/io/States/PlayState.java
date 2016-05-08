package com.tanks.io.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.tanks.io.Sprites.Tank;
import com.tanks.io.TanksIo;


/**
 * Created by Brent on 6/25/2015.
 */
public class PlayState extends State {
    private static final int GROUND_Y_OFFSET = -30;
    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT = 4;

    private Tank mTank;
    private Texture background;
    private Texture ground;
    private Texture gameoverImg;
    private Vector2 groundPos1;
    private Vector2 groundPos2;
    private ShapeRenderer sr;

    private boolean gameover;

    public PlayState(GameStateManager gsm){
        super(gsm);
        mTank = new Tank((TanksIo.WIDTH / 2) , TanksIo.HEIGHT / 2);
        background = new Texture("bg.png");
        ground = new Texture("ground.png");
        gameoverImg = new Texture("gameover.png");

        for(int i = 1; i <= TUBE_COUNT; i++)

        groundPos1 = new Vector2(cam.position.x - cam.viewportWidth / 2, GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) + ground.getWidth(), GROUND_Y_OFFSET);
        gameover = false;
    }
    @Override
    public void handleInput() {
        if(Gdx.input.isTouched()) {
            if(gameover)
                gsm.set(new PlayState(gsm));
            else
                mTank.jump();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        updateGround();
        mTank.update(dt);
        cam.position.set(mTank.getX() + 80, cam.viewportHeight / 2, 0);


        if(mTank.getY() <= ground.getHeight() + GROUND_Y_OFFSET){
            gameover = true;
            mTank.colliding = true;
        }
        cam.update();
    }

    public void updateGround(){
        if(cam.position.x - (cam.viewportWidth / 2) > groundPos1.x + ground.getWidth())
            groundPos1.add(ground.getWidth() * 2, 0);
        if(cam.position.x - (cam.viewportWidth / 2) > groundPos2.x + ground.getWidth())
            groundPos2.add(ground.getWidth() * 2, 0);

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, cam.position.x - (cam.viewportWidth / 2), 0);
        //System.out.println("X: " + groundPos1.x + " Y: " + groundPos1.y);

        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);
        sb.draw(mTank.getTexture(), mTank.getX(), mTank.getY());
        if(gameover)
            sb.draw(gameoverImg, cam.position.x - gameoverImg.getWidth() / 2, cam.position.y);
        sb.end();
    }
}
