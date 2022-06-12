package com.mga1.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScreen implements Screen{
    private modgolf game;
    private Stage stage;
    private String x0;
    private String y0;
    private String xT;
    private String yT;
    private String radius;
    private String muk;
    private String mus;
    private String heightProfile;
    public String getHeightProfile() {
        return heightProfile;
    }
    public String getMus() {
        return mus;
    }
    public String getMuk() {
        return muk;
    }
    public String getRadius() {
        return radius;
    }
    public String getX0() {
        return x0;
    }
    public String getY0() {
        return y0;
    }
    public String getxT() {
        return xT;
    }
    public String getyT() {
        return yT;
    }

    public MenuScreen(modgolf firstGame) {
        super();
        this.game = firstGame;
        stage = new Stage(new StretchViewport(600, 800));
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        table.center();
        stage.addActor(table);
        Skin skin = new Skin(Gdx.files.internal("modestgolfalpha1\\skins\\glassy\\skin\\glassy-ui.json"));
        TextButton newGame = new TextButton("New Game", skin);
        //TextButton preferences = new TextButton("Preferences", skin);
        TextButton exit = new TextButton("Exit", skin);

        final TextField fieldX0 = new TextField("X0 = ", skin);
        final TextField fieldY0 = new TextField("Y0 = ", skin);
        final TextField fieldXt = new TextField("Xt = ", skin);
        final TextField fieldYt = new TextField("Yt = ", skin);
        final TextField fieldRadius = new TextField("Radius = ", skin);
        final TextField fieldMuk = new TextField("Muk = ", skin);
        final TextField fieldMus = new TextField("Mus = ", skin);
        final TextField fieldHeightProfile = new TextField("HeightProfile = ", skin);
        
        table.add(fieldX0).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(fieldY0).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(fieldXt).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(fieldYt).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(fieldRadius).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(fieldMuk).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(fieldMus).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(fieldHeightProfile).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(newGame).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        //table.add(preferences).fillX().uniformX();
        //table.row().pad(5, 0, 5, 0);
        table.add(exit).fillX().uniformX();
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();				
            }
        });
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String[] arr1 = fieldX0.getText().split(" ");
                x0 = arr1[arr1.length - 1];
                String[] arr2 = fieldY0.getText().split(" ");
                y0 = arr2[arr2.length - 1];
                String[] arr3 = fieldXt.getText().split(" ");
                xT = arr3[arr3.length - 1];
                String[] arr4 = fieldYt.getText().split(" ");
                yT = arr4[arr4.length - 1];
                String[] arr5 = fieldRadius.getText().split(" ");
                radius = arr5[arr5.length - 1];
                String[] arr6 = fieldMuk.getText().split(" ");
                muk = arr6[arr6.length - 1];
                String[] arr7 = fieldMus.getText().split(" ");
                mus = arr7[arr7.length - 1];
                heightProfile = fieldHeightProfile.getText();
                game.changeScreen(game.APPLICATION);
            }
        });
        
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
	    stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
