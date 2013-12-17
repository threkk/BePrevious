package com.hva.boxlabapp.shimmer.driver;

import java.util.Collection;

import com.hva.boxlabapp.devices.Device;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ShimmerHandler extends Handler {
	private static final String TAG = ShimmerHandler.class.getName();
	private double[] data;
	private Shimmer shimmer;
	public ShimmerHandler() {
		data = new double[6];
		data[0] = 0; // accel x
		data[1] = 0; // accel y
		data[2] = 0; // accel z
		data[3] = 0; // gyros x
		data[4] = 0; // gyros y
		data[5] = 0; // gyros z
					 // I guess...
	}

	public void init(Device device, Context context) {
		Shimmer shimmerDevice = new Shimmer(context, this, device.getName(),
				10, 1, 4, Shimmer.SENSOR_ACCEL, false);
		shimmerDevice.connect(device.getMac(), "default"); // address is just a
															// string name, any
		this.shimmer = shimmerDevice;													// name can be used
		
	}

	@Override
	public void handleMessage(Message msg) {

		switch (msg.what) {
		case Shimmer.MESSAGE_STATE_CHANGE:
			switch (msg.arg1) {
			case Shimmer.MSG_STATE_FULLY_INITIALIZED:
				Log.i(TAG, "Shimmer fully initialized");
				 shimmer.setgetdatainstruction("a"); // ???
				 shimmer.writeEnabledSensors(Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_GYRO);
				 shimmer.startStreaming();
				break;
			case Shimmer.STATE_CONNECTING:
				Log.i(TAG, "Shimmer connecting");
				break;
			case Shimmer.STATE_NONE:
				Log.i(TAG, "Shimmer not connected");
				break;

			}
			break;
		case Shimmer.MESSAGE_READ:

			if ((msg.obj instanceof ObjectCluster)) {
				ObjectCluster objectCluster = (ObjectCluster) msg.obj;

				String[] sensorName = new String[0];
				sensorName = new String[6]; // for x y and z axis
				sensorName[0] = "Accelerometer X";
				sensorName[1] = "Accelerometer Y";
				sensorName[2] = "Accelerometer Z";
				sensorName[3] = "Gyroscope X";
    			sensorName[4] = "Gyroscope Y";
    			sensorName[5] = "Gyroscope Z";
				for (int i = 0; i < sensorName.length; i++) {
					Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster
							.get(sensorName[i]);
					FormatCluster formatCluster = ((FormatCluster) ObjectCluster
							.returnFormatCluster(ofFormats, "CAL"));
					if (formatCluster != null) {
						data[i] = formatCluster.mData;
						Log.i(TAG, "Data calibrated for sensorname " + sensorName[i]+"  = " + data[i]);

					}
				}

			}

			break;
		case Shimmer.MESSAGE_ACK_RECEIVED:
			Log.i(TAG,"Message state = MESSAGE_ACK_RECEIVED");
			
			break;
		case Shimmer.MESSAGE_INQUIRY_RESPONSE:
			Log.i(TAG, "Messagae state = MESSAGE_INQUIRY_RESPONSE");
		}
		

	}

	public double[] readSensors() {
		return data;
	}
}
