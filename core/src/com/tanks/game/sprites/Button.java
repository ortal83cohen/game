package com.tanks.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;


/**
 * Created by Brent on 7/5/2015.
 */
public class Button {

    private final TextButton button;

    private Texture texture;

    public Button(Stage stage, int x, int y) {
        super();
        texture = new Texture("button.png");

        Gdx.input.setInputProcessor(stage);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();
        textButtonStyle.up = new Image(texture).getDrawable();
        textButtonStyle.down = new Image(texture).getDrawable();
        textButtonStyle.checked = new Image(texture).getDrawable();
        button = new TextButton("Button1", textButtonStyle);
        button.setBounds(x, y, 150, 150);
        stage.addActor(button);
    }

    public TextButton getButton() {
        return button;
    }


    public void dispose() {
        texture.dispose();
    }


}