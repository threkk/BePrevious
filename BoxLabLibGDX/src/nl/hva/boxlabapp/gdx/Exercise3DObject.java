package nl.hva.boxlabapp.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Exercise3DObject implements ApplicationListener {
		
	public interface SensorsHandler {
		Quaternion[] getRotation();
		Vector3[] getTranslation();
	}
	
	private LegModel legModel;
	private LegRenderer renderer;
	private LegController controller;
	private SensorsHandler handler;

    private CameraInputController camController;
	
	public Exercise3DObject(SensorsHandler handler){
		super();
		this.handler = handler;
	}
	
	@Override
	public void create() {
		// Fuck this shit.
		Texture.setEnforcePotImages(false);
		
		this.legModel = new LegModel();
		this.renderer = new LegRenderer(legModel);
		this.controller = new LegController(renderer.getInstance());
		this.camController = new CameraInputController(renderer.getCamera());
        Gdx.input.setInputProcessor(camController);
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	    updateController();
		renderer.render();
	    camController.update();
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
	public void dispose() {
		renderer.dispose();
	}
	
	public void updateController(){
		
		Quaternion[] rotations = handler.getRotation();
		Vector3[] translations = handler.getTranslation();
		
		controller.rotateThigh(rotations[1]);
		controller.rotateShin(rotations[2]);

	}
	
}
