package nl.hva.boxlabapp;

import java.util.Date;
import java.util.List;

import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.serializer.JSONEntitySerializer;
import nl.hva.boxlabapp.bluetooth.ConnectToRaspberryPi;
import nl.hva.boxlabapp.database.DevicesDatasource;
import nl.hva.boxlabapp.database.MessagesDatasource;
import nl.hva.boxlabapp.database.ScheduleDatasource;
import nl.hva.boxlabapp.devices.Device;
import nl.hva.boxlabapp.devices.DeviceActivity;
import nl.hva.boxlabapp.entities.ExerciseEntryItem;
import nl.hva.boxlabapp.entities.MessageItem;
import nl.hva.boxlabapp.utils.TabListenerImpl;

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

import nl.hva.boxlabapp.R;

/**
 * Main activity of the application. Most of the content is displayed inside
 * this view. Its purpose is to manage the different tabs where each one
 * implements one different feature.
 * 
 * @author Alberto Mtnz de Murga
 * @version 1
 */
@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {

	private final static String TAG = MainActivity.class.getName();

	/**
	 * Tab intent identifier
	 */
	public final static String TAB = "TAB";

	/**
	 * Bluetooth adapter needed for synchronization
	 */
	private BluetoothAdapter mBluetoothAdapter = null;
	private String mJson;

	// Android UI

	/**
	 * Loads the tabs, checks the bluetooth and tweaks the back button
	 * behaviour.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the action bar and set the tabs.
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
						new TabListenerImpl<FragmentMessages>(this, "messages",
								FragmentMessages.class));
		Tab tabLibrary = bar
				.newTab()
				.setText(getText(R.string.fragment_library))
				.setTabListener(
						new TabListenerImpl<FragmentLibrary>(this, "library",
								FragmentLibrary.class));

		bar.addTab(tabLanding);
		bar.addTab(tabLibrary);
		bar.addTab(tabFeedback);

		// The next 4 lines are needed to handle the back button behaviour.
		Intent intent = getIntent();
		int tab = intent.getIntExtra(TAB, 0);
		Tab initial = bar.getTabAt(tab);
		bar.selectTab(initial);

		// Bluetooth sync.
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		int bt_response = 1;
		if (mBluetoothAdapter == null) {
			Toast.makeText(this,
					"Device does not support Bluetooth\nExiting...",
					Toast.LENGTH_LONG).show();
			this.finish();
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBt, bt_response);
		}

		if (bt_response == RESULT_CANCELED) {
			Toast.makeText(this,
					"The app needs bluetooth enabled to work\nExiting...",
					Toast.LENGTH_LONG).show();
			this.finish();
		}

	}

	/**
	 * Enables the bluetooth if not connected.
	 */
	@Override
	public void onStart() {
		super.onStart();

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 0);
		}
	}

	/**
	 * Actionbar extra buttons.
	 * 
	 * @param menu
	 * @return <code>true</code> if it is displayed.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Manages the extra buttons in the tool bar. The first button is the
	 * Settings button, which will launch the configuration activity of the app.
	 * The second button is used for development purposes. Right now, populates
	 * the schedule and the messages. The third button is the sync button. The
	 * user will press it to sync with the BoxLab.
	 * 
	 * Actually, the third button is not operative. It needs to be done adapted
	 * to the new version of the models.
	 * 
	 * @param item
	 *            The button pressed.
	 * @return <code>true</code> to show.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(MainActivity.this, DeviceActivity.class));
			return true;
		case R.id.action_about:
			// This button is used as development button. Right now, it
			// populates the
			// schedule and the chat. Probably will disappear in the production
			// version.

			ExerciseEntryItem entry = new ExerciseEntryItem("test", new Date(),
					new Date(), 3, "10 10 10", "Nothing else", false);
			ScheduleDatasource db = new ScheduleDatasource(this);
			db.create(entry);

			MessageItem message = new MessageItem(new Date(), "Hoi!", false);
			MessagesDatasource fbdb = new MessagesDatasource(this);
			fbdb.create(message);

			return true;
		case R.id.action_refresh:
			// Sync button.

			Handler mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					ScheduleDatasource db = new ScheduleDatasource(
							getApplicationContext());
					Log.i(TAG, "Getting data...");
					mJson = msg.getData().getString(ConnectToRaspberryPi.JSON);
					String[] entries = mJson
							.split(ConnectToRaspberryPi.SEPARATOR);

					Log.i(TAG, "Starting deserialization");
					JSONEntitySerializer serializer = new JSONEntitySerializer();
					for (String jentry : entries) {
						ExerciseEntry entry = serializer.deserialize(
								ExerciseEntry.class, jentry);
						ExerciseEntryItem schedule = new ExerciseEntryItem(
								entry);
						db.create(schedule);
						Log.e(TAG, schedule.toString());
					}
					Toast.makeText(getApplicationContext(),
							"Synchronization finished. Have a nice day :)",
							Toast.LENGTH_SHORT).show();
				}
			};

			// Connect to the raspberry Pi
			Toast.makeText(this, "Starting synchronization, wait...",
					Toast.LENGTH_LONG).show();
			DevicesDatasource devicesDb = new DevicesDatasource(this);
			// The list is having at most 4 elements, so it's trivial to iterate
			// it.
			List<Device> devices = devicesDb.getDevices();
			Device server = null;
			for (Device device : devices) {
				if (device.getPosition() == Device.Position.SERVER) {
					server = device;
					break;
				}
			}

			if (server == null) {
				Toast.makeText(this, "Server not found.", Toast.LENGTH_SHORT)
						.show();
				return true;
			}

			ConnectToRaspberryPi connection = new ConnectToRaspberryPi(
					server.getMac(), mBluetoothAdapter, mHandler);
			connection.start();
			// Temporary. The feedback from the sensors will be sent here.
			// Once decided what is going to be sent.
			connection.write("This is a test string.\r\n".getBytes());
			connection.write("Whatever can be sent, as it is accepted.\r\n"
					.getBytes());
			// Needed at the moment to close the connection with the server.
			connection.write("EXIT\r\n".getBytes());
			Toast.makeText(this, "Synchronization finished.",
					Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Changes the behaviour of the back button to move around the tabs instead
	 * of closing the application.
	 */
	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		// Needed to keep the current tab and schedule selected.
		ExerciseEntryItem exercise = (ExerciseEntryItem) intent
				.getSerializableExtra(FragmentSchedule.EXERCISE);

		if (exercise != null) {
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
