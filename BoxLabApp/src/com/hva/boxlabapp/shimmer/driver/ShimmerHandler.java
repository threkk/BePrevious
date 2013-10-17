package com.hva.boxlabapp.shimmer.driver;

import java.util.Collection;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ShimmerHandler extends Handler {
	private static final String TAG = "ShimmerHandler";

	@Override
	public void handleMessage(Message msg) {

		switch (msg.what) {
		case Shimmer.MESSAGE_STATE_CHANGE:
			switch (msg.arg1) {
			case Shimmer.MSG_STATE_FULLY_INITIALIZED:
				Log.i(TAG, "Shimmer fully initialized");
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

				int[] dataArray = new int[0];
				String[] sensorName = new String[0];
				sensorName = new String[3]; // for x y and z axis
				dataArray = new int[3];
				sensorName[0] = "Accelerometer X";
				sensorName[1] = "Accelerometer Y";
				sensorName[2] = "Accelerometer Z";
				for (int i = 0; i < 3; i++) {
					Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster
							.get(sensorName[i]);
					FormatCluster formatCluster = ((FormatCluster) ObjectCluster
							.returnFormatCluster(ofFormats, "CAL"));
					if (formatCluster != null) {
						dataArray[i] = (int) ((FormatCluster) ObjectCluster
								.returnFormatCluster(ofFormats, "RAW")).mData;
					}
				}
			}

			break;

		}

	}
}
