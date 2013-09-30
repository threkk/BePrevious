package com.hva.boxlabapp.devices;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hva.boxlabapp.R;
import com.hva.boxlabapp.R.id;
import com.hva.boxlabapp.R.layout;
import com.hva.boxlabapp.R.menu;
import com.hva.boxlabapp.R.string;
import com.hva.boxlabapp.model.SensorDevice;

public class ManageDevicesActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();

		List<SensorDevice> devices = new ArrayList<SensorDevice>();
		devices.add(new SensorDevice("3B33", SensorDevice.Type.SHIMMER_2R));
		devices.add(new SensorDevice("GB21", SensorDevice.Type.SHIMMER_2R));
		devices.add(new SensorDevice("6B54", SensorDevice.Type.SHIMMER_2R));

		ListAdapter adapter = new SensorDeviceAdapter(this,
				R.layout.device_manager_row, devices);

		setListAdapter(adapter);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setSubtitle(R.string.devicemanager_subtitle);
	}

	// private void editSensorDevice(SensorDevice device) {
	// String title = "Edit device";
	// if (device.getId() == null && device.getType() == null) {
	// title = "Add a new device";
	// }
	//
	// LayoutInflater inflater = getLayoutInflater();
	// View view = inflater.inflate(R.layout.device_manager_dialog, null);
	// final EditText editID = (EditText) view
	// .findViewById(R.id.device_dialog_id);
	// final Spinner spinnerType = (Spinner) view
	// .findViewById(R.id.device_dialog_type);
	//
	// spinnerType.setAdapter(new ArrayAdapter<SensorDevice.Type>(this,
	// android.R.layout.simple_spinner_item, SensorDevice.Type
	// .values()));
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(this)
	// .setTitle(title)
	// .setView(view)
	// .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// Log.i("DEBUG", "id: "
	// + editID.getEditableText().toString());
	//
	// }
	// })
	// .setNegativeButton("Cancel",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int whichButton) {
	// // Canceled.
	// }
	// });
	//
	// builder.show();
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.manage_devices, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemID = item.getItemId();
		if (item.getItemId() == R.id.action_add) {
			ManageDevicesDialogFactory factory = new ManageDevicesDialogFactory();
			factory.setActivity(this);
			factory.showDialog();

			factory.onFinish(new ManageDevicesDialogFactory.FinishedCommand() {

				@Override
				public void finished(boolean cancelled) {
					Toast.makeText(ManageDevicesActivity.this,
							"Cancelled: " + cancelled, Toast.LENGTH_SHORT)
							.show();
				}
			});

		}
		return super.onOptionsItemSelected(item);
	}

	protected class SensorDeviceAdapter extends ArrayAdapter<SensorDevice> {

		private Context context;

		public SensorDeviceAdapter(Context context, int textViewResourceId,
				List<SensorDevice> items) {
			super(context, textViewResourceId, items);
			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.device_manager_row, null);
			}

			SensorDevice item = getItem(position);
			if (item != null) {
				TextView titleView = (TextView) view
						.findViewById(R.id.row_title);
				TextView subTitleView = (TextView) view
						.findViewById(R.id.row_subtitle);

				if (titleView != null && subTitleView != null) {
					titleView.setText(item.getName());
					subTitleView.setText(item.getType().getDescription());
				}
			}

			return view;
		}
	}
}
