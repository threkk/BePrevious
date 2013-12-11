package com.hva.boxlabapp.deprecated;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.hva.boxlabapp.R;
import com.hva.boxlabapp.database.DevicesDatasource;

public class ManageDevicesActivity extends ListActivity {

	private DevicesDatasource datasource;
	private List<SensorDevice> devices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.datasource = new DevicesDatasource(this);

		setupActionBar();
		updateView();
		registerForContextMenu(getListView());
		getListView().setLongClickable(true);
		getListView().setOnItemClickListener(new ItemClickListener());
	}

	private void updateView() {
		this.devices = this.datasource.getDevices();
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

	private void addDevice() {
		final ManageDevicesDialogFactory factory = new ManageDevicesDialogFactory();
		factory.setActivity(this);
		factory.showDialog();
		factory.onFinish(new ManageDevicesDialogFactory.FinishedCommand() {

			@Override
			public void finished(boolean cancelled) {
				if (cancelled) {
					return;
				}

				datasource.create(factory.getDevice());
				updateView();
			}
		});
	}

	private void editDevice(SensorDevice device) {
		final ManageDevicesDialogFactory factory = new ManageDevicesDialogFactory();
		factory.setDevice(device);
		factory.setActivity(this);
		factory.showDialog();
		factory.onFinish(new ManageDevicesDialogFactory.FinishedCommand() {

			@Override
			public void finished(boolean cancelled) {
				if (cancelled) {
					return;
				}

				datasource.update(factory.getDevice());
				updateView();
			}
		});
	}

	private void deleteDevice(final SensorDevice device) {
		String title = "Deleting device";
		String message = "Are you sure you want to delete device "
				+ device.getName();

		OnClickListener deleteListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				datasource.delete(device);
				updateView();
			}
		};

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert).setTitle(title)
				.setMessage(message).setPositiveButton("Yes", deleteListener)
				.setNegativeButton("No", null).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.manage_devices, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//int itemID = item.getItemId();
		if (item.getItemId() == R.id.action_add) {
			addDevice();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v == getListView()) {
			menu.setHeaderTitle("Select an option");
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.manage_devices_context, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (this.devices == null) {
			return true;
		}

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		SensorDevice device = this.devices.get(info.position);

		switch (item.getItemId()) {
		case R.id.action_edit:
			editDevice(device);
			return true;
		case R.id.action_delete:
			deleteDevice(device);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
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

	protected class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (devices == null) {
				return;
			}

			editDevice(devices.get(position));
		}
	}
}
