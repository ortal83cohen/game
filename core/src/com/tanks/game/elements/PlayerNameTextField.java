package com.tanks.game.elements;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created by cohenort on 29/06/2016.
 */
public class PlayerNameTextField extends TextField {


    public PlayerNameTextField(Stage stage, String text, Skin skin) {
        super(text, skin);
        stage.addActor(this);
    }

    public PlayerNameTextField(Stage stage, String text, Skin skin, String styleName) {
        super(text, skin, styleName);
        stage.addActor(this);
    }

    public PlayerNameTextField(Stage stage, String text, TextFieldStyle style) {
        super(text, style);
        stage.addActor(this);
    }
}
