package com.monyetmabuk.rajawali.tutorials.examples.interactive;

import java.io.ObjectInputStream;

import javax.microedition.khronos.opengles.GL10;

import rajawali.Object3D;
import rajawali.SerializedObject3D;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monyetmabuk.rajawali.tutorials.R;
import com.monyetmabuk.rajawali.tutorials.examples.AExampleFragment;

public class ObjectPickingFragment extends AExampleFragment implements
		OnTouchListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		mSurfaceView.setOnTouchListener(this);

		LinearLayout ll = new LinearLayout(getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setGravity(Gravity.CENTER);

		TextView label = new TextView(getActivity());
		label.setText(R.string.object_picking_fragment);
		label.setTextSize(20);
		label.setGravity(Gravity.CENTER);
		label.setHeight(100);
		ll.addView(label);

		mLayout.addView(ll);

		return mLayout;
	}

	@Override
	protected AExampleRenderer createRenderer() {
		return new ObjectPickingRenderer(getActivity());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			((ObjectPickingRenderer) mRenderer).getObjectAt(event.getX(),
					event.getY());
		}
		return getActivity().onTouchEvent(event);
	}

	private final class ObjectPickingRenderer extends AExampleRenderer
			implements OnObjectPickedListener {
		private PointLight mLight;
		private Object3D mMonkey1, mMonkey2, mMonkey3, mMonkey4;
		private ObjectColorPicker mPicker;

		public ObjectPickingRenderer(Context context) {
			super(context);
		}

		protected void initScene() {
			mPicker = new ObjectColorPicker(this);
			mPicker.setOnObjectPickedListener(this);
			mLight = new PointLight();
			mLight.setPosition(-2, 1, 4);
			mLight.setPower(1.5f);
			getCurrentScene().addLight(mLight);
			getCurrentCamera().setPosition(0, 0, 7);

			try {
				ObjectInputStream ois = new ObjectInputStream(mContext
						.getResources().openRawResource(R.raw.monkey_ser));
				SerializedObject3D serializedMonkey = (SerializedObject3D) ois
						.readObject();
				ois.close();

				mMonkey1 = new Object3D(serializedMonkey);
				mMonkey1.setScale(.7f);
				mMonkey1.setPosition(-1, 1, 0);
				mMonkey1.setRotY(0);
				addChild(mMonkey1);

				mMonkey2 = mMonkey1.clone();
				mMonkey2.setScale(.7f);
				mMonkey2.setPosition(1, 1, 0);
				mMonkey2.setRotY(45);
				addChild(mMonkey2);

				mMonkey3 = mMonkey1.clone();
				mMonkey3.setScale(.7f);
				mMonkey3.setPosition(-1, -1, 0);
				mMonkey3.setRotY(90);
				addChild(mMonkey3);

				mMonkey4 = mMonkey1.clone();
				mMonkey4.setScale(.7f);
				mMonkey4.setPosition(1, -1, 0);
				mMonkey4.setRotY(135);
				addChild(mMonkey4);

				mPicker.registerObject(mMonkey1);
				mPicker.registerObject(mMonkey2);
				mPicker.registerObject(mMonkey3);
				mPicker.registerObject(mMonkey4);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Material material = new Material();
			material.enableLighting(true);
			material.setDiffuseMethod(new DiffuseMethod.Lambert());

			mMonkey1.setMaterial(material);
			mMonkey1.setColor(0x0000ff);

			mMonkey2.setMaterial(material);
			mMonkey2.setColor(0x00ff00);

			mMonkey3.setMaterial(material);
			mMonkey3.setColor(0xcc1100);

			mMonkey4.setMaterial(material);
			mMonkey4.setColor(0xff9955);
		}

		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			mMonkey1.setRotY(mMonkey1.getRotY() - 1f);
			mMonkey2.setRotY(mMonkey2.getRotY() + 1f);
			mMonkey3.setRotY(mMonkey3.getRotY() - 1f);
			mMonkey4.setRotY(mMonkey4.getRotY() + 1f);
		}

		public void getObjectAt(float x, float y) {
			mPicker.getObjectAt(x, y);
		}

		public void onObjectPicked(Object3D object) {
			object.setZ(object.getZ() == 0 ? -2 : 0);
		}

	}

}
