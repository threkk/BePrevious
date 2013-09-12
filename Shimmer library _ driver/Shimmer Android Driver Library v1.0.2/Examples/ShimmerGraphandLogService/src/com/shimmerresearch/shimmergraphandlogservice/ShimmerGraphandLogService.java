package com.shimmerresearch.shimmergraphandlogservice;

import java.util.Collection;

import com.shimmerresearch.shimmergraphandlogservice.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.Shimmer;
import com.shimmerresearch.service.ShimmerService;
import com.shimmerresearch.service.ShimmerService.LocalBinder;
import com.shimmerresearch.tools.Logging;
//import android.graphics.Matrix;

public class ShimmerGraphandLogService extends ServiceActivity {

	private static Context context;
	static final int REQUEST_ENABLE_BT = 1;
	static final int REQUEST_CONNECT_SHIMMER = 2;
	static final int REQUEST_CONFIGURE_SHIMMER = 3;
	static final int REQUEST_CONFIGURE_VIEW_SENSOR = 4;
	static final int REQUEST_COMMAND_SHIMMER = 5;
	static final int REQUEST_LOGFILE_SHIMMER = 6;
	private static GraphView mGraph;
		
	//private DataMethods DM;
	private static TextView mTitle;
	private static TextView mTitleLogging;
	private static TextView mValueSensor1;
	private static TextView mValueSensor2;
	private static TextView mValueSensor3;
	private static TextView mTVPRR;
	
	private static TextView mTextSensor1;
	private static TextView mTextSensor2;
	private static TextView mTextSensor3;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Name of the connected device
    private static String mConnectedDeviceName = null;
    // Member object for communication services
    
    
    private static String mSensorView = ""; //The sensor device which should be viewed on the graph
    private static int mGraphSubSamplingCount = 0; //10 
    private static String mFileName = "myFirstDataSet";
    static Logging log = new Logging(mFileName,"\t"); //insert file name
    private static boolean mEnableLogging = false; 
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Shimmer","On Create");
        super.onCreate(savedInstanceState);
//      requestWindowFeature(Window.FEATURE_LEFT_ICON);
//      setContentView(R.layout.main);
  //    
      requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
      //requestWindowFeature(Window.FEATURE_LEFT_ICON);
      setContentView(R.layout.main);
      getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
     // getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.icon_very_small);
      //setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.icon);
      getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
      Log.d("Shimmer","On Create");
      mTitle = (TextView) findViewById(R.id.title_right_text);
      // Set up the custom title
      mTitleLogging = (TextView) findViewById(R.id.title_left_text);
      
      
      
      
    	mGraph = (GraphView)findViewById(R.id.graph);
        mValueSensor1 = (TextView) findViewById(R.id.sensorvalue1);
        mValueSensor2 = (TextView) findViewById(R.id.sensorvalue2);
        mValueSensor3 = (TextView) findViewById(R.id.sensorvalue3);
        mTextSensor1 =  (TextView) findViewById(R.id.LabelSensor1);
        mTextSensor2 =  (TextView) findViewById(R.id.LabelSensor2);
        mTextSensor3 =  (TextView) findViewById(R.id.LabelSensor3);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		 switch(metrics.densityDpi){
	     case DisplayMetrics.DENSITY_LOW:
	    	 mTitleLogging.setTextSize(10);
	    	 mTitle.setTextSize(10);
	    	 mValueSensor1.setTextSize(10);
	    	 mValueSensor2.setTextSize(10);
	    	 mValueSensor3.setTextSize(10);
	    	 mTextSensor1.setTextSize(10);
	    	 mTextSensor2.setTextSize(10);
	    	 mTextSensor3.setTextSize(10);
	                break;
	     case DisplayMetrics.DENSITY_MEDIUM:
	    	 mTitleLogging.setTextSize(10);
	    	 mTitle.setTextSize(10);
	    	 mValueSensor1.setTextSize(14);
	    	 mValueSensor2.setTextSize(14);
	    	 mValueSensor3.setTextSize(14);
	    	 mTextSensor1.setTextSize(14);
	    	 mTextSensor2.setTextSize(14);
	    	 mTextSensor3.setTextSize(14);
	                 break;
	     case DisplayMetrics.DENSITY_HIGH:
	    	 mTitleLogging.setTextSize(20);
	    	 mTitle.setTextSize(20);
	    	 mValueSensor1.setTextSize(20);
	    	 mValueSensor2.setTextSize(20);
	    	 mValueSensor3.setTextSize(20);
	    	 mTextSensor1.setTextSize(20);
	    	 mTextSensor2.setTextSize(20);
	    	 mTextSensor3.setTextSize(20);
	                 break;
	     case DisplayMetrics.DENSITY_XHIGH:
	    	 mTitle.setTextSize(22);
	    	 mTitleLogging.setTextSize(22);
	    	 mValueSensor1.setTextSize(22);
	    	 mValueSensor2.setTextSize(22);
	    	 mValueSensor3.setTextSize(22);
	    	 mTextSensor1.setTextSize(22);
	    	 mTextSensor2.setTextSize(22);
	    	 mTextSensor3.setTextSize(22);
	    	 break;
		 }
		 
		 if (!isMyServiceRunning())
	      {
	      	Log.d("ShimmerH","Oncreate2");
	      	Intent intent=new Intent(this, ShimmerService.class);
	      	startService(intent);
	      	if (mServiceFirstTime==true){
	      		Log.d("ShimmerH","Oncreate3");
	  			getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
	  			mServiceFirstTime=false;
	  		}
	      	mTitle.setText(R.string.title_not_connected); // if no service is running means no devices are connected
	      }         
	      
	      if (mConnectedDeviceName!=null){
	      	mTitle.setText(R.string.title_connected_to);
	          mTitle.append(mConnectedDeviceName);    
	      }
		 
		 
		 if (mEnableLogging==false){
		      	mTitleLogging.setText("Logging Disabled");
		      } else if (mEnableLogging==true){
		      	mTitleLogging.setText("Logging Enabled");
	      }
		      
		 
		mTVPRR =  (TextView) findViewById(R.id.textViewPRR);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
        	Toast.makeText(this, "Device does not support Bluetooth\nExiting...", Toast.LENGTH_LONG).show();
        	finish();
        }
        
        ShimmerGraphandLogService.context = getApplicationContext();
 
    }
    
    @Override
    public void onStart() {
    	super.onStart();

    	if(!mBluetoothAdapter.isEnabled()) {     	
        	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    	else {
    		
    		
    	}
    }
    
    
    
    @Override
	public void onPause() {
		super.onPause();
		Log.d("Shimmer","On Pause");
		//finish();
		if(mServiceBind == true){
  		  //getApplicationContext().unbindService(mTestServiceConnection); 
  	  }
	}

	public void onResume() {
		super.onResume();
		ShimmerGraphandLogService.context = getApplicationContext();
		Log.d("Shimmer","On Resume");
		Intent intent=new Intent(this, ShimmerService.class);
  	  	Log.d("ShimmerH","on Resume");
  	  	getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		Log.d("Shimmer","On Destroy");
	}
	
	
	 protected ServiceConnection mTestServiceConnection = new ServiceConnection() {

	      	public void onServiceConnected(ComponentName arg0, IBinder service) {
	      		// TODO Auto-generated method stub
	      		Log.d("ShimmerService", "service connected from main activity");
	      		LocalBinder binder = (LocalBinder) service;
	      		mService = binder.getService();
	      		mServiceBind = true;
	      		mService.setGraphHandler(mHandler);
	      		
	      	}
	      	public void onServiceDisconnected(ComponentName arg0) {
	      		// TODO Auto-generated method stub
	      		mServiceBind = false;
	      	}
	    };
	
	
	// The Handler that gets information back from the BluetoothChatService
    private static Handler mHandler = new Handler() {
   

		public void handleMessage(Message msg) {
			Log.d("Shimmer","msg");
            switch (msg.what) {
            
            case Shimmer.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case Shimmer.STATE_CONNECTED:
                	//this has been deprecated
                	/*Log.d("Shimmer","ms1");
                    mTitle.setText(R.string.title_connected_to);
                    mConnectedDeviceName=((ObjectCluster)msg.obj).mBluetoothAddress;
                    mTitle.append(mConnectedDeviceName);    
                    mService.enableGraphingHandler(true);*/
                    break;
                case Shimmer.MSG_STATE_FULLY_INITIALIZED:
                	Log.d("Shimmer","ms1");
                    mTitle.setText(R.string.title_connected_to);
                    mConnectedDeviceName=((ObjectCluster)msg.obj).mBluetoothAddress;
                    mTitle.append(mConnectedDeviceName);    
                    mService.enableGraphingHandler(true);
                    break;
                case Shimmer.STATE_CONNECTING:
                	Log.d("Shimmer","msg2");
                    mTitle.setText(R.string.title_connecting);
                    break;
                case Shimmer.STATE_NONE:
                	Log.d("Shimmer","msg3");
                    mTitle.setText(R.string.title_not_connected);;
                    mConnectedDeviceName=null;
                    // this also stops streaming
                    break;
                }
                break;
            case Shimmer.MESSAGE_READ:

            	    if ((msg.obj instanceof ObjectCluster)){
            	    ObjectCluster objectCluster =  (ObjectCluster) msg.obj; 
            	    Log.d("Shimmer","MSGREAD");
            	   
            	
            		int[] dataArray = new int[0];
            		double[] calibratedDataArray = new double[0];
            		String[] sensorName = new String[0];
            		String units="";
            		String calibratedUnits="";
            		//mSensorView determines which sensor to graph
            		if (mSensorView.equals("Accelerometer")){
            			sensorName = new String[3]; // for x y and z axis
            			dataArray = new int[3];
            			calibratedDataArray = new double[3];
            			sensorName[0] = "Accelerometer X";
            			sensorName[1] = "Accelerometer Y";
            			sensorName[2] = "Accelerometer Z";
            			units="u12";
            		}
            		if (mSensorView.equals("Gyroscope")){
            			sensorName = new String[3]; // for x y and z axis
            			dataArray = new int[3];
            			calibratedDataArray = new double[3];
            			sensorName[0] = "Gyroscope X";
            			sensorName[1] = "Gyroscope Y";
            			sensorName[2] = "Gyroscope Z";
            			units="u12";
            		}
            		if (mSensorView.equals("Magnetometer")){
            			sensorName = new String[3]; // for x y and z axis
            			dataArray = new int[3];
            			calibratedDataArray = new double[3];
            			sensorName[0] = "Magnetometer X";
            			sensorName[1] = "Magnetometer Y";
            			sensorName[2] = "Magnetometer Z";
            			units="i16";
            		}
            		if (mSensorView.equals("GSR")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			calibratedDataArray = new double[1];
            			sensorName[0] = "GSR";
            			units="u16";
            		}
            		if (mSensorView.equals("EMG")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			calibratedDataArray = new double[1];
            			sensorName[0] = "EMG";
            			units="u12";
            		}
            		if (mSensorView.equals("ECG")){
            			sensorName = new String[2]; 
            			dataArray = new int[2];
            			calibratedDataArray = new double[2];
            			sensorName[0] = "ECG RA-LL";
            			sensorName[1] = "ECG LA-LL";
            			units="u12";
            		}
            		if (mSensorView.equals("StrainGauge")){
            			sensorName = new String[2]; 
            			dataArray = new int[2];
            			calibratedDataArray = new double[2];
            			sensorName[0] = "Strain Gauge High";
            			sensorName[1] = "Strain Gauge Low";
            			units="u12";
            		}
            		if (mSensorView.equals("HeartRate")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			calibratedDataArray = new double[1];
            			sensorName[0] = "Heart Rate";
            			units="u8";
            			if (mService.getFWVersion(mConnectedDeviceName)>0.1){
            				units="u16";
            			}
            		}
            		if (mSensorView.equals("ExpBoardA0")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			calibratedDataArray = new double[1];
            			sensorName[0] = "ExpBoard A0";
            			units="u12";
            		}
            		if (mSensorView.equals("ExpBoardA7")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			calibratedDataArray = new double[1];
            			sensorName[0] = "ExpBoard A7";
            			units="u12";
            		}
            		if (mSensorView.equals("Battery")){
            			sensorName = new String[2]; 
            			dataArray = new int[2];
            			calibratedDataArray = new double[2];
            			sensorName[0] = "VSenseReg";
            			sensorName[1] = "VSenseBatt";
            			units="u12";
            		}
            		if (mSensorView.equals("TimeStamp")){
            			sensorName = new String[1]; 
            			dataArray = new int[1];
            			calibratedDataArray = new double[1];
            			sensorName[0] = "Timestamp";
            			units="u16";
            		}
            		
            		String deviceName = objectCluster.mMyName;
            		//log data
            	    
            		
            		if (sensorName.length!=0){  // Device 1 is the assigned user id, see constructor of the Shimmer
				 	    if (sensorName.length>0){
				 	    	
				 	    	Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster.get(sensorName[0]);  // first retrieve all the possible formats for the current sensor device
				 	    	FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL")); 
				 	    	if (formatCluster != null) {
				 	    		//Obtain data for text view
				 	    		calibratedDataArray[0] = formatCluster.mData;
				 	    		calibratedUnits = formatCluster.mUnits;
				 	    		Log.d("Shimmer","MSGREAD2");
				 	    		//Obtain data for graph
					 	    	
						 	 	dataArray[0] = (int)((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mData; 
						 	 	
					 	    	
					 	    }
				 	    }
				 	    if (sensorName.length>1) {
				 	    	Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster.get(sensorName[1]);  // first retrieve all the possible formats for the current sensor device
				 	    	FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL"));
				 	    	if (formatCluster != null ) {
					 	    	calibratedDataArray[1] = formatCluster.mData;
					 	    	//Obtain data for text view
					 	    	
					 	    	//Obtain data for graph
					 	    	dataArray[1] =(int) ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mData; 
					 	    	

				 	    	}
				 	    }
				 	    if (sensorName.length>2){
				 	    
				 	    	Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster.get(sensorName[2]);  // first retrieve all the possible formats for the current sensor device
				 	    	FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL")); 
				 	    	if (formatCluster != null) {
				 	    		calibratedDataArray[2] = formatCluster.mData;
					 	    	
					 	   	    
				 	    		//Obtain data for graph
				 	    		dataArray[2] =(int) ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mData; 
					 	    	
				 	    	}
				 	    	
			            }
				 	   
				 	    
				 	    //in order to prevent LAG the number of data points plotted is REDUCED
				 	    int maxNumberofSamplesPerSecond=50; //Change this to increase/decrease the number of samples which are graphed
				 	    int subSamplingCount=0;
				 	    if (mService.getSamplingRate(mConnectedDeviceName)>maxNumberofSamplesPerSecond){
				 	    	subSamplingCount=(int) (mService.getSamplingRate(mConnectedDeviceName)/maxNumberofSamplesPerSecond);
				 	    	mGraphSubSamplingCount++;
				 	    	Log.d("SSC",Integer.toString(subSamplingCount));
				 	    }
				 	    if (mGraphSubSamplingCount==subSamplingCount){
				 	    	mGraph.setDataWithAdjustment(dataArray,"Shimmer : " + deviceName,units);
				 	    	mTVPRR.setText("PRR : "+String.format("%.2f", mService.getPacketReceptionRate(mConnectedDeviceName))+ "%");
						if (calibratedDataArray.length>0) {
							mValueSensor1.setText(String.format("%.4f",calibratedDataArray[0]));
							mTextSensor1.setText(sensorName[0] + "("+calibratedUnits+")");
						}
						if (calibratedDataArray.length>1) {
							mValueSensor2.setText(String.format("%.4f",calibratedDataArray[1]));
							mTextSensor2.setText(sensorName[1] + "("+calibratedUnits+")");
						}
						if (calibratedDataArray.length>2) {
							mValueSensor3.setText(String.format("%.4f",calibratedDataArray[2]));
							mTextSensor3.setText(sensorName[2] + "("+calibratedUnits+")");
						}
							        			
						mGraphSubSamplingCount=0;
				 	    }
					}
            	}
				
                break;
            case Shimmer.MESSAGE_ACK_RECEIVED:
            	
            	break;
            case Shimmer.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                
                Toast.makeText(getContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
       
            	
            case Shimmer.MESSAGE_TOAST:
                Toast.makeText(getContext(), msg.getData().getString(Shimmer.TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
           
            }
        }
    };
	
    private static Context getContext(){
    	return ShimmerGraphandLogService.context;
    }
    
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
    	switch (requestCode) {
    	case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
            	
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
                Log.d("Shimmer",address);
          		mService.connectShimmer(address, "Device");
          		mService.setGraphHandler(mHandler);
                
                //mShimmerDevice.connect(address,"gerdavax");
                //mShimmerDevice.setgetdatainstruction("a");
            }
            break;
    	case REQUEST_CONFIGURE_SHIMMER:
    		if (resultCode == Activity.RESULT_OK) {
    			mService.setEnabledSensors(data.getExtras().getInt(ConfigureActivity.mDone),mConnectedDeviceName);
    		}
    		break;
    	case REQUEST_CONFIGURE_VIEW_SENSOR:
    		if (resultCode == Activity.RESULT_OK) {
    			Log.d("Shimmer","Request Configure Graph");
    			mSensorView=data.getExtras().getString(ConfigureActivity.mDone);
    			mTextSensor1.setText("");
    			mTextSensor2.setText("");
    			mTextSensor3.setText("");
    			if (mSensorView.equals("Accelerometer")){
        			mTextSensor1.setText("AccelerometerX");
        			mTextSensor2.setText("AccelerometerY");
        			mTextSensor3.setText("AccelerometerZ");
        		}
        		if (mSensorView.equals("Gyroscope")){
        			mTextSensor1.setText("GyroscopeX");
        			mTextSensor2.setText("GyroscopeY");
        			mTextSensor3.setText("GyroscopeZ");
        		}
        		if (mSensorView.equals("Magnetometer")){
        			mTextSensor1.setText("MagnetometerX");
        			mTextSensor2.setText("MagnetometerY");
        			mTextSensor3.setText("MagnetometerZ");
        		}
        		if (mSensorView.equals("GSR")){
        			mTextSensor1.setText("GSR");
        		}
        		if (mSensorView.equals("EMG")){
        			mTextSensor1.setText("EMG");
        		}
        		if (mSensorView.equals("ECG")){
        			mTextSensor1.setText("ECGRALL");
        			mTextSensor2.setText("ECGLALL");
        		}
        		if (mSensorView.equals("StrainGauge")){
        			mTextSensor1.setText("Strain Gauge High");
        			mTextSensor2.setText("Strain Gauge Low");
        		}
        		if (mSensorView.equals("HeartRate")){
        			mTextSensor1.setText("Heart Rate");
        		}
        		if (mSensorView.equals("ExpBoardA0")){
        			mTextSensor1.setText("ExpBoardA0");
        		}
        		if (mSensorView.equals("ExpBoardA7")){
        			mTextSensor1.setText("ExpBoardA7");
        		}
        		if (mSensorView.equals("TimeStamp")){
        			mTextSensor1.setText("TimeStamp");
        		} 
        		if (mSensorView.equals("Battery")){
        			mTextSensor1.setText("VSenseReg");
        			mTextSensor2.setText("VSenseBatt");
        		}
    		}
    		break;
    	case REQUEST_COMMAND_SHIMMER:
    		
    		if (resultCode == Activity.RESULT_OK) {
	    		if(data.getExtras().getBoolean("ToggleLED",false) == true)
	    		{
	    			mService.toggleAllLEDS();
	    		}
	    		
	    		if(data.getExtras().getDouble("SamplingRate",-1) != -1)
	    		{
	    			mService.writeSamplingRate(mConnectedDeviceName, data.getExtras().getDouble("SamplingRate",-1));
	    			Log.d("Shimmer",Double.toString(data.getExtras().getDouble("SamplingRate",-1)));
	    			mGraphSubSamplingCount=0;
	    		}
	    		
	    		if(data.getExtras().getInt("AccelRange",-1) != -1)
	    		{
	    			mService.writeAccelRange(mConnectedDeviceName, data.getExtras().getInt("AccelRange",-1));
	    		}
	    		
	    		if(data.getExtras().getInt("GSRRange",-1) != -1)
	    		{
	    			mService.writeGSRRange(mConnectedDeviceName,data.getExtras().getInt("GSRRange",-1));
	    		}
	    		if(data.getExtras().getDouble("BatteryLimit",-1) != -1)
	    		{
	    			mService.setBattLimitWarning(mConnectedDeviceName, data.getExtras().getDouble("BatteryLimit",-1));
	    		}
	    		
    		}
    		break;
    	case REQUEST_LOGFILE_SHIMMER:
    		Log.d("Shimmer","iam");
    		if (resultCode == Activity.RESULT_OK) {
    			mEnableLogging = data.getExtras().getBoolean("LogFileEnableLogging");
    			if (mEnableLogging==true){
    				mService.setEnableLogging(mEnableLogging);
    			}
    			//set the filename in the LogFile
    			mFileName=data.getExtras().getString("LogFileName");
    			mService.setLoggingName(mFileName);
    			
    			if (mEnableLogging==false){
    	        	mTitleLogging.setText("Logging Disabled");
    	        } else if (mEnableLogging==true){
    	        	mTitleLogging.setText("Logging Enabled");
    	        }
    			
    		}
    		break;
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		MenuItem streamItem = menu.findItem(R.id.stream);
		streamItem.setEnabled(false);
		MenuItem settingsItem = menu.findItem(R.id.settings);
		settingsItem.setEnabled(false);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	
		//disable graph edit for sensors which are not enabled
		
		MenuItem scanItem = menu.findItem(R.id.scan);
		MenuItem streamItem = menu.findItem(R.id.stream);
		MenuItem settingsItem = menu.findItem(R.id.settings);
		MenuItem commandsItem = menu.findItem(R.id.commands);
		MenuItem viewItem = menu.findItem(R.id.viewsensor);
		if((mService.DevicesConnected(mConnectedDeviceName) == true)){
			scanItem.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			scanItem.setTitle(R.string.disconnect);
			streamItem.setIcon(R.drawable.ic_menu_play_clip);
			streamItem.setTitle(R.string.startstream);
			streamItem.setEnabled(true);
			settingsItem.setEnabled(true);
			commandsItem.setEnabled(true);
			viewItem.setEnabled(true);
		}
		else {
			scanItem.setIcon(android.R.drawable.ic_menu_search);
			scanItem.setTitle(R.string.connect);
			streamItem.setIcon(R.drawable.ic_menu_play_clip);
			streamItem.setEnabled(false);
			settingsItem.setEnabled(false);
			commandsItem.setEnabled(false);
			viewItem.setEnabled(false);
		}
		if(mService.DeviceIsStreaming(mConnectedDeviceName) == true && mService.DevicesConnected(mConnectedDeviceName) == true){
			streamItem.setIcon(R.drawable.ic_menu_stop);
			streamItem.setTitle(R.string.stopstream);
			
		}
		if(mService.DeviceIsStreaming(mConnectedDeviceName) == false && mService.DevicesConnected(mConnectedDeviceName) == true && mService.GetInstructionStatus(mConnectedDeviceName)==true){
			streamItem.setIcon(R.drawable.ic_menu_play_clip);
			streamItem.setTitle(R.string.startstream);
		}	
		if (mService.GetInstructionStatus(mConnectedDeviceName)==false || (mService.GetInstructionStatus(mConnectedDeviceName)==false)){ 
			streamItem.setEnabled(false);
			settingsItem.setEnabled(false);
			commandsItem.setEnabled(false);
		}
		if (mService.DeviceIsStreaming(mConnectedDeviceName)){
			settingsItem.setEnabled(false);
			commandsItem.setEnabled(false);
		}
		if (mService.GetInstructionStatus(mConnectedDeviceName)==false)
		{
			streamItem.setEnabled(false);
			settingsItem.setEnabled(false);
			commandsItem.setEnabled(false);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			if ((mService.DevicesConnected(mConnectedDeviceName) == true)) {
				mService.disconnectAllDevices();
			} else {
				Intent serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_SHIMMER);
			}
			return true;

		case R.id.stream:
			if (mService.DeviceIsStreaming(mConnectedDeviceName) == true) {
				mService.stopStreaming(mConnectedDeviceName);
				
			} else {
				mService.startStreaming(mConnectedDeviceName);
				log = new Logging(mFileName,"\t");
			}
			
     		return true;
     	case R.id.settings:
     		Intent confIntent=new Intent(this, ConfigureActivity.class);
     		confIntent.putExtra("BluetoothAddress",mConnectedDeviceName);
			startActivityForResult(confIntent, REQUEST_CONFIGURE_SHIMMER);
			return true;
     	case R.id.viewsensor:
     		Intent viewIntent=new Intent(this, SensorViewActivity.class);
     		viewIntent.putExtra("BluetoothAddress",mConnectedDeviceName);
     		viewIntent.putExtra("Enabled_Sensors", mService.getEnabledSensors(mConnectedDeviceName));
     		startActivityForResult(viewIntent, REQUEST_CONFIGURE_VIEW_SENSOR);
			return true;
     	case R.id.commands:
     		Intent commandIntent=new Intent(this, CommandsActivity.class);
     		commandIntent.putExtra("BluetoothAddress",mConnectedDeviceName);
     		commandIntent.putExtra("SamplingRate",mService.getSamplingRate(mConnectedDeviceName));
     		commandIntent.putExtra("AccelerometerRange",mService.getAccelRange(mConnectedDeviceName));
     		commandIntent.putExtra("GSRRange",mService.getGSRRange(mConnectedDeviceName));
     		commandIntent.putExtra("BatteryLimit",mService.getBattLimitWarning(mConnectedDeviceName));
     		startActivityForResult(commandIntent, REQUEST_COMMAND_SHIMMER);
     		return true;
     	case R.id.logfile:
     		Intent logfileIntent=new Intent(this, LogFileActivity.class);
     		startActivityForResult(logfileIntent, REQUEST_LOGFILE_SHIMMER);
     		return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}