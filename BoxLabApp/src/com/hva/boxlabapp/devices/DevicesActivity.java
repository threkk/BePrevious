package com.hva.boxlabapp.devices;

import java.util.ArrayList;
import java.util.List;

import com.hva.boxlabapp.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DevicesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(R.string.devicemanager_title);
		getActionBar().setSubtitle(R.string.devicemanager_subtitle);
		
		final ListView mListView = (ListView) findViewById(R.id.device_list);
		List<Device> devices = new ArrayList<Device>();
		final DeviceAdapter adapter = new DeviceAdapter(this,devices);
		mListView.setAdapter(adapter);
	}

	private class DeviceAdapter extends BaseAdapter {

		private Device[] devices;
		private Context context;

		public DeviceAdapter(Context context, List<Device> source) {
			this.context = context;
			this.devices = new Device[4];
			
			for(Device device : source){
				switch (device.getPosition().id){
				case 0 : this.devices[0] = device; break;
				case 1 : this.devices[1] = device; break;
				case 2 : this.devices[2] = device; break;
				case 3 : this.devices[3] = device; break;
				default : break;
				}
			}
			
			for(int i = 0; i < this.devices.length; i++){
				if(this.devices[i] == null){
					this.devices[i] = new Device();
					this.devices[i].setPosition(i);
				}
			}
		}

		@Override
		public int getCount() {
			return devices.length;
		}

		@Override
		public Object getItem(int position) {
			return devices[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.item_device, parent, false);
			
			TextView mPosition = (TextView) view.findViewById(R.id.device_position);
			mPosition.setText(devices[position].getPosition().getName());
			
			TextView mName = (TextView) view.findViewById(R.id.device_name);
			mName.setText(devices[position].getName());
			
			TextView mType = (TextView) view.findViewById(R.id.device_type);
			mType.setText(devices[position].getType().getDescription());

			return view;
		}

	}
}
