package com.hva.boxlabapp;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.hva.boxlabapp.database.SensorDeviceDatasource;
import com.hva.boxlabapp.model.SensorDevice;

public class ManageDevicesActivity extends ListActivity {

	private SensorDeviceDatasource datasource;
	private List<SensorDevice> devices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.datasource = new SensorDeviceDatasource(this);

		setupActionBar();
		updateView();
		getListView().setLongClickable(true);
		getListView().setOnItemLongClickListener(new LongPressListener());
	}

	private void updateView() {
		this.datasource.open();
		this.devices = this.datasource.getAllDevices();
		ListAdapter adapter = new SensorDeviceAdapter(this,
				R.layout.device_manager_row, devices);
		this.datasource.close();

		setListAdapter(adapter);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setSubtitle(R.string.devicemanager_subtitle);
	}

	private void addDevice(SensorDevice device) {
		this.datasource.open();
		this.datasource.create(device);
		this.datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.manage_devices, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add) {
			final ManageDevicesDialogFactory factory = new ManageDevicesDialogFactory();
			factory.setActivity(this);
			factory.showDialog();
			factory.onFinish(new ManageDevicesDialogFactory.FinishedCommand() {

				@Override
				public void finished(boolean cancelled) {
					if (cancelled) {
						return;
					}

					addDevice(factory.getDevice());
					updateView();
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

	protected class LongPressListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos,
				long id) {
			// TODO Auto-generated method stub
			return false;
		}

	}
}
