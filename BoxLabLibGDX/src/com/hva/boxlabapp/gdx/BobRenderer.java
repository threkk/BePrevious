package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BobRenderer {

	private static final float CAMERA_WIDTH = 10f;
	private static final float CAMERA_HEIGHT = 7f;
	
	private int width;
	private int height;
	private float ppuX;
	private float ppuY;
	
	private BobView bobView;
	private Texture bobTexture;
	private OrthographicCamera cam;
	private SpriteBatch spriteBatch;
	
	public BobRenderer(BobView bob) {
		this.bobView = bob;
		this.cam =  new OrthographicCamera(CAMERA_WIDTH,CAMERA_HEIGHT);
		this.cam.position.set(CAMERA_WIDTH/2f,CAMERA_HEIGHT/2f,0);
		this.cam.update();
		spriteBatch = new SpriteBatch();
		loadTextures();
	}
	
	public void setSize(int w, int h){
		this.width = w;
		this.height = h;
		this.ppuX = (float)this.width/CAMERA_WIDTH;
		this.ppuY = (float)this.height/CAMERA_HEIGHT;
	}
	
	private void loadTextures(){
		bobTexture = new Texture(Gdx.files.internal("bob.png"));
	}
	
	public void render(){
		spriteBatch.begin();
			drawBob();
		spriteBatch.end();
	}

	private void drawBob() {
		Bob bob = bobView.getBob();
		spriteBatch.draw(bobTexture, bob.getPosition().x*ppuX, bob.getPosition().y*ppuY,bob.SIZE*ppuX,bob.SIZE*ppuY );
	}

}
