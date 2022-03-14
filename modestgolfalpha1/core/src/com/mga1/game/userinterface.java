package com.mga1.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import jdk.jfr.internal.settings.PeriodSetting;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

public class userinterface extends Game {

    private OrthographicCamera camera;

    // buttons
private Stage stage;
private Texture playbutton;
private TextureRegion playregion;
private TextureRegionDrawable playdraw;
private ImageButton button;

    private Texture exitbutton;
    private TextureRegion exitregion;
    private TextureRegionDrawable exitdraw;
    private ImageButton xbutton;


    private Texture setbutton;
    private TextureRegion setregion;
    private TextureRegionDrawable setdraw;
    private ImageButton sbutton;


    private Texture scobutton;
    private TextureRegion scoregion;
    private TextureRegionDrawable scodraw;
    private ImageButton scbutton;


    public void create(){
        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,480);

        playbutton = new Texture(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\modestgolfalpha1\\Textures\\PlayButton.png"));
        playregion = new TextureRegion(playbutton);
        playdraw = new TextureRegionDrawable(playdraw);
        button = new ImageButton(playdraw);

        exitbutton = new Texture(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\modestgolfalpha1\\Textures\\ExitButton.png"));
        exitregion = new TextureRegion(exitbutton);
        exitdraw = new TextureRegionDrawable(exitdraw);
        xbutton = new ImageButton(exitdraw);

        setbutton = new Texture(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\modestgolfalpha1\\Textures\\SettingsButton.png"));
        setregion = new TextureRegion(setbutton);
        setdraw = new TextureRegionDrawable(setdraw);
        sbutton = new ImageButton(setdraw);

        scobutton = new Texture(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\modestgolfalpha1\\Textures\\ScoresButton.png"));
        scoregion = new TextureRegion(scobutton);
        scodraw = new TextureRegionDrawable(scodraw);
        scbutton = new ImageButton(scodraw);

        stage = new Stage(new ScreenViewport());
        stage.addActor(button);
        stage.addActor(sbutton);
        stage.addActor(xbutton);
        stage.addActor(scbutton);
        Gdx.input.setInputProcessor(stage);
    }
    public void render(){
        ScreenUtils.clear(0,0,0.2f,1);
        camera.update();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

}
