package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.Model;

public class Exercise3DObject implements ApplicationListener, InputProcessor {
	
// 2D Bob
	
	private BobView bobView;
	private BobRenderer renderer;
	private BobController controller;
	private Exercise3DHandler pos;
	private boolean way;
//    private PerspectiveCamera cam;
//    private Model model;
	
	public Exercise3DObject(Exercise3DHandler pos){
		super();
		this.pos = pos;
	}
	
	@Override
	public void create() {
		// Fuck this shit.
		Texture.setEnforcePotImages(false);
		bobView = new BobView();
		renderer = new BobRenderer(bobView);
		controller = new BobController(bobView);
		Gdx.input.setInputProcessor(this);
		way = true;
		
//		3D
//		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//      cam.position.set(10f, 10f, 10f);
//      cam.lookAt(0,0,0);
//      cam.near = 0.1f;
//      cam.far = 300f;
//      cam.update();
        
        
       
	}

	@Override
	public void resize(int width, int height) {
		renderer.setSize(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.moveBob(pos.getPosition(way));
		way = !way;
		renderer.render();	
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
		// TODO Auto-generated method stub
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		if(screenX < renderer.getWidth()/2){
			controller.moveBob(-1);
		} else {
			controller.moveBob(1);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void moveBob(int x) {
		if(x < 0){
			controller.moveBob(-1);
		} else {
			controller.moveBob(1);
		}
	}
}
