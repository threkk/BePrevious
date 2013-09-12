package com.shimmerresearch.shimmergraphandlogservice;


import java.lang.ref.WeakReference;

import com.shimmerresearch.driver.Shimmer;
import com.shimmerresearch.service.ShimmerService;
import com.shimmerresearch.service.ShimmerService.LocalBinder;

import com.shimmerresearch.shimmergraphandlogservice.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ConfigureActivity extends ServiceActivity{
	// Return Intent extra
	public static String mDone = "Done";
	private int mReturnEnabledSensors = 0;
	private String mBluetoothAddress="";
	private final int SENSOR_ACCEL=0x80;
	private final int SENSOR_GYRO=0x40;
	private final int SENSOR_MAG=0x20;
	private final int SENSOR_ECG=0x10;
	private final int SENSOR_EMG=0x08;
	private final int SENSOR_GSR=0x04;
	private final int SENSOR_EXP_BOARD_A7=0x02;
	private final int SENSOR_EXP_BOARD_A0=0x01;
	private final int SENSOR_STRAIN_GAUGE=0x8000;
	private final int SENSOR_HEART_RATE=0x4000;
	CheckBox cboxBatt;
   CheckBox cboxGyro;
   CheckBox cboxAccel;
   CheckBox cboxMag;
   CheckBox cboxSG;
   CheckBox cboxECG;
   CheckBox cboxEMG;
   CheckBox cboxGSR;
   CheckBox cboxHR;
   CheckBox cboxA7;
   CheckBox cboxA0;
   private static WeakReference<ConfigureActivity> mConAct;
	
	ConfigureActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	// Setup the window	    
    	setContentView(R.layout.configure);
    	
    	Bundle extras = getIntent().getExtras();
        mBluetoothAddress = extras.getString("BluetoothAddress");
        mConAct = new WeakReference<ConfigureActivity>(this);	     

 	   
	   
	   
    }
    
    protected ServiceConnection mTestServiceConnection = new ServiceConnection() {

      	public void onServiceConnected(ComponentName arg0, IBinder service) {
      		// TODO Auto-generated method stub
      		Log.d("ShimmerService", "srvice connected");
      		final ConfigureActivity mActivity = mConAct.get();

      		LocalBinder binder = (ShimmerService.LocalBinder) service;
      		mService = binder.getService();
      		mServiceBind = true;
      		cboxBatt = (CheckBox) findViewById(R.id.checkBoxBatt);
      	   cboxGyro = (CheckBox) findViewById(R.id.checkBoxGyroscope);
     	   cboxAccel = (CheckBox) findViewById(R.id.checkBoxAccelerometer);
     	   cboxMag = (CheckBox) findViewById(R.id.checkBoxMagnetometer);
     	   cboxSG = (CheckBox) findViewById(R.id.checkBoxStrainGauge);
     	   cboxECG = (CheckBox) findViewById(R.id.checkBoxECG);
     	   cboxEMG = (CheckBox) findViewById(R.id.checkBoxEMG);
     	   cboxGSR = (CheckBox) findViewById(R.id.checkBoxGSR);
     	   cboxHR = (CheckBox) findViewById(R.id.checkBoxHeartRate);
     	   cboxA7 = (CheckBox) findViewById(R.id.checkBoxExpBoardA7);
     	   cboxA0 = (CheckBox) findViewById(R.id.checkBoxExpBoardA0);
      		cboxBatt.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton arg0, boolean check) {
					// TODO Auto-generated method stub
					if (check){
						/*if (mService.getpmux(mBluetoothAddress)==1){
							
						} else {
							//mActivity.message();
						}*/
					}
				}
      			
      			
      		});
      		
     	   cboxBatt.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxGyro.isChecked() || cboxMag.isChecked()  )
   	    		{
   	    			cboxA7.setChecked(false);
   	    			cboxA0.setChecked(false);
   	    		} 
   	    	}
   	    });
      		

       	Button enableDone = (Button) findViewById(R.id.enable_sensors_done);
   	    enableDone.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		//First run through the buttons
   	    		   if (cboxGyro.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | SENSOR_GYRO;
   	    		   }
   	    		   if (cboxAccel.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | SENSOR_ACCEL;
   	    		   }
   	    		   if (cboxMag.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | SENSOR_MAG;
   	    		   }
   	    		   if (cboxSG.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | SENSOR_STRAIN_GAUGE;
   	    		   }
   	    		   if (cboxECG.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | SENSOR_ECG;
   	    		   }
   	    		   if (cboxEMG.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | SENSOR_EMG;
   	    		   }
   	    		   if (cboxGSR.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | SENSOR_GSR;
   	    		   }
   	    		   if (cboxHR.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | SENSOR_HEART_RATE;
   	    		   }
   	    		   if (cboxA7.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | SENSOR_EXP_BOARD_A7;
   	    		   }
   	    		   if (cboxA0.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | SENSOR_EXP_BOARD_A0;
   	    		   }
   	    		   if (cboxBatt.isChecked()) {
   	    			   mReturnEnabledSensors=mReturnEnabledSensors | Shimmer.SENSOR_BATT;
   	    		   }
   	    		
   	    		// Create the result Intent
   	    		Intent intent = new Intent();
   	            intent.putExtra(mDone, mReturnEnabledSensors);
   	            // Set result and finish this Activity
   	            setResult(Activity.RESULT_OK, intent);
   	            mReturnEnabledSensors=0;
   	    		finish();
   	    	}
   	    });
   	    

   	   cboxAccel.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxAccel.isChecked()){
   	    			mReturnEnabledSensors=mReturnEnabledSensors|SENSOR_ACCEL;
   	    		}
   	    		
   	     	}
   	    });
   	   
   	   cboxGyro.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxGyro.isChecked() || cboxMag.isChecked()  )
   	    		{
   	    			cboxSG.setChecked(false);
   	    			cboxGSR.setChecked(false);
   	    			cboxECG.setChecked(false);
   	    			cboxEMG.setChecked(false);
   	    		} 
   	    	}
   	    });
   	   
   	   cboxA7.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxGyro.isChecked() || cboxMag.isChecked()  )
   	    		{
   	    			cboxBatt.setChecked(false);
   	    		} 
   	    	}
   	    });
   	   
   	   cboxA0.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxGyro.isChecked() || cboxMag.isChecked()  )
   	    		{
   	    			cboxBatt.setChecked(false);
   	    		} 
   	    	}
   	    });
   	   

   	   
   	   cboxMag.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxGyro.isChecked() || cboxMag.isChecked()  )
   	    		{
   	    			cboxSG.setChecked(false);
   	    			cboxGSR.setChecked(false);
   	    			cboxECG.setChecked(false);
   	    			cboxEMG.setChecked(false);
   	    		} 
   	    	
   	    	}
   	    });
   	   
   	   cboxSG.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxSG.isChecked())
   	    		{
   	    			cboxGyro.setChecked(false);
   	    			cboxMag.setChecked(false);
   	    			cboxECG.setChecked(false);
   	    			cboxEMG.setChecked(false);
   	    			cboxGSR.setChecked(false);
   	    		} 
   	    	
   	    	}
   	    });
   	   
   	   cboxGSR.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxGSR.isChecked())
   	    		{
   	    			cboxGyro.setChecked(false);
   	    			cboxMag.setChecked(false);
   	    			cboxECG.setChecked(false);
   	    			cboxEMG.setChecked(false);
   	    			cboxSG.setChecked(false);
   	    		}
   	    	
   	    	}
   	    });
   	   
   	   cboxECG.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxECG.isChecked())
   	    		{
   	    			cboxGyro.setChecked(false);
   	    			cboxMag.setChecked(false);
   	    			cboxGSR.setChecked(false);
   	    			cboxEMG.setChecked(false);
   	    			cboxSG.setChecked(false);
   	    		}
   	    	
   	    	}
   	    });
   	   
   	   cboxEMG.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxEMG.isChecked())
   	    		{
   	    			cboxGyro.setChecked(false);
   	    			cboxMag.setChecked(false);
   	    			cboxGSR.setChecked(false);
   	    			cboxECG.setChecked(false);
   	    			cboxSG.setChecked(false);
   	    		}
   	    	
   	    	}
   	    });
   	   
   	   cboxHR.setOnClickListener(new OnClickListener() {
   	    	public void onClick(View v) {
   	    		if (cboxHR.isChecked())
   	    		{
   	    		}
   	    	}
   	    });
   	   
   	   
   	   
   	   
      		//update the view
      		
        	
      		}

      	public void onServiceDisconnected(ComponentName arg0) {
      		// TODO Auto-generated method stub
      		mServiceBind = false;
      	}
    };
    
    public void message(){
    	Toast.makeText(this, "Please enable PMUX first", Toast.LENGTH_LONG).show();
    }
    
	public void onPause() {
		super.onPause();
		Log.d("Shimmer","On Pause");
		//finish();
		if(mServiceBind == true){
  		  getApplicationContext().unbindService(mTestServiceConnection);
  	  }
	}

	public void onResume() {
		super.onResume();
		Log.d("Shimmer","On Resume");
		Intent intent=new Intent(this, ShimmerService.class);
  	  	Log.d("ShimmerH","on Resume");
  	  	getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
	}
	/*
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	*/
}
