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
	
	private static final float POSITIVE_GRAVITY_FIX = -10f;
	private static final float NEGATIVE_GRAVITY_FIX = 9.5f;
	private static final String TAG = ShimmerHandler.class.getName();
	private float[] data;
	private Shimmer shimmer;
	private Device device;
	
	public ShimmerHandler(Device device) {
		this.device = device;
		data = new float[7];
		data[0] = 0; // q1
		data[1] = 0; // q2
		data[2] = 0; // q3
		data[3] = 0; // q4
		data[4] = 0; // a1
		data[5] = 0; // a2
		data[6] = 0; // a3
					 
	}
	public void init(Context context) {
		Shimmer shimmerDevice = new Shimmer(context, this, device.getName(),
				10, 1, 4, Shimmer.SENSOR_ACCEL|Shimmer.SENSOR_GYRO|Shimmer.SENSOR_MAG, false);
		this.shimmer = shimmerDevice;
		
	}
	
	public void connect() {
		shimmer.connect(device.getMac(), "default");
	}

	public int getState() {
		return this.shimmer.getShimmerState();
	}
	
	public void disconnect(){
		this.shimmer.stop();
	}
	
	public boolean isStreaming() {
		return shimmer.getStreamingStatus();
	}
	
	@Override
	public void handleMessage(Message msg) {

		switch (msg.what) {
		case Shimmer.MESSAGE_STATE_CHANGE:
			switch (msg.arg1) {
			case Shimmer.MSG_STATE_FULLY_INITIALIZED:
				Log.i(TAG, "Shimmer fully initialized");
				 
				 shimmer.writeEnabledSensors(Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_GYRO | Shimmer.SENSOR_MAG);
				 shimmer.writeMagRange(1);
				 shimmer.setAccelerometerSpecialMode(Shimmer.ACCEL_SMART_MODE);
				 shimmer.enable3DOrientation(true);
				 shimmer.enableCalibration(true);
				 shimmer.enableOnTheFlyGyroCal(true, 100, 1.2);
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
				sensorName[0] = "Quaternion 0";
				sensorName[1] = "Quaternion 1";
				sensorName[2] = "Quaternion 2";
				sensorName[3] = "Quaternion 3";
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
					}
				}
			
				
				int max = 4;
				int min = 4;
				
				for(int i = 4; i<=6; i++){
					if(data[i] >= data[max]) {
						max = i;
					} else if (data[i] <= data[min]) {
						min = i;
					}
				}
				
				if(data[max] >= 9 && data[min] >= -8.5f) {
					data[max] += POSITIVE_GRAVITY_FIX;					
				} else if(data[max] <= 9f && data[min] <= -8.5f) {
					data[min] += NEGATIVE_GRAVITY_FIX;
				}
				
				for(int i = 4; i <= 6; i++){
					data[i] = (float)Math.round(data[i]*10)/10;
					if(data[i] > 0 && data[i] < 0.2f) {
						data[i] = 0;
					}
				}
				
				Log.i(TAG,"X: " + data[4] + " Y: " + data[5] + " Z: " + data[6]);
			}

			break;
		case Shimmer.MESSAGE_ACK_RECEIVED:
			Log.i(TAG,"Message state = MESSAGE_ACK_RECEIVED");
			
			break;
		case Shimmer.MESSAGE_INQUIRY_RESPONSE:
			Log.i(TAG, "Messagae state = MESSAGE_INQUIRY_RESPONSE");
			break;

		}
	}

	public Quaternion readMagnetometer() {
		return new Quaternion(data[0], data[1], data[2], data[3]);
	}
	
	public Vector3 readAccelerometer() {
		// Axis are rotated. Probably same problem with magnetometer.
		// Sensors -> Anim
		// z -> x
		// x -> y
		// y -> z
		
		return new Vector3(data[6], data[4], data[5]);
	}
}
