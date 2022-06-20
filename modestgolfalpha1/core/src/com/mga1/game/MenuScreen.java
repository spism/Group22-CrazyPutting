package com.mga1.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
    private String sandX1;
    private String sandY1;
    private String sandX2;
    private String sandY2;
    private String sandKinetic;
    private String sandStatic;
    private boolean hasSand;
    private boolean hasAI;
    private Integer solver;

    public Integer getSolver() {
        return solver;
    }
    public boolean isHasAI() {
        return hasAI;
    }

    ScreenViewport secondViewport;
    
    public String getSandY2() {
        return sandY2;
    }
    public String getSandX2() {
        return sandX2;
    }
    public String getSandY1() {
        return sandY1;
    }
    public String getSandKinetic() {
        return sandKinetic;
    }
    public String getSandStatic() {
        return sandStatic;
    }
    public boolean isHasSand() {
        return hasSand;
    }
    public String getSandX1() {
        return sandX1;
    }
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
        StretchViewport firstViewport = new StretchViewport(600, 800);
        
        stage = new Stage(firstViewport);
        Gdx.input.setInputProcessor(stage);
        //secondViewport = new ScreenViewport();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }
    private void makeChanges(final TextField fieldX0, final TextField fieldY0, final TextField fieldXt,
                    final TextField fieldYt, final TextField fieldRadius, final TextField fieldMuk,
                    final TextField fieldMus, final TextField fieldHeightProfile, final TextField fieldSandX,
                    final TextField fieldSandY, final TextField fieldSandKineticAndStatic, final TextField fieldSolver) {
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
                heightProfile = fieldHeightProfile.getText().split("= ")[1];
                String[] arr8 = fieldSandX.getText().split(" ");
                if (arr8.length > 2) {
                    sandX1 = arr8[2];
                    sandX2 = arr8[6];
                    hasSand = true;
                }
                else{
                    hasSand = false;
                }
                String[] arr9 = fieldSandY.getText().split(" ");
                if (arr9.length > 2) {
                    sandY1 = arr9[2];
                    sandY2 = arr9[6];
                    hasSand = true;
                }else{
                    hasSand = false;
                }
                String[] arr10 = fieldSandKineticAndStatic.getText().split(" ");
                if (arr10.length > 2) {
                    sandKinetic = arr10[2];
                    sandStatic = arr10[arr10.length-1];
                    hasSand = true;
                }else{
                    hasSand = false;
                }
                String[] arr11 = fieldSolver.getText().split(" ");
                try {
                    solver = Integer.parseInt(arr11[arr11.length - 1]);
                } catch (Exception e) {
                    solver = 0;
                }
                
                
                game.changeScreen(game.APPLICATION);
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
        TextButton ai = new TextButton("AI", skin);

        final TextField fieldX0 = new TextField("X0 = -1", skin);
        final TextField fieldY0 = new TextField("Y0 = -0.5", skin);
        final TextField fieldXt = new TextField("Xt = 4", skin);
        final TextField fieldYt = new TextField("Yt = 1", skin);
        final TextField fieldRadius = new TextField("Radius = 0.1", skin);
        final TextField fieldMuk = new TextField("Muk = 0.1", skin);
        final TextField fieldMus = new TextField("Mus = 0.2", skin);
        final TextField fieldHeightProfile = new TextField("HeightProfile = ( e ^ ( ( -1 * ( ( x ^ 2 ) + ( y ^ 2 ) ) ) / 40 ) )", skin);
        final TextField fieldSandX = new TextField("SandXCoordintates = ", skin);
        final TextField fieldSandY = new TextField("SandYCoordintates = ", skin);
        final TextField fieldSandKineticAndStatic = new TextField("SandKineticAndStatic = ", skin);
        final TextField fieldSolver = new TextField("Solver = 0", skin);


        // final ScrollPane scrollPane = new ScrollPane(table, skin);
        
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
        table.add(fieldSandX).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(fieldSandY).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(fieldSandKineticAndStatic).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(fieldSolver).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        table.add(newGame).fillX().uniformX();
        table.row().pad(5, 0, 5, 0);
        //table.add(preferences).fillX().uniformX();
        //table.row().pad(5, 0, 5, 0);
        table.add(ai).fillX().uniformX();
        ai.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //Gdx.app.exit();
                makeChanges(fieldX0, fieldY0, fieldXt, fieldYt, fieldRadius, fieldMuk, fieldMus, fieldHeightProfile,
                        fieldSandX, fieldSandY, fieldSandKineticAndStatic, fieldSolver);	
                hasAI = true;			
            }
        });
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                makeChanges(fieldX0, fieldY0, fieldXt, fieldYt, fieldRadius, fieldMuk, fieldMus, fieldHeightProfile,
                        fieldSandX, fieldSandY, fieldSandKineticAndStatic, fieldSolver);
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
