package com.hva.boxlabapp.gdx;

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
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

public class DemoRenderer {
    private PerspectiveCamera cam;
    private Environment environment;
    private ModelBatch modelBatch;
    private AssetManager assets;
	private Model model;
	private ModelInstance instance;
	private AnimationController controller;
	
	public DemoRenderer() {
		
		this.modelBatch = new ModelBatch();
				
		// Camera
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();
        
        // Environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	
        // Assets
        assets = new AssetManager();
        assets.load("animation.g3db",Model.class);
        // I feel dirty for this...
        while(!assets.update());
        model = assets.get("animation.g3db", Model.class);
        instance = new ModelInstance(model);
        
        // Controller
        controller = new AnimationController(instance);
        // this little bastard needs to be fixed on Maya.
        controller.setAnimation("play");
	}
	
	
	public void render(){
		modelBatch.begin(cam);
		modelBatch.render(instance,environment);
		controller.update(Gdx.graphics.getDeltaTime());
		modelBatch.end();
	}
	
	public void dispose(){
		modelBatch.dispose();
	}
	
	public Camera getCamera(){
		return cam;
	}
}
