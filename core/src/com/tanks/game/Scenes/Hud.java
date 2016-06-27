package com.tanks.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;


public class Hud implements Disposable {

    private static Label text1;

    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;

    //Scene2D widgets
    private Label text3;

    private Label title3;

    private Label text2;

    private Label title2;

    private Label title1;


    public Hud(Stage stage) {

        //define a table used to organize our hud's labels
        Table table = new Table();
        //Top-Align table
        table.top();
        //make the table fill the entire stage
        table.setFillParent(true);

        //define our labels using the String, and a Label style consisting of a font and color
        title1 = new Label("my id", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        text1 = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        title2 = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        text2 = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        title3 = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        text3 = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        //add our labels to our table, padding the top, and giving them all equal width with expandX
        table.add(title1).expandX().padTop(10);
        table.add(title2).expandX().padTop(10);
        table.add(title3).expandX().padTop(10);
        //add a second row to our table
        table.row();
        table.add(text1).expandX();
        table.add(text2).expandX();
        table.add(text3).expandX();

        //add our table to the stage

        //add our labels to our table, padding the top, and giving them all equal width with expandX

        stage.addActor(table);

    }

//        public void update(float dt){
//        timeCount += dt;
//        if(timeCount >= 1){
//            if (worldTimer > 0) {
//                worldTimer--;
//            } else {
//                timeUp = true;
//            }
//            text3.setText(String.format("%03d", worldTimer));
//            timeCount = 0;
//        }
//    }


    public void set1(String string) {
        text1.setText(string);
    }

    @Override
    public void dispose() {
    }


}
