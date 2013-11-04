package com.hva.boxlabapp.exercises;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.hva.boxlabapp.R;
import com.hva.boxlabapp.gdx.Exercise3DObject;
import com.hva.boxlabapp.gdx.Exercise3DHandler;
import com.hva.boxlabapp.model.SensorDevice;
import com.hva.boxlabapp.shimmer.driver.ShimmerHandler;

public class Exercise3DActivity extends AndroidApplication implements
		Exercise3DHandler {

	private ShimmerHandler hip;
	private ShimmerHandler thigh;
	private ShimmerHandler shin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Start the handler. We need to do something with this when the pause
		// and
		// destroy events happen.
		hip = new ShimmerHandler();
		SensorDevice device = new SensorDevice(1, "Test",
				SensorDevice.Type.SHIMMER_2R, "hip");
		BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
		if (defaultAdapter != null) {
			Iterator<BluetoothDevice> iterator = defaultAdapter
					.getBondedDevices().iterator();
			while (iterator.hasNext()) {
				BluetoothDevice next = iterator.next();
				device.setMac(next.getAddress());
			}
		}

		// Initialize
		hip.init(device, this);
		// thigh.init(device, this);
		// shin.init(device, this);

		// Also we need to do something to distinguish between them.

		LinearLayout layout = new LinearLayout(this);

		// Dimensions
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x / 2;
		int height = size.y;

		View contentView = LayoutInflater.from(this).inflate(
				R.layout.exercise_3d_content, null);
		layout.addView(contentView, width, height);

		final Exercise3DObject ex = new Exercise3DObject(this);
		View gameView = initializeForView(ex, false);
		layout.addView(gameView, width, height);

		setContentView(layout);

	}

	@Override
	public int[][] getAccel() {
		// 3 sensors sending 3 outputs
		int[][] data = new int[3][3];
		// data[0] = hip.readSensors();
		// data[1] = thigh.readSensors();
		// data[2] = shin.readSensors();
		return data;
	}

	@Override
	public float[][] getGyro() {
		float[][] gyro = {};
		return gyro;
	}
}