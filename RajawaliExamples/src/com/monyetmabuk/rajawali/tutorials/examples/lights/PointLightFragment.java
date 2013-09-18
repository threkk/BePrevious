package com.monyetmabuk.rajawali.tutorials.examples.lights;

import rajawali.Object3D;
import rajawali.animation.Animation3D.RepeatMode;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.math.MathUtil;
import rajawali.math.vector.Vector3;
import rajawali.primitives.Sphere;
import android.content.Context;

import com.monyetmabuk.rajawali.tutorials.examples.AExampleFragment;

public class PointLightFragment extends AExampleFragment {

	@Override
	protected AExampleRenderer createRenderer() {
		return new PointLightRenderer(getActivity());
	}

	private final class PointLightRenderer extends AExampleRenderer {

		public PointLightRenderer(Context context) {
			super(context);
		}

		protected void initScene() {
			super.initScene();

			PointLight pointLight = new PointLight();
			pointLight.setY(2);
			pointLight.setPower(1.5f);
			
			getCurrentScene().addLight(pointLight);

			getCurrentCamera().setPosition(0, 2, 6);
			getCurrentCamera().setLookAt(0, 0, 0);
			
			Material sphereMaterial = new Material();
			sphereMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
			SpecularMethod.Phong phongMethod = new SpecularMethod.Phong();
			phongMethod.setShininess(180);
			sphereMaterial.setSpecularMethod(phongMethod);
			sphereMaterial.setAmbientIntensity(0, 0, 0);
			sphereMaterial.enableLighting(true);

			Sphere rootSphere = new Sphere(.2f, 12, 12);
			rootSphere.setMaterial(sphereMaterial);
			rootSphere.setRenderChildrenAsBatch(true);
			rootSphere.setVisible(false);
			addChild(rootSphere);

			// -- inner ring

			float radius = .8f;
			int count = 0;

			for (int i = 0; i < 360; i += 36) {
				double radians = MathUtil.degreesToRadians(i);
				int color = 0xfed14f;
				if (count % 3 == 0)
					color = 0x10a962;
				else if (count % 3 == 1)
					color = 0x4184fa;
				count++;

				Object3D sphere = rootSphere.clone(false);
				sphere.setPosition((float) Math.sin(radians) * radius, 0,
						(float) Math.cos(radians) * radius);
				sphere.setMaterial(sphereMaterial);
				sphere.setColor(color);
				rootSphere.addChild(sphere);
			}

			// -- outer ring

			radius = 2.4f;
			count = 0;

			for (int i = 0; i < 360; i += 12) {
				double radians = MathUtil.degreesToRadians(i);
				int color = 0xfed14f;
				if (count % 3 == 0)
					color = 0x10a962;
				else if (count % 3 == 1)
					color = 0x4184fa;
				count++;

				Object3D sphere = rootSphere.clone(false);
				sphere.setPosition(Math.sin(radians) * radius, 0,
						Math.cos(radians) * radius);
				sphere.setMaterial(sphereMaterial);
				sphere.setColor(color);
				rootSphere.addChild(sphere);
			}

			// -- Circular animation. Rotate the camera around the point (0, 1, 0)
			EllipticalOrbitAnimation3D anim = new EllipticalOrbitAnimation3D(
					new Vector3(0, 1, 0), new Vector3(1, 1, 1), 0, 359);
			anim.setRepeatMode(RepeatMode.INFINITE);
			anim.setDuration(6000);
			anim.setTransformable3D(pointLight);
			registerAnimation(anim);
			anim.play();
		}

	}

}
