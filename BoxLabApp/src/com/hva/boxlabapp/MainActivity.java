package com.hva.boxlabapp;

import java.util.Date;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.hva.boxlabapp.bluetooth.ConnectToRaspberryPi;
import com.hva.boxlabapp.database.FeedbackDatasource;
import com.hva.boxlabapp.database.ScheduleDatasource;
import com.hva.boxlabapp.devices.DeviceActivity;
import com.hva.boxlabapp.entities.MessageItem;
import com.hva.boxlabapp.entities.Schedule;
import com.hva.boxlabapp.entities.client.ExerciseEntry;
import com.hva.boxlabapp.entities.client.JSONEntitySerializer;
import com.hva.boxlabapp.utils.TabListenerImpl;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {
	
	public final static String TAB = "TAB";
	
	private final static String TAG = MainActivity.class.getName();
	private BluetoothAdapter mBluetoothAdapter = null;
	private String mJson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tabLanding = bar
				.newTab()
				.setText(getText(R.string.fragment_schedule))
				.setTabListener(
						new TabListenerImpl<FragmentSchedule>(this, "schedule",
								FragmentSchedule.class));

		Tab tabFeedback = bar
				.newTab()
				.setText(getText(R.string.fragment_messages))
				.setTabListener(
						new TabListenerImpl<FragmentMessages>(this,
								"messages", FragmentMessages.class));
		Tab tabLibrary = bar
				.newTab()
				.setText(getText(R.string.fragment_library))
				.setTabListener(
						new TabListenerImpl<FragmentLibrary>(this, "library",
								FragmentLibrary.class));

		bar.addTab(tabLanding);
		bar.addTab(tabLibrary);
		bar.addTab(tabFeedback);

		Intent intent = getIntent();
		int tab = intent.getIntExtra(TAB, 0);
		Tab initial = bar.getTabAt(tab);
		bar.selectTab(initial);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 
		int bt_response = 1;
		if (mBluetoothAdapter == null) {
			Toast.makeText(this,
					"Device does not support Bluetooth\nExiting...",
					Toast.LENGTH_LONG).show();
			finish();
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBt, bt_response);
		}

		if (bt_response == RESULT_CANCELED) {
			Toast.makeText(this,
					"The app needs bluetooth enabled to work\nExiting...",
					Toast.LENGTH_LONG).show();
			finish();
		}
		
	}

	@Override
	public void onStart() {
		super.onStart();

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 0);
		}
	}

	// Actionbar extra buttons
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(MainActivity.this, DeviceActivity.class));
			return true;
		case R.id.action_about:
			// Meanwhile, we can use this for testing purposes.
			Schedule entry = new Schedule("test", new Date(), 3, "10 10 10", "Nothing else", false);
			ScheduleDatasource db = new ScheduleDatasource(this);
			Schedule ret = db.create(entry);
			
			MessageItem message = new MessageItem(new Date(), "Hoi!", false); 
			FeedbackDatasource fbdb = new FeedbackDatasource(this);
			fbdb.create(message);
			
			
			return ret.get_id() != -1;
		case R.id.action_refresh:
			
			Handler mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					ScheduleDatasource db = new ScheduleDatasource(getApplicationContext());
					Log.e(TAG, "Getting data...");
					mJson = msg.getData().getString(ConnectToRaspberryPi.JSON);
					String[] entries = mJson.split(ConnectToRaspberryPi.SEPARATOR);
					
					Log.e(TAG, "Starting deserialization");
					JSONEntitySerializer serializer = new JSONEntitySerializer();
					for(String jentry : entries) {
						ExerciseEntry entry = serializer.deserialize(ExerciseEntry.class, jentry);
						Schedule schedule = Schedule.fromExerciseEntryToSchedule(entry);
						db.create(schedule);
						Log.e(TAG,schedule.toString());
					}
					Toast.makeText(getApplicationContext(), "Synchronization finished. Have a nice day :)", Toast.LENGTH_SHORT).show();
				}
			};

			//"00:27:13:A5:9F:9F"
			// Connect to the raspberry Pi
			Toast.makeText(this, "Starting synchronization, wait...", Toast.LENGTH_LONG).show();
			ConnectToRaspberryPi connection = new ConnectToRaspberryPi(
					"00:09:DD:50:5C:BF", mBluetoothAdapter, mHandler);
			connection.start();
			// Temporary.
			connection.write("I will send you all my shit:\r\n".getBytes());
			connection.write("Your server is a pain in the ass\r\n".getBytes());
			connection.write("Just that\r\n".getBytes());
			connection.write("Here is your fucking exit with slashes.\r\n".getBytes());
			connection.write("EXIT\r\n".getBytes());

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		Schedule exercise = (Schedule) intent.getSerializableExtra(FragmentSchedule.EXERCISE);

		if(exercise != null) {
			this.finish();
		} else {
			ActionBar bar = getActionBar();
			Tab schedule = bar.getTabAt(0);
			if (bar.getSelectedTab().equals(schedule)) {
				this.finish();
			} else {
				bar.selectTab(schedule);
			}
		}
	}
}
