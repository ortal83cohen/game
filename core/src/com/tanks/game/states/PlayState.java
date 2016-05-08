package com.tanks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.tanks.game.TanksDemo;
import com.tanks.game.sprites.Tank;

/**
 * Created by Brent on 7/5/2015.
 */
public class PlayState extends State {
    private static final int GROUND_Y_OFFSET = -50;

    private Tank mTank;
    private Texture bg;
    private Vector2 groundPos1, groundPos2;


    public PlayState(com.tanks.game.states.GameStateManager gsm) {
        super(gsm);
        mTank = new Tank(40,200);
        cam.setToOrtho(false, TanksDemo.WIDTH / 2, TanksDemo.HEIGHT / 2);
        bg = new Texture("bg.png");


    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched())
            mTank.jump();
    }

    @Override
    public void update(float dt) {
        handleInput();

        mTank.update(dt);
        cam.position.x = mTank.getPosition().x + 80;



//
//            if(cam.position.x - (cam.viewportWidth / 2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()){
//                tube.reposition(tube.getPosTopTube().x  + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
//            }
//
//            if(tube.collides(mTank.getBounds()))
//                gsm.set(new MenuState(gsm));



        cam.update();

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth / 2), 0);
        sb.draw(mTank.getTexture(), mTank.getPosition().x, mTank.getPosition().y);



        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        mTank.dispose();



        System.out.println("Play State Disposed");
    }


}
