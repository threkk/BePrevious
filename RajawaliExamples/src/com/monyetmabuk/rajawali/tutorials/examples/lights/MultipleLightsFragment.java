package com.monyetmabuk.rajawali.tutorials.examples.lights;

import java.io.ObjectInputStream;

import rajawali.Object3D;
import rajawali.SerializedObject3D;
import rajawali.animation.Animation3D;
import rajawali.animation.Animation3D.RepeatMode;
import rajawali.animation.TranslateAnimation3D;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.Texture;
import rajawali.math.vector.Vector3;
import android.content.Context;

import com.monyetmabuk.rajawali.tutorials.R;
import com.monyetmabuk.rajawali.tutorials.examples.AExampleFragment;

public class MultipleLightsFragment extends AExampleFragment {

	@Override
	protected AExampleRenderer createRenderer() {
		return new MultipleLightsRenderer(getActivity());
	}

	private final class MultipleLightsRenderer extends AExampleRenderer {

		public MultipleLightsRenderer(Context context) {
			super(context);
		}

		protected void initScene() {
			PointLight light1 = new PointLight();
			light1.setPower(5);
			PointLight light2 = new PointLight();
			light2.setPower(5);

			getCurrentScene().addLight(light1);
			getCurrentScene().addLight(light2);
			
			getCurrentCamera().setPosition(-8, 8, 8);
			getCurrentCamera().setLookAt(0, 0, 0);

			try {
				ObjectInputStream ois = new ObjectInputStream(mContext
						.getResources().openRawResource(R.raw.jet));
				SerializedObject3D serializedJet = (SerializedObject3D) ois
						.readObject();
				ois.close();

				Object3D jet = new Object3D(serializedJet);
				Material material = new Material();
				material.setDiffuseMethod(new DiffuseMethod.Lambert());
				material.enableLighting(true);
				material.addTexture(new Texture("jetTexture", R.drawable.jettexture));
				jet.setMaterial(material);
				jet.setPosition(1, 0, 0);
				jet.setRotY(180);
				addChild(jet);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Animation3D anim = new TranslateAnimation3D(
					new Vector3(-10, -10, 5), new Vector3(-10, 10, 5));
			anim.setDuration(4000);
			anim.setRepeatMode(RepeatMode.REVERSE_INFINITE);
			anim.setTransformable3D(light1);
			registerAnimation(anim);
			anim.play();

			anim = new TranslateAnimation3D(new Vector3(10, 10, 5),
					new Vector3(10, -10, 5));
			anim.setDuration(2000);
			anim.setRepeatMode(RepeatMode.REVERSE_INFINITE);
			anim.setTransformable3D(light2);
			registerAnimation(anim);
			anim.play();
		}

	}
}
