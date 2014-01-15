package nl.hva.boxlabapp.shimmer.driver;

import java.util.Collection;

import nl.hva.boxlabapp.devices.Device;

import com.badlogic.gdx.math.Quaternion;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ShimmerHandler extends Handler {
	private static final String TAG = ShimmerHandler.class.getName();
	private float[] data;
	private Shimmer shimmer;
	public ShimmerHandler() {
		data = new float[3];
		data[0] = 0; // q1
		data[1] = 0; // q2
		data[2] = 0; // q3
		data[3] = 0; // q4
					 
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
				sensorName = new String[4]; // for x y and z axis
				sensorName[0] = "Quarternion 1";
				sensorName[1] = "Quarternion 2";
				sensorName[2] = "Quarternion 3";
				sensorName[3] = "Quarternion 4";
    			
				for (int i = 0; i < sensorName.length; i++) {
					Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster
							.get(sensorName[i]);
					FormatCluster formatCluster = ((FormatCluster) ObjectCluster
							.returnFormatCluster(ofFormats, "CAL"));
					if (formatCluster != null) {
						data[i] = (float) formatCluster.mData;
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

	public Quaternion readSensors() {
		return new Quaternion(data[0], data[1], data[2], data[3]);
	}
}
