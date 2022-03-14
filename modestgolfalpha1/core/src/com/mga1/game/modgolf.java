package com.mga1.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import jdk.jfr.internal.settings.PeriodSetting;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

public class modgolf extends ApplicationAdapter {

	//buttons and menu
	private Stage Stage;
	private Texture playbutton;
	private TextureRegion playzone;
	private TextureRegionDrawable playdraw;
	private ImageButton button;

	//other stuff

	public Texture funnyface;

	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public Model redpill;
	public Model ball;
	public ModelInstance instance;
	public Environment universe;
	public CameraInputController camController;
	
	@Override
	public void create () {

	}

	@Override
	public void render () {


	}
	
	@Override
	public void dispose () {

	}

	@Override
	public void resume ()  {

	}

	@Override
	public void pause () {

	}
}
