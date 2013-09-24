package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class ModelScreen implements Screen{

	private BobView bobView;
	private BobRenderer renderer;
	
	public ModelScreen() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderer.render();
	}

	@Override
	public void resize(int width, int height) {
		renderer.setSize(width, height);
		
	}

	@Override
	public void show() {
		bobView = new BobView();
		renderer = new BobRenderer(bobView);
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
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

}
