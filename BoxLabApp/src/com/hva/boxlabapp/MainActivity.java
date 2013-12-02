package com.hva.boxlabapp;

import java.util.Date;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.remote.JSONEntitySerializer;

import com.hva.boxlabapp.bluetooth.BluetoothReaderHandler;
import com.hva.boxlabapp.bluetooth.ConnectToRaspberryPi;
import com.hva.boxlabapp.database.ScheduleDatasource;
import com.hva.boxlabapp.database.entities.Schedule;
import com.hva.boxlabapp.devices.ManageDevicesActivity;
import com.hva.boxlabapp.utils.TabListenerImpl;

public class MainActivity extends Activity {

	private BluetoothAdapter mBluetoothAdapter = null;

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

		Tab tabExercises = bar
				.newTab()
				.setText(getText(R.string.fragment_exercises))
				.setTabListener(
						new TabListenerImpl<FragmentExercises>(this,
								"exercises", FragmentExercises.class));
		Tab tabLibrary = bar
				.newTab()
				.setText(getText(R.string.fragment_library))
				.setTabListener(
						new TabListenerImpl<FragmentLibrary>(this, "library",
								FragmentLibrary.class));

		bar.addTab(tabLanding);
		bar.addTab(tabExercises);
		bar.addTab(tabLibrary);

		initBT();
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
			startActivity(new Intent(MainActivity.this,
					ManageDevicesActivity.class));
			return true;
		case R.id.action_about:
			// Meanwhile, we can use this for testing purposes.
			Schedule entry = new Schedule("test", new Date(), 3, "10 10 10", "Nothing else", false);
			ScheduleDatasource db = new ScheduleDatasource(this);
			Schedule ret = db.create(entry);
			return ret.get_id() != -1;
		case R.id.action_refresh:
			
			return btSync();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		ActionBar bar = getActionBar();
		Tab schedule = bar.getTabAt(0);
		if (bar.getSelectedTab().equals(schedule)) {
			this.finish();
		} else {
			bar.selectTab(schedule);
		}
	}

	private void initBT() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		int BT_RESPONSE = 1;
		if (mBluetoothAdapter == null) {
			Toast.makeText(this,
					"Device does not support Bluetooth\nExiting...",
					Toast.LENGTH_LONG).show();
			finish();
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBt, BT_RESPONSE);
		}

		if (BT_RESPONSE == RESULT_CANCELED) {
			Toast.makeText(this,
					"The app needs bluetooth enabled to work\nExiting...",
					Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	private boolean btSync(){
		Schedule ret;
		
		// Create the handler
		BluetoothReaderHandler btRh = new BluetoothReaderHandler();
		// Connect to the raspberry Pi
		ConnectToRaspberryPi connection = new ConnectToRaspberryPi(
				"00:27:13:A5:9F:9F", mBluetoothAdapter, btRh);
		connection.start();

		// Maybe this will work
		String json = btRh.getData();
		if(json == "EOF") return false;
		// Maarten's shit
		JSONEntitySerializer serializer = new JSONEntitySerializer();
		ExerciseEntry entry = serializer.deserialize(ExerciseEntry.class, json);
		// My shit
		Schedule schedule = Schedule.fromExerciseEntryToSchedule(entry);
		
		// Insertion in the database. Bitches love databases.
		ScheduleDatasource db = new ScheduleDatasource(this);
		ret = db.create(schedule);
		return ret.get_id() != -1;
	}
	
	
}
