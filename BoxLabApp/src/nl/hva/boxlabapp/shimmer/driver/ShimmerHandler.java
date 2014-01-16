package nl.hva.boxlabapp.shimmer.driver;

import java.util.Collection;

import nl.hva.boxlabapp.devices.Device;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ShimmerHandler extends Handler {
	private static final String TAG = ShimmerHandler.class.getName();
	private float[] data;
	private Shimmer shimmer;
	public ShimmerHandler() {
		data = new float[7];
		data[0] = 0; // q1
		data[1] = 0; // q2
		data[2] = 0; // q3
		data[3] = 0; // q4
		data[4] = 0; // a1
		data[5] = 0; // a2
		data[6] = 0; // a3
					 
	}
	public void init(Device device, Context context) {
		Shimmer shimmerDevice = new Shimmer(context, this, device.getName(),
				10, 1, 4, Shimmer.SENSOR_ACCEL|Shimmer.SENSOR_GYRO|Shimmer.SENSOR_MAG, false);
		shimmerDevice.connect(device.getMac(), "default"); 
		this.shimmer = shimmerDevice;
		
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

				String[] sensorName = new String[7];
				sensorName = new String[7]; // for x y and z axis
				sensorName[0] = "Quartenion 0";
				sensorName[1] = "Quartenion 1";
				sensorName[2] = "Quartenion 2";
				sensorName[3] = "Quartenion 3";
				sensorName[4] = "Accelerometer X";
				sensorName[5] = "Accelerometer Y";
				sensorName[6] = "Accelerometer Z";
    			
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

	public Quaternion readMagnetometer() {
		return new Quaternion(data[0], data[1], data[2], data[3]);
	}
	
	public Vector3 readAccelerometer() {
		return new Vector3(data[4]*0.1f, data[5]*0.1f, data[6]*0.1f);
	}
}
