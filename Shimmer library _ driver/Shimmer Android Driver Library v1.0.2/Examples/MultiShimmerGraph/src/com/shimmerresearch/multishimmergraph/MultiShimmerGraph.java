package com.shimmerresearch.multishimmergraph;

import android.app.Activity;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;


import java.lang.String;
import java.util.Collection;
//import android.graphics.Matrix;
import com.shimmerresearch.multishimmergraph.R;
import com.shimmerresearch.driver.*;
public class MultiShimmerGraph extends Activity {

    // Key names received from the Shimmer Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_CONNECT_SHIMMER = 2;
	private static final int REQUEST_CONFIGURE_SHIMMER = 3;
	private static final int REQUEST_CONFIGURE_VIEW_SENSOR = 4;
	private static final int REQUEST_CONFIGURE_SHIMMER2 = 5;
	private static final int REQUEST_CONFIGURE_VIEW_SENSOR2 = 6;
	
	private GraphView mGraph;
	private GraphView mGraph2; //multishimmer 
		
	//private DataMethods DM;
	private TextView mTitle;
	private TextView mValueSensor1;
	private TextView mValueSensor2;
	private TextView mValueSensor3;
	private TextView mValueSensor4;
	private TextView mValueSensor5;
	private TextView mValueSensor6;

	private TextView mTextSensor1;
	private TextView mTextSensor2;
	private TextView mTextSensor3;
	private TextView mTextSensor4;
	private TextView mTextSensor5;
	private TextView mTextSensor6;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Name of the connected device
    private String mConnectedDeviceName = null;
    // Member object for communication services
    private Shimmer mShimmerDevice1 = null;
    private Shimmer mShimmerDevice2 = null; //multishimmer
    
    private String mSensorView1 = ""; //The sensor device which should be viewed on the graph
    private String mSensorView2 = ""; //The sensor device which should be viewed on the graph
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);
        mTitle.setText(R.string.title_not_connected);
              
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
        	Toast.makeText(this, "Device does not support Bluetooth\nExiting...", Toast.LENGTH_LONG).show();
        	finish();
        }
    }
    
    @Override
    public void onStart() {
    	super.onStart();

    	if(!mBluetoothAdapter.isEnabled()) {     	
        	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    	else {
    		if(mShimmerDevice1 == null) setupMain();
    	}
    }
    
    private void setupMain() {
    	mGraph = (GraphView)findViewById(R.id.graph);
    	mGraph2 = (GraphView)findViewById(R.id.graph2);  //multishimmer 
        mValueSensor1 = (TextView) findViewById(R.id.sensorvalue1);
        mValueSensor2 = (TextView) findViewById(R.id.sensorvalue2);
        mValueSensor3 = (TextView) findViewById(R.id.sensorvalue3);
        mTextSensor1 =  (TextView) findViewById(R.id.LabelSensor1);
        mTextSensor2 =  (TextView) findViewById(R.id.LabelSensor2);
        mTextSensor3 =  (TextView) findViewById(R.id.LabelSensor3);
        
        mValueSensor4 = (TextView) findViewById(R.id.sensorvalue4);
        mValueSensor5 = (TextView) findViewById(R.id.sensorvalue5);
        mValueSensor6 = (TextView) findViewById(R.id.sensorvalue6);
        mTextSensor4 =  (TextView) findViewById(R.id.LabelSensor4);
        mTextSensor5 =  (TextView) findViewById(R.id.LabelSensor5);
        mTextSensor6 =  (TextView) findViewById(R.id.LabelSensor6);
        
    	mShimmerDevice1 = new Shimmer(this, mHandler,"Device 1",10,1, 4, Shimmer.SENSOR_ACCEL, false);
    	mShimmerDevice2 = new Shimmer(this, mHandler,"Device 2",10,1, 4, Shimmer.SENSOR_ACCEL, false);  //multishimmer
    }
    
    @Override
	protected void onPause() {
		super.onPause();
		//finish();
	}
    
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShimmerDevice1 != null) mShimmerDevice1.stop();

		if (mShimmerDevice2 != null) mShimmerDevice2.stop();
	}
	
	
	// The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override

        public void handleMessage(Message msg) {
        	
			
            switch (msg.what) {
            case Shimmer.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case Shimmer.MSG_STATE_FULLY_INITIALIZED:
                	  mTitle.setText(R.string.title_connected_to);
                	  mTitle.append(((ObjectCluster)msg.obj).mBluetoothAddress);
                	break;
                case Shimmer.STATE_CONNECTING:
                    mTitle.setText(R.string.title_connecting);
                    break;
                case Shimmer.STATE_NONE:
                    mTitle.setText(R.string.title_not_connected);
                    // this also stops streaming
                    break;
                }
                break;
            case Shimmer.MESSAGE_READ:
            	
            	    if ((msg.obj instanceof ObjectCluster)){
            	    ObjectCluster objectCluster =  (ObjectCluster) msg.obj; 
            	    
            	
            	//	setMessage("\nData packet received");
            		
            		int[] dataArray = new int[0];
            		String[] sensorName = new String[0];
            		String mCurrentSensorView="";
            		String deviceName = objectCluster.mMyName;
            		if (deviceName == "Device 1") {
            			mCurrentSensorView=mSensorView1;
            		} else if (deviceName == "Device 2") {
                		mCurrentSensorView=mSensorView2;
                		}
            		String units="";
            		if (mCurrentSensorView.equals("Accelerometer")){
            			sensorName = new String[3]; // for x y and z axis
            			dataArray = new int[3];
            			sensorName[0] = "Accelerometer X";
            			sensorName[1] = "Accelerometer Y";
            			sensorName[2] = "Accelerometer Z";
            			units="u12";
            		}
            		if (mCurrentSensorView.equals("Gyroscope")){
            			sensorName = new String[3]; // for x y and z axis
            			dataArray = new int[3];
            			sensorName[0] = "Gyroscope X";
            			sensorName[1] = "Gyroscope Y";
            			sensorName[2] = "Gyroscope Z";
            			units="u12";
            		}
            		if (mCurrentSensorView.equals("Magnetometer")){
            			sensorName = new String[3]; // for x y and z axis
            			dataArray = new int[3];
            			sensorName[0] = "Magnetometer X";
            			sensorName[1] = "Magnetometer Y";
            			sensorName[2] = "Magnetometer Z";
            			units="i16";
            		}
            		if (mCurrentSensorView.equals("GSR")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			sensorName[0] = "GSR";
            			units="u12";
            		}
            		if (mCurrentSensorView.equals("EMG")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			sensorName[0] = "EMG";
            			units="u12";
            		}
            		if (mCurrentSensorView.equals("ECG")){
            			sensorName = new String[2]; 
            			dataArray = new int[2];
            			sensorName[0] = "ECG RA-LL";
            			sensorName[1] = "ECG LA-LL";
            			units="u12";
            		}
            		if (mCurrentSensorView.equals("StrainGauge")){
            			sensorName = new String[2]; 
            			dataArray = new int[2];
            			sensorName[0] = "Strain Gauge High";
            			sensorName[1] = "Strain Gauge Low";
            			units="u12";
            		}
            		if (mCurrentSensorView.equals("HeartRate")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			sensorName[0] = "Heart Rate";
            			units="u16";
            		}
            		if (mCurrentSensorView.equals("ExpBoardA0")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			sensorName[0] = "ExpBoard A0";
            			units="u12";
            		}
            		if (mCurrentSensorView.equals("ExpBoardA7")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			sensorName[0] = "ExpBoard A7";
            			units="u12";
            		}
            		if (mCurrentSensorView.equals("TimeStamp")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			sensorName[0] = "Timestamp";
            			units="u16";
            		}
            		if (sensorName.length!=0){  // 1 is the assigned user id, see constructor of the Shimmer
				 	    if (sensorName.length>0){
				 	    	
				 	    	Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster.get(sensorName[0]);  // first retrieve all the possible formats for the current sensor device
				 	    	FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL")); 
				 	    	if (formatCluster != null) {
				 	    		//Obtain data for text view
					 	    	double calibratedData = formatCluster.mData;
					 	    	if (deviceName == "Device 1"){
					 	    		mValueSensor1.setText(String.format("%.4f",calibratedData));
					 	    		mTextSensor1.setText(sensorName[0] + " " + formatCluster.mUnits); 
					 	    	} else if (deviceName == "Device 2"){
					 	    		mValueSensor4.setText(String.format("%.4f",calibratedData));
					 	    		mTextSensor4.setText(sensorName[0]  + " " + formatCluster.mUnits); 
					 	    	}
				 	    		//Obtain data for graph
					 	    	dataArray[0] = (int)((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mData; 
							 	
					 	    	 
					 	    }
				 	    }
				 	    if (sensorName.length>1) {
				 	    	Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster.get(sensorName[1]);  // first retrieve all the possible formats for the current sensor device
				 	    	FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL"));
				 	    	if (formatCluster != null ) {
					 	    	double calibratedData = formatCluster.mData;
					 	    	//Obtain data for text view
					 	    	if (deviceName == "Device 1"){
					 	    		mValueSensor2.setText(String.format("%.4f",calibratedData));
					 	    		mTextSensor2.setText(sensorName[1] + " " + formatCluster.mUnits); 
					 	    	} else if (deviceName == "Device 2"){
					 	    		mValueSensor5.setText(String.format("%.4f",calibratedData));
					 	    		mTextSensor5.setText(sensorName[1]  + " " + formatCluster.mUnits); 
					 	    	}
					 	    	
					 	    	//Obtain data for graph
					 	    	dataArray[1] =(int) ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mData; 
					 	    	 

				 	    	}
				 	    }
				 	    if (sensorName.length>2){
				 	    
				 	    	Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster.get(sensorName[2]);  // first retrieve all the possible formats for the current sensor device
				 	    	FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL")); 
				 	    	if (formatCluster != null) {
				 	    		double calibratedData = formatCluster.mData;
				 	    		if (deviceName == "Device 1"){
					 	    		mValueSensor3.setText(String.format("%.4f",calibratedData));
					 	    		mTextSensor3.setText(sensorName[2] + " " + formatCluster.mUnits); 
					 	    	} else if (deviceName == "Device 2"){
					 	    		mValueSensor6.setText(String.format("%.4f",calibratedData));
					 	    		mTextSensor6.setText(sensorName[2]  + " " + formatCluster.mUnits);
					 	    	}
					 	    	
				 	    		//Obtain data for graph
				 	    		dataArray[2] =(int) ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mData; 
					 	    	 
					 	    	}
				 	    	
			            }

						if (deviceName=="Device 1"){
				 	    	mGraph.setDataWithAdjustment(dataArray,"Shimmer : " + deviceName,units);
				 	    } else if (deviceName=="Device 2"){
				 	    	mGraph2.setDataWithAdjustment(dataArray,"Shimmer : " + deviceName,units); //multishimmer
					   	}
					}
            	}
			
                break;
            case Shimmer.MESSAGE_ACK_RECEIVED:
            	
            	break;
            case Shimmer.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
           
            }
        }
    };
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
    	switch (requestCode) {
    	case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
            	if(mShimmerDevice1 == null) setupMain();
                //setMessage("\nBluetooth is now enabled");
                Toast.makeText(this, "Bluetooth is now enabled", Toast.LENGTH_SHORT).show();
            } else {
                // User did not enable Bluetooth or an error occured
            	Toast.makeText(this, "Bluetooth not enabled\nExiting...", Toast.LENGTH_SHORT).show();
                finish();       
            }
            break;
    	case REQUEST_CONNECT_SHIMMER:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                String address = data.getExtras()
                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                
                // This is a simplistic way of connecting the device one after the other. 
                if (mShimmerDevice1.getShimmerState()!=Shimmer.STATE_CONNECTED){ //multishimmer
                	mShimmerDevice1.connect(address,"default"); //address is just a string name, any name can be used
                	mShimmerDevice1.setgetdatainstruction("a");

                } else if (mShimmerDevice1.getShimmerState()==Shimmer.STATE_CONNECTED){ //multishimmer
                	mShimmerDevice2.connect(address,"default"); //address is just a string name, any name can be used
                    mShimmerDevice2.setgetdatainstruction("a");

                }
            }
            break;
    	case REQUEST_CONFIGURE_SHIMMER:
    		if (resultCode == Activity.RESULT_OK) {
    			mShimmerDevice1.writeEnabledSensors(data.getExtras().getInt(ConfigureActivity.mDone));
    		}
    		break;
    	case REQUEST_CONFIGURE_SHIMMER2:
    		if (resultCode == Activity.RESULT_OK) {
    			mShimmerDevice2.writeEnabledSensors(data.getExtras().getInt(ConfigureActivity.mDone));
    		}
    		break;
    	case REQUEST_CONFIGURE_VIEW_SENSOR:
    		if (resultCode == Activity.RESULT_OK) {
    			mSensorView1=data.getExtras().getString(ConfigureActivity.mDone);

    		}
    		break;
    	case REQUEST_CONFIGURE_VIEW_SENSOR2:
    		if (resultCode == Activity.RESULT_OK) {
    			mSensorView2=data.getExtras().getString(ConfigureActivity.mDone);

    		}
    		break;
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		MenuItem settings1Item = menu.findItem(R.id.settingsdev1);
		MenuItem settings2Item = menu.findItem(R.id.settingsdev2);
		MenuItem streamItem = menu.findItem(R.id.stream);
		settings1Item.setEnabled(false);
		settings2Item.setEnabled(false);
		streamItem.setEnabled(false);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	
		//disable graph edit for sensors which are not enabled
		MenuItem scanItem = menu.findItem(R.id.scan);
		MenuItem streamItem = menu.findItem(R.id.stream);
		MenuItem settings1Item = menu.findItem(R.id.settingsdev1);
		MenuItem settings2Item = menu.findItem(R.id.settingsdev2);
		if (mShimmerDevice1.getShimmerState() == Shimmer.STATE_CONNECTED) {
			settings1Item.setEnabled(true);
		}
		if (mShimmerDevice1.getShimmerState() == Shimmer.STATE_NONE && mShimmerDevice2.getShimmerState() == Shimmer.STATE_NONE) {
			settings1Item.setEnabled(false);
			settings2Item.setEnabled(false);
			streamItem.setIcon(R.drawable.ic_menu_play_clip);
			streamItem.setTitle(R.string.startstream);
			streamItem.setEnabled(false);
		}
		
		if (mShimmerDevice2.getShimmerState() == Shimmer.STATE_CONNECTED) {
			settings2Item.setEnabled(true);
		}
		if (mShimmerDevice1.getShimmerState() == Shimmer.STATE_CONNECTED && mShimmerDevice2.getShimmerState() == Shimmer.STATE_CONNECTED){
			scanItem.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			scanItem.setTitle(R.string.disconnect);
		}
		if (mShimmerDevice1.getInstructionStatus()==false){
			settings1Item.setEnabled(false);
		}
		
		if (mShimmerDevice2.getInstructionStatus()==false){
			settings2Item.setEnabled(false);
		}
		if (mShimmerDevice1.getShimmerState() == Shimmer.STATE_CONNECTING || mShimmerDevice1.getShimmerState() == Shimmer.STATE_CONNECTING) {
			scanItem.setEnabled(false);
		} else {
			scanItem.setEnabled(true);
		}
			
		// if connected and in streaming state show the stop streaming button and disable the settings button
		if((mShimmerDevice1.getStreamingStatus() == true && mShimmerDevice2.getStreamingStatus() == true) && (mShimmerDevice1.getShimmerState() == Shimmer.STATE_CONNECTED && mShimmerDevice2.getShimmerState() == Shimmer.STATE_CONNECTED)){
			streamItem.setIcon(R.drawable.ic_menu_stop);
			streamItem.setTitle(R.string.stopstream);
			settings1Item.setEnabled(false);
			settings2Item.setEnabled(false);
		}
		// if connected and not in streaming state show the start streaming button
		if((mShimmerDevice1.getStreamingStatus() == false && mShimmerDevice2.getStreamingStatus() == false) && (mShimmerDevice1.getShimmerState() == Shimmer.STATE_CONNECTED && mShimmerDevice2.getShimmerState() == Shimmer.STATE_CONNECTED)){
			streamItem.setIcon(R.drawable.ic_menu_play_clip);
			streamItem.setTitle(R.string.startstream);
			//disable if the devices are in the middle of a command
			/*if(mBtService.getInstructionStatus()==false ||mBtService2.getInstructionStatus()==false ){
				streamItem.setEnabled(false);
			} else {
				streamItem.setEnabled(true);
			}*/
			streamItem.setEnabled(true);
		}
		
		if (mShimmerDevice1.getShimmerState() == Shimmer.STATE_NONE && mShimmerDevice2.getShimmerState() == Shimmer.STATE_NONE){
			scanItem.setIcon(android.R.drawable.ic_menu_search);
			scanItem.setTitle(R.string.connect);
		}
		
		return true;
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			if (mShimmerDevice1.getShimmerState() == Shimmer.STATE_CONNECTED && mShimmerDevice2.getShimmerState() == Shimmer.STATE_CONNECTED){
				mShimmerDevice1.stop();
				while (mShimmerDevice1.getInstructionStatus()==false){};
				mShimmerDevice2.stop();
				while (mShimmerDevice2.getInstructionStatus()==false){};
		    	mShimmerDevice1 = new Shimmer(this, mHandler,"Device 1",10,1, 4, Shimmer.SENSOR_ACCEL, false);
		    	mShimmerDevice2 = new Shimmer(this, mHandler,"Device 2",10,1, 4, Shimmer.SENSOR_ACCEL, false);  //multishimmer
			} else {
				Intent serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_SHIMMER);
			}
				return true;

		case R.id.stream:
			if (mShimmerDevice1.getStreamingStatus()==true && mShimmerDevice2.getStreamingStatus()==true){
				mShimmerDevice1.stopStreaming();
				while (mShimmerDevice1.getInstructionStatus()==false){};
				mShimmerDevice2.stopStreaming(); //multishimmer
				while (mShimmerDevice2.getInstructionStatus()==false){};
			} else {
				mShimmerDevice1.startStreaming();
				mShimmerDevice2.startStreaming(); //multishimmer
			}
     		return true;
     	case R.id.settingsdev1:
			startActivityForResult(new Intent(this, ConfigureActivity.class), REQUEST_CONFIGURE_SHIMMER);
			return true;
     	case R.id.settingsdev2:
			startActivityForResult(new Intent(this, ConfigureActivity.class), REQUEST_CONFIGURE_SHIMMER2);
			return true;
     	case R.id.viewsensordev1:
     		Intent viewIntent=new Intent(this, SensorViewActivity.class);
     		viewIntent.putExtra("Enabled_Sensors", mShimmerDevice1.getEnabledSensors());
     		startActivityForResult(viewIntent, REQUEST_CONFIGURE_VIEW_SENSOR);
			return true;
     	case R.id.viewsensordev2:
     		Intent viewIntent2=new Intent(this, SensorViewActivity.class);
     		viewIntent2.putExtra("Enabled_Sensors", mShimmerDevice2.getEnabledSensors());
     		startActivityForResult(viewIntent2, REQUEST_CONFIGURE_VIEW_SENSOR2);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}