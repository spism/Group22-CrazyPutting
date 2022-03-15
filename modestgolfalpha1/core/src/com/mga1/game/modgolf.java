package com.mga1.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ScreenUtils;
import jdk.jfr.internal.settings.PeriodSetting;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import jdk.internal.loader.Loader;





public class modgolf extends ApplicationAdapter {


	//camera
	public PerspectiveCamera cam;
	public CameraInputController camController;

	//models
	public ModelInstance inst;
	public ModelBatch modelBatch;
	public ModelBuilder bbuilder;
	public Model testfloor;
	public Model sky;
	public Model ball;
	public Model putter;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	//skybox etc
	public Environment environment;


	//states
	public boolean loading;


	public class Terrain {
		private static final float SIZE = 800;
		private static final int VERTEX_COUNT = 128;

		public float x;
		public float z;
		public Model terra;
		public ModelTexture terratex;

		public Terrain(int gridx, int gridz, Loader loader, ModelTexture texture){
			this.terratex = texture;
			this.x = gridx * SIZE;
			this.z = gridz * SIZE;
		}
	}


	@Override
	public void create () {

		bbuilder = new ModelBuilder();

		testfloor = bbuilder.createBox(1000f, 1f, 1000f, new Material(ColorAttribute.createDiffuse(new Color(0, 255, 0, 1))), Usage.Position | Usage.Normal);

		sky = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\Shithole\\Group22-phase-one\\modestgolfalpha1\\models\\simplesky.g3dj"));
		ball = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\Shithole\\Group22-phase-one\\modestgolfalpha1\\models\\golfballS.g3dj"));
		putter = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\Shithole\\Group22-phase-one\\modestgolfalpha1\\models\\gun.g3dj"));

		ModelInstance gball = new ModelInstance(ball, 0, 2, 0);
		ModelInstance pputter = new ModelInstance(putter,0,0,0);
		ModelInstance skybox = new ModelInstance(sky);
		ModelInstance ground = new ModelInstance(testfloor);

		instances.add(gball);
		instances.add(pputter);
		instances.add(skybox);
		instances.add(ground);

		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));


		cam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(50f, 50f, 50f);
		cam.lookAt(0, 5, 0);
		cam.near = 1f;
		cam.far = 999999f;
		cam.update();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);
	}


	@Override
	public void render () {
		camController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		modelBatch.end();
	}

	@Override
	public void dispose () {

		modelBatch.dispose();
	}

	public void resume () {
	}

	public void resize (int width, int height) {
	}

	public void pause () {
	}
}