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
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision._btMprSimplex_t;
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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;

import java.awt.Color;


public class modgolf extends ApplicationAdapter {

	//phys
	public PhysicsEngine phys;

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
	public ModelInstance golfbol;
	public ModelInstance putt;


	//materials
	public Material gball;

	//skybox etc
	public Environment environment;


	//states
	public boolean loading;



	public Model createshit(){

		phys = new PhysicsEngine("C:\\Users\\liams\\Documents\\Java Projects\\Shithole\\Group22-phase-one\\modestgolfalpha1\\core\\src\\com\\mga1\\game\\example_inputfile.txt");

		Material grass = new Material();
		grass.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\Shithole\\Group22-phase-one\\modestgolfalpha1\\Textures\\grass texture.png"))));

		final int divisions = 128;
		final int dimension = 512;
		final int view = dimension / divisions;

		bbuilder.begin();
		MeshPartBuilder part = bbuilder.part("",
			GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, grass);

		part.setUVRange(0,0,1,1);

		for(int x = 0; x < divisions -1;  x++){
			for(int y = 0; y < divisions -1; y++) {

				VertexInfo v00 = new VertexInfo().set(new Vector3((x * (view)), (float)phys.getHeight(x,y), y * -view), null, null, new Vector2(0,0));

				VertexInfo v10 = new VertexInfo().set(new Vector3((x+1) * view, (float)phys.getHeight(x,y), y * -view), null, null, new Vector2(1,0));

				VertexInfo v11 = new VertexInfo().set(new Vector3((x+1) * view, (float)phys.getHeight(x,y), (y+1) * -view), null, null, new Vector2(1,1));

				VertexInfo v01 = new VertexInfo().set(new Vector3((x * (view)), (float)phys.getHeight(x,y), (y+1) * -view), null, null, new Vector2(0,1));


				part.rect(v00, v10, v11, v01);
			}
		}

		return bbuilder.end();
	}





	@Override
	public void create () {



		bbuilder = new ModelBuilder();

		testfloor = createshit();
		Material water = new Material();
		water.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\Shithole\\Group22-phase-one\\modestgolfalpha1\\Textures\\water-pool.jpg"))));
		Material gballs = new Material();
		gballs.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\Shithole\\Group22-phase-one\\modestgolfalpha1\\Textures\\Golfball.png"))));
		Model golfball = bbuilder.createSphere(3,3,3,32,32,gballs,Usage.Position | Usage.Normal);
		Model waterp = bbuilder.createBox(512f,1f,512f, water, Usage.Position | Usage.Normal);
		Model skyfix = bbuilder.createBox(1000,1000,1000, water, Usage.Position | Usage.Normal);
		Model flagpole = new Model();
		flagpole = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\Shithole\\Group22-phase-one\\modestgolfalpha1\\models\\flagpole.g3dj"));

		sky = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\Shithole\\Group22-phase-one\\modestgolfalpha1\\models\\fixedskyob.g3dj"));
		putter = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("C:\\Users\\liams\\Documents\\Java Projects\\Shithole\\Group22-phase-one\\modestgolfalpha1\\models\\barbellputter.g3dj"));

		ModelInstance penaltywater = new ModelInstance(waterp,0, -10, 0);
		golfbol = new ModelInstance(golfball, 0, 1, 0);
		ModelInstance skybox = new ModelInstance(sky);
		ModelInstance ground = new ModelInstance(testfloor, -256, 0, 256);
		ModelInstance skybox2 = new ModelInstance(skyfix);
		ModelInstance flag = new ModelInstance(flagpole, -128,-10,-128);
		putt = new ModelInstance(putter,5,1,0);


		instances.add(flag);
		instances.add(penaltywater);
		instances.add(golfbol);
		instances.add(skybox);
		instances.add(ground);
		instances.add(putt);

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
		int i = 0;
		camController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(instances, environment);


		while (true) {
			phys.runSimulation(getBall().x, getBall().y);
			moveBall(phys.stateVector[0], phys.stateVector[1]);

			if (phys.stateVector[2] == 0 && phys.stateVector[3] == 0);
				break;
		}
		modelBatch.end();
	}

	public void moveBall(double x, double y){
		if (x <= -128 && x >= -200 || y <= -128 && y >= -200){
			return;
		}
		float nx = ((Double)x).floatValue();
		float nz = ((Double)y).floatValue();
		float ny = (float)Math.pow((phys.getHeight(x,y)),100.0)*2+2;
		golfbol.transform.setToTranslation(nx, ny, nz);

	}
	public Vector3 getBall(){
		Vector3 pos = new Vector3();
		pos = golfbol.transform.getTranslation(pos);

		return pos;
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