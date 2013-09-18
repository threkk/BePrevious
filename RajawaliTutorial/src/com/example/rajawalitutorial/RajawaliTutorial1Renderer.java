package com.example.rajawalitutorial;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.Texture;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;

public class RajawaliTutorial1Renderer extends RajawaliRenderer {
	
	private DirectionalLight mLight;
	private Sphere mSphere;

	public RajawaliTutorial1Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}
	
	public void initScene(){
		mLight = new DirectionalLight(1f, 0.2f, -1.0f); // set the direction
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(2); 
		
		getCurrentScene().addLight(mLight);
		try {
			Material material = new Material();
			material.addTexture(new Texture("earthColors",R.drawable.earthtruecolor_nasa_big));
			mSphere = new Sphere(1, 24, 24);
			mSphere.setMaterial(material);
			addChild(mSphere); //Queue an addition task for mSphere
		} catch (TextureException e) {
			e.printStackTrace();
		}
		
		getCurrentCamera().setZ(4.2f);
	}
	
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		mSphere.setRotY(mSphere.getRotY() + 1);
	}

}
