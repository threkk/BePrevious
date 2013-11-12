package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

public class Exercise3DObject implements ApplicationListener {
		
	private DemoRenderer renderer;
	private LegController controller;
	private Exercise3DHandler handler;
	private CameraInputController camController;
	
	public Exercise3DObject(Exercise3DHandler handler){
		super();
		this.handler = handler;
	}
	
	@Override
	public void create() {
		// Fuck this shit.
		Texture.setEnforcePotImages(false);
	
		this.renderer = new DemoRenderer();
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
		
		// Accelerometer only
		// TODO: Correct the gravity issue with the data!!!
		Vector3 chest, thigh, shin;
		double[][] data = handler.getData();
		
		if(!(data[0][0] == 0 && data[0][1] == 0 && data[0][2] == 0)) { // Hip reading something
			chest = new Vector3((float)data[0][0], (float)data[0][1], (float)data[0][2]);
			controller.translateChest(chest);
		} 
		if(!(data[1][0] == 0 && data[1][1] == 0 && data[1][2] == 0)) { // Thigh reading something
			thigh = new Vector3((float)data[1][0], (float)data[1][1], (float)data[1][2]);
			controller.translateThigh(thigh);
		}
		if(!(data[2][0] == 0 && data[2][1] == 0 && data[2][2] == 0)) { // Shin reading something
			shin = new Vector3((float)data[2][0], (float)data[2][1], (float)data[2][2]);
			controller.translateShin(shin);
		}
	}
	
}
