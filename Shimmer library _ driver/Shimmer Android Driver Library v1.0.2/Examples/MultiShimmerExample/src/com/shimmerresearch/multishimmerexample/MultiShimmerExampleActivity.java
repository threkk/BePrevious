package com.shimmerresearch.multishimmerexample;


import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import com.shimmerresearch.multishimmerexample.R;
import android.os.Handler;
import android.os.Message;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import android.os.Bundle;
import android.app.Activity;
import com.shimmerresearch.driver.*;
public class MultiShimmerExampleActivity extends Activity {
    /** Called when the activity is first created. */

    
    private Shimmer mShimmerDevice1 = null;
    private Shimmer mShimmerDevice2 = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	Timer mTimer;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mShimmerDevice1 = new Shimmer(this, mHandler,"RightArm",false); //Right Arm is a unique identifier for the shimmer unit
        mShimmerDevice2 = new Shimmer(this, mHandler, "LeftArm", 51.2, 0, 0, Shimmer.SENSOR_ACCEL, false); //Left Arm is a unique identifier for the shimmer unit
        
        String bluetoothAddress="00:06:66:43:9A:1A";
        mShimmerDevice1.connect(bluetoothAddress,"default");         

    }
 // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) { // handlers have a what identifier which is used to identify the type of msg
            case Shimmer.MESSAGE_READ:
            	if ((msg.obj instanceof ObjectCluster)){	// within each msg an object can be include, objectclusters are used to represent the data structure of the shimmer device
            	    ObjectCluster objectCluster =  (ObjectCluster) msg.obj; 
                	    Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Accelerometer X");  // first retrieve all the possible formats for the current sensor device
			 	    	if (accelXFormats != null){
			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelXFormats,"CAL")); // retrieve the calibrated data
			 	    		Log.d("CalibratedData",objectCluster.mMyName + " AccelX: " + formatCluster.mData + " "+ formatCluster.mUnits);
			 	    	}
			 	    	Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Accelerometer Y");  // first retrieve all the possible formats for the current sensor device
			 	    	if (accelYFormats != null){
			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelYFormats,"CAL")); // retrieve the calibrated data
			 	    		Log.d("CalibratedData",objectCluster.mMyName + " AccelY: " + formatCluster.mData + " "+formatCluster.mUnits);
			 	    	}
			 	    	Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Accelerometer Z");  // first retrieve all the possible formats for the current sensor device
			 	    	if (accelZFormats != null){
			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelZFormats,"CAL")); // retrieve the calibrated data
			 	    		Log.d("CalibratedData",objectCluster.mMyName + " AccelZ: " + formatCluster.mData + " "+formatCluster.mUnits);
			 	    	}
            	}
                break;
                 case Shimmer.MESSAGE_TOAST:
                	Log.d("toast",msg.getData().getString(Shimmer.TOAST));
                	Toast.makeText(getApplicationContext(), msg.getData().getString(Shimmer.TOAST),
                            Toast.LENGTH_SHORT).show();
                break;
                

                 case Shimmer.MESSAGE_STATE_CHANGE:
                	 switch (msg.arg1) {
	                     case Shimmer.MSG_STATE_FULLY_INITIALIZED:
	                    	//Next connect the next device
	                    	 Log.d("ConnectionStatus","Fully Initialized" + ((ObjectCluster)msg.obj).mBluetoothAddress);
	    		             
	                    	 if (mShimmerDevice1.getShimmerState()==Shimmer.STATE_CONNECTED && mShimmerDevice2.getShimmerState()!=Shimmer.STATE_CONNECTED){
	    	            		 mShimmerDevice2.connect("00:06:66:46:B6:52","default"); 
	    	            	 }
	    	            	 if (mShimmerDevice1.getShimmerState()==Shimmer.STATE_CONNECTED && mShimmerDevice1.getStreamingStatus()==false && mShimmerDevice2.getShimmerState()==Shimmer.STATE_CONNECTED && mShimmerDevice2.getStreamingStatus()==false){
	    		            	 Log.d("ConnectionStatus","Successful!");
	    		                 
	    		            	 mShimmerDevice1.writeEnabledSensors(Shimmer.SENSOR_ACCEL);
	    		            	 mShimmerDevice1.writeSamplingRate(51.2);
	    		                 mShimmerDevice1.startStreaming();
	    		                 
	    		                 mShimmerDevice2.startStreaming();
	    		                 shimmerTimer(30); //disconnect in 30 seconds
	    		             } 
	                		
	                         break;
                     }
                break;
            }
        }
    };

	  public synchronized void shimmerTimer(int seconds) {
	        mTimer = new Timer();
	        mTimer.schedule(new responseTask(), seconds*1000);
		}
	    
	    class responseTask extends TimerTask {
	        public void run() {
	        	mShimmerDevice1.stopStreaming();
	        	mShimmerDevice1.stop();
	        	mShimmerDevice2.stopStreaming();
	        	mShimmerDevice2.stop();
	        	
	        }
	    }
    
    
    
    }
    



    
    