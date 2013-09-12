package com.shimmerresearch.shimmergraphandlogservice;

import java.util.ArrayList;
import java.util.Arrays;

import com.shimmerresearch.shimmergraphandlogservice.R;

import com.shimmerresearch.service.ShimmerService;
import com.shimmerresearch.service.ShimmerService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CommandsActivity extends ServiceActivity {
	public static String mDone = "Done";

	String mBluetoothAddress;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.shimmer_commands);
        
    	Bundle extras = getIntent().getExtras();
        mBluetoothAddress = extras.getString("BluetoothAddress");
    	double mSamplingRateV = extras.getDouble("SamplingRate");
    	int mAccelerometerRangeV = extras.getInt("AccelerometerRange");
    	int mGSRRangeV = extras.getInt("GSRRange");
    	final double batteryLimit = extras.getDouble("BatteryLimit");
    	String[] samplingRate = new String [] {"10","51.2","102.4","128","170.7","204.8","256","512"};
    	String[] accelRange = new String [] {"+/- 1.5g","+/- 6g"};
    	String[] gsrRange = new String [] {"10kOhm to 56kOhm","56kOhm to 220kOhm","220kOhm to 680kOhm","680kOhm to 4.7MOhm","Auto Range"};
    	
    	
    	
    	final ListView listViewSamplingRate = (ListView) findViewById(R.id.listView1);
        final ListView listViewAccelRange = (ListView) findViewById(R.id.listView2);
        final ListView listViewGsrRange = (ListView) findViewById(R.id.listView3);
        final EditText editTextBattLimit = (EditText) findViewById(R.id.editTextBattLimit);
        final TextView textViewCurrentSamplingRate = (TextView) findViewById(R.id.TextView4);
        final TextView textViewCurrentAccelRange = (TextView) findViewById(R.id.textView5);
        final TextView textViewCurrentGsrRange = (TextView) findViewById(R.id.textView6);
        
        textViewCurrentSamplingRate.setTextColor(Color.rgb(0, 135, 202));
        textViewCurrentAccelRange.setTextColor(Color.rgb(0, 135, 202));
        textViewCurrentGsrRange.setTextColor(Color.rgb(0, 135, 202));
        
        textViewCurrentSamplingRate.setText(Double.toString(mSamplingRateV));
        
        editTextBattLimit.setText(Double.toString(batteryLimit));
        
        if (mAccelerometerRangeV==0){
        	textViewCurrentAccelRange.setText("+/- 1.5g");
        }
        else if (mAccelerometerRangeV==3){
        	textViewCurrentAccelRange.setText("+/- 6g");
        }
        
        if (mGSRRangeV==0) {
        	textViewCurrentGsrRange.setText("10kOhm to 56kOhm");
        } else if (mGSRRangeV==1) {
        	textViewCurrentGsrRange.setText("56kOhm to 220kOhm");
        } else if (mGSRRangeV==2) {
        	textViewCurrentGsrRange.setText("220kOhm to 680kOhm");
        } else if (mGSRRangeV==3) {
        	textViewCurrentGsrRange.setText("680kOhm to 4.7MOhm"); 
        } else if (mGSRRangeV==4) {
        	textViewCurrentGsrRange.setText("Auto Range");
        }
        
      
        
    	ArrayList<String> samplingRateList = new ArrayList<String>();  
    	samplingRateList.addAll( Arrays.asList(samplingRate) );  
        ArrayAdapter<String> sR = new ArrayAdapter<String>(CommandsActivity.this, R.layout.commands_name,samplingRateList);
    	listViewSamplingRate.setAdapter(sR);
    	
    	ArrayList<String> accelRangeList = new ArrayList<String>();  
    	accelRangeList.addAll( Arrays.asList(accelRange) );  
        ArrayAdapter<String> sR2 = new ArrayAdapter<String>(CommandsActivity.this, R.layout.commands_name,accelRangeList);
    	listViewAccelRange.setAdapter(sR2);
    	
    	ArrayList<String> gsrRangeList = new ArrayList<String>();  
    	gsrRangeList.addAll( Arrays.asList(gsrRange) );  
        ArrayAdapter<String> sR3 = new ArrayAdapter<String>(CommandsActivity.this, R.layout.commands_name,gsrRangeList);
    	listViewGsrRange.setAdapter(sR3);
    	
    	
    	
    	
    	Button buttonToggleLED = (Button) findViewById(R.id.button1);
    	
    	buttonToggleLED.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
	            intent.putExtra("ToggleLED", true);
	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
			}
        	
        });
    	
    	
    	Button buttonBattVoltLimit = (Button) findViewById(R.id.buttonBattLimit);
    	
    	buttonBattVoltLimit.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				Intent intent = new Intent();
	            intent.putExtra("BatteryLimit", Double.parseDouble(editTextBattLimit.getText().toString()));
	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
			}
    		
    	});
    	
    	buttonToggleLED.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
	            intent.putExtra("ToggleLED", true);
	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
			}
        	
        });
    	
    	listViewSamplingRate.setOnItemClickListener(new AdapterView.OnItemClickListener() {

    		  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

    		    Object o = listViewSamplingRate.getItemAtPosition(position);
    		    Log.d("Shimmer",o.toString());
    		    Intent intent = new Intent();
	            intent.putExtra("SamplingRate",Double.valueOf(o.toString()).doubleValue());
	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
    		    
    		  }
    		});
    	
    	listViewAccelRange.setOnItemClickListener(new AdapterView.OnItemClickListener() {

  		  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

  		    Object o = listViewAccelRange.getItemAtPosition(position);
  		    Log.d("Shimmer",o.toString());
  		    int accelRange=0;
  		    if (o.toString()=="+/- 1.5g"){
  		    	accelRange=0;
  		    } else if (o.toString()=="+/- 6g"){
  		    	accelRange=3;
  		    }
  		    Intent intent = new Intent();
	            intent.putExtra("AccelRange",accelRange);
	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
  		    
  		  }
  		});
    	
    	
    	listViewGsrRange.setOnItemClickListener(new AdapterView.OnItemClickListener() {

    		  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    			
    		    Object o = listViewGsrRange.getItemAtPosition(position);
    		    Log.d("Shimmer",o.toString());
    		    int gsrRange=0;
    		    if (o.toString()=="10kOhm to 56kOhm"){
    		    	gsrRange=0;
    		    } else if (o.toString()=="56kOhm to 220kOhm"){
    		    	gsrRange=1;
    		    } else if (o.toString()=="220kOhm to 680kOhm"){
    		    	gsrRange=2;
    		    } else if (o.toString()=="680kOhm to 4.7MOhm"){
    		    	gsrRange=3;
    		    } else if (o.toString()=="Auto Range"){
    		    	gsrRange=4;
    		    }
    		    Intent intent = new Intent();
  	            intent.putExtra("GSRRange",gsrRange);
  	            // Set result and finish this Activity
  	            setResult(Activity.RESULT_OK, intent);
  	            finish();
    		    
    		  }
    		});
    	
    	
    	
	}
    protected ServiceConnection mTestServiceConnection = new ServiceConnection() {

      	public void onServiceConnected(ComponentName arg0, IBinder service) {
      		// TODO Auto-generated method stub
      		Log.d("ShimmerService", "srvice connected");
      		LocalBinder binder = (ShimmerService.LocalBinder) service;
      		mService = binder.getService();
      		mServiceBind = true;
      		//update the view
      		CheckBox cBox5VReg = (CheckBox) findViewById(R.id.checkBox5VReg);
      		if (mService.get5VReg(mBluetoothAddress)==1){
      			cBox5VReg.setChecked(true);
      		}
      		
      		cBox5VReg.setOnCheckedChangeListener(new OnCheckedChangeListener(){

    			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
    				// TODO Auto-generated method stub
    				if (checked){
    					mService.write5VReg(mBluetoothAddress, 1);
    				} else {
    					mService.write5VReg(mBluetoothAddress, 0);
    				}
    				finish();
    			}
        		
        	});
        	
      		CheckBox cBoxLowPowerMag = (CheckBox) findViewById(R.id.checkBoxLowPowerMag);
      		cBoxLowPowerMag.setChecked(mService.isLowPowerMagEnabled(mBluetoothAddress));
      		
      		cBoxLowPowerMag.setOnCheckedChangeListener(new OnCheckedChangeListener(){

    			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
    				// TODO Auto-generated method stub
    				if (checked){
    					mService.enableLowPowerMag(mBluetoothAddress, true);
    				} else {
    					mService.enableLowPowerMag(mBluetoothAddress, false);
    				}
    				finish();
    			}
        		
        	});
      		
      		}

      	public void onServiceDisconnected(ComponentName arg0) {
      		// TODO Auto-generated method stub
      		mServiceBind = false;
      	}
    };
	
	
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
	
}
