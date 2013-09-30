package com.hva.boxlabapp.exercises;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.hva.boxlabapp.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import rajawali.Object3D;
import rajawali.RajawaliFragment;
import rajawali.animation.Animation3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.parser.ALoader.ParsingException;
import rajawali.parser.LoaderOBJ;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;

public class Exercise3DFragment extends RajawaliFragment {

	private Exercise3DRender render;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		render = new Exercise3DRender(getActivity());
		render.setSurfaceView(mSurfaceView);
		setRenderer(render);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLayout = (FrameLayout) inflater.inflate(R.layout.exercise_3d_layout,
				container, false);

		mLayout.addView(mSurfaceView);

		return mLayout;
	}
	
	private class Exercise3DRender extends RajawaliRenderer {

		private DirectionalLight mLight;
		private Sphere mSphere;
		private Object3D mObj;
		
		public Exercise3DRender(Context context) {
			super(context);
			setFrameRate(60);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			super.onSurfaceCreated(gl, config);
		}
		
		public void initScene(){
			mLight = new DirectionalLight(1f, 0.2f, -1.0f); // set the direction
			mLight.setColor(1.0f, 1.0f, 1.0f);
			mLight.setPower(2); 
			
			getCurrentScene().addLight(mLight);
//			try {
//				Material material = new Material();
//				material.addTexture(new Texture("earthColors",R.drawable.earthtruecolor_nasa_big));
//				mSphere = new Sphere(1, 24, 24);
//				mSphere.setMaterial(material);
//				addChild(mSphere); //Queue an addition task for mSphere
//			} catch (TextureException e) {
//				//e.printStackTrace();
//			}
			
			LoaderOBJ objParser = new LoaderOBJ(render, R.raw.skeleton_obj);
			
			
				try {
					objParser.parse();
				} catch (ParsingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mObj = objParser.getParsedObject();
				mObj.setColor(Color.BLUE);
				addChild(mObj);
				
			
			
			getCurrentCamera().setZ(4.2f);
		}
		
		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
//			mSphere.setRotY(mSphere.getRotY() + 1);
		}

	}
}
