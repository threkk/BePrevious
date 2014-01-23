package nl.hva.boxlabapp.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;

public class ModelRender {
	private PerspectiveCamera cam;
	private Environment environment;
	private ModelBatch modelBatch;
	private AssetManager assets;
	private Model hip, thigh, shin;
	private ModelInstance ihip, ithigh, ishin;

	public ModelRender() {

		this.modelBatch = new ModelBatch();

		// Camera
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.lookAt(0, 0, 0);
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update();

		// Environment
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f,
				0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f,
				-0.8f, -0.2f));

		// Assets
		assets = new AssetManager();
		assets.load("pie.g3dj", Model.class);
		assets.load("pierna.g3dj",Model.class);
		assets.load("cadera.g3dj", Model.class);
		// I feel dirty for this...
		while (!assets.update());
		shin = assets.get("pie.g3dj", Model.class);
		thigh = assets.get("pierna.g3dj", Model.class);
		hip = assets.get("cadera.g3dj", Model.class);
		ishin = new ModelInstance(shin);
		ithigh = new ModelInstance(thigh);
		ihip = new ModelInstance(hip);

		// // Controller
		// controller = new AnimationController(instance);
		// controller.setAnimation(instance.animations.get(0).id,-1);
	}

	public void render() {
		modelBatch.begin(cam);
		modelBatch.render(ihip, environment);
		modelBatch.render(ishin, environment);
		modelBatch.render(ithigh, environment);
		// controller.update(Gdx.graphics.getDeltaTime());
		modelBatch.end();
	}

	public void dispose() {
		modelBatch.dispose();
		hip.dispose();
		thigh.dispose();
		shin.dispose();
		// controller = null;
	}

	public Camera getCamera() {
		return cam;
	}
	
	public ModelInstance[] getInstance(){
		ModelInstance[] leg = {ihip, ithigh, ishin};
		return leg;
	}
}