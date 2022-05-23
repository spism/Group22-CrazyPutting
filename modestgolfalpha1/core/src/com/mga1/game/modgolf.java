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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision._btMprSimplex_t;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.Input.Keys;




public class modgolf extends Game{

	//phys
	public PhysicsEngine phys = new PhysicsEngine("modestgolfalpha1\\core\\src\\com\\mga1\\game\\example_inputfile.txt");

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
	public Material grass;
	public Material gball;

	//skybox etc
	public Environment environment;


	//states
	public boolean loading;



	public Model createstuff(){


		phys = new PhysicsEngine("modestgolfalpha1\\core\\src\\com\\mga1\\game\\example_inputfile.txt");

		grass = new Material();
		grass.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture(Gdx.files.internal("modestgolfalpha1\\Textures\\grass texture.png"))));

		final int divisions = 128;
		final int dimension = 200;
		final int view = dimension / divisions;

		bbuilder.begin();
		MeshPartBuilder part = bbuilder.part("",
			GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, grass);

		part.setUVRange(0,0,1,1);

		for(int x = 0; x < divisions -1;  x++){
			for(int y = 0; y < divisions -1; y++) {

				VertexInfo v00 = new VertexInfo().set(new Vector3((x * (view)-100), (float)phys.function(x* (view)-100,y* (view)-100), y * -view+100), null, null, new Vector2(0,0));

VertexInfo v10 = new VertexInfo().set(new Vector3((x+1) * view-100, (float)phys.function((x+1)* (view)-100,y* (view)-100), y * -view+100), null, null, new Vector2(1,0));

VertexInfo v11 = new VertexInfo().set(new Vector3((x+1) * view-100, (float)phys.function((x+1)* (view)-100,(y+1)* (view)-100), (y+1) * -view+100), null, null, new Vector2(1,1));

VertexInfo v01 = new VertexInfo().set(new Vector3((x * (view))-100, (float)phys.function(x* (view)-100,(y+1)* (view)-100), (y+1) * -view+100), null, null, new Vector2(0,1));


				

			//	VertexInfo v00 = new VertexInfo().set(new Vector3((x * (view)), (float)phys.function(x,y), y * -view), null, null, new Vector2(0,0));

			//	VertexInfo v10 = new VertexInfo().set(new Vector3((x+1) * view, (float)phys.function(x+1,y), y * -view), null, null, new Vector2(1,0));

			//	VertexInfo v11 = new VertexInfo().set(new Vector3((x+1) * view, (float)phys.function(x+1,y+1), (y+1) * -view), null, null, new Vector2(1,1));

			//	VertexInfo v01 = new VertexInfo().set(new Vector3((x * (view)), (float)phys.function(x,y+1), (y+1) * -view), null, null, new Vector2(0,1));


				part.rect(v00, v10, v11, v01);
			}
		}

		return bbuilder.end();
	}





	@Override
	public void create () {

	

		Material grass2 = new Material();
		grass2.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture(Gdx.files.internal("modestgolfalpha1\\Textures\\grass texture.png"))));
		bbuilder = new ModelBuilder();

		testfloor = createstuff();
		Material water = new Material();
		Material wallMaterial = new Material();
		wallMaterial.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture(Gdx.files.internal("modestgolfalpha1\\Textures\\carbontext.jpg"))));
		water.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture(Gdx.files.internal("modestgolfalpha1\\Textures\\water-pool.jpg"))));
		Material gballs = new Material();
		gballs.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture(Gdx.files.internal("modestgolfalpha1\\Textures\\Golfball.png"))));
		Model golfball = bbuilder.createSphere(3,3,3,32,32,gballs,Usage.Position | Usage.Normal);
		Model waterp = bbuilder.createBox(128f,1f,128f, water, Usage.Position | Usage.Normal);
		Model skyfix = bbuilder.createBox(1000,1000,1000, water, Usage.Position | Usage.Normal);
		Model skyball = bbuilder.createSphere(-9000,-9000,-9000,64,64,water,Usage.Position | Usage.Normal);
		Model flagpole = new Model();
		Model wall = bbuilder.createBox((float)Math.abs(phys.x2Wall - phys.x1Wall) * 10, 30,(float)Math.abs(phys.y2Wall - phys.y1Wall), wallMaterial, Usage.Position | Usage.Normal);
		Model grassp = bbuilder.createBox(256f,1f,256f,grass2, Usage.Position | Usage.Normal);
		flagpole = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("modestgolfalpha1\\models\\flagpole.g3dj"));

		sky = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("modestgolfalpha1\\models\\simplesky.g3dj"));
		putter = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("modestgolfalpha1\\models\\putterfix.g3dj"));

		ModelInstance penaltywater = new ModelInstance(waterp,64, -0.7f, 192);
		ModelInstance penaltygrass = new ModelInstance(grassp,128, -8, 128);
		golfbol = new ModelInstance(golfball, 0, 0, 0);
		ModelInstance skybox = new ModelInstance(sky);
		ModelInstance wallInstance = new ModelInstance(wall, (float)phys.x1Wall + 64, (float)(phys.function(phys.x1Wall, phys.y1Wall)) - 4, (float)(phys.y1Wall) + 190);
		ModelInstance ground = new ModelInstance(testfloor,65,0,193);	
		ModelInstance skybox2 = new ModelInstance(skyfix);
		ModelInstance flag = new ModelInstance(flagpole, (float)phys.targetX+64,(float)(phys.function(phys.targetX,phys.targetY))-4,(float)(phys.targetY)+190);
		putt = new ModelInstance(putter,5,0,0);
		ModelInstance ball = new ModelInstance(skyball,0,0,0);






		instances.add(flag);
		instances.add(penaltywater);
		instances.add(golfbol);
		instances.add(ball);
		instances.add(ground);
		instances.add(putt);
		instances.add(wallInstance);

		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));


		cam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 50f, 256f);
		cam.lookAt(0f, 0f, -256f);
		cam.near = 1f;
		cam.far = 999999f;
		cam.update();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);
	}

// GUI models are not correct to scale for what the input file says just look at the command terminal not GUI 
	@Override
	public void render () {
		
		camController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(instances, environment);

		moveBall((float) phys.stateVector[0], (float) phys.stateVector[1]);
		cam.lookAt(golfbol.transform.getTranslation(new Vector3()));
					cam.update();

	


				if (Gdx.input.isKeyPressed(Keys.R)) {
					cam.position.set(65f, 75f, 200f);
					cam.lookAt(golfbol.transform.getTranslation(new Vector3()));
					cam.update();
					System.out.println("XPOS: " + phys.stateVector[0] + " YPOS: " + phys.stateVector[1]);
					System.out.println("Height: " + phys.function(phys.stateVector[0], phys.stateVector[1]));
					int i = 0;
					while(i < 1000){
					phys.runSimulation(3, 0, 2);
					i = i + 1;
					}
					moveBall((float) phys.stateVector[0], (float) phys.stateVector[1]);
					System.out.println("Distance to hole " + (Math.pow((phys.stateVector[0]-phys.targetX),2)+Math.pow((phys.stateVector[1]-phys.targetY), 2)));

					if ((Math.pow((phys.stateVector[0]-phys.targetX),2)+Math.pow((phys.stateVector[1]-phys.targetY), 2)<= Math.pow(phys.targetRadius, 2))) {
						System.out.println("You Win!");
					}
					if (phys.stateVector[2] == 0 && phys.stateVector[3] == 0) {
						System.out.println("You lose!");
					}

				}
		modelBatch.end();
	}

	public void moveBall(float x, float y){

		float nx = (float)x;
		float nz = (float)y;
		float ny = (float)(phys.function(phys.stateVector[0],phys.stateVector[1]))+1.3f;
		golfbol.transform.setToTranslation(x+64, ny, -(y-192));
		putt.transform.setToTranslation(x+64,ny,-(y-192));

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
