package com.hva.boxlabapp.devices;

import java.util.Arrays;
import java.util.Collection;

import com.hva.boxlabapp.R;
import com.hva.boxlabapp.database.DevicesDatasource;
import com.hva.boxlabapp.devices.Device.Type;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class DeviceUpdater extends DialogFragment {

	private static final String TAG = DialogFragment.class.getName();
	private static final String RASPBERRY = "raspberrypi";
	private Device device;
	private final BaseAdapter adapter;

	public DeviceUpdater() {
		super();
		adapter = null;
	}

	public DeviceUpdater(Device device, BaseAdapter adapter) {
		this.device = device;
		this.adapter = adapter;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final DevicesDatasource db = new DevicesDatasource(getActivity());
		final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.device_manager_dialog, null);
		
		Spinner fieldType = (Spinner) view
				.findViewById(R.id.dialog_device_type);
		fieldType.setAdapter(new ArrayAdapter<Device.Type>(getActivity(),
				android.R.layout.simple_spinner_item, Device.Type.values()));
		fieldType.setSelection(3);
		
		if(device.getId() != 0) {
			EditText name = (EditText) view.findViewById(R.id.dialog_device_name);
			name.setText(device.getName());
			
			if (this.device.getType() != null) {
				Device.Type type = this.device.getType();
				Device.Type[] types = Device.Type.values();
				int index = Arrays.asList(types).indexOf(type);
				fieldType.setSelection(index);
			}
		}
		
		builder.setView(view);
		
		// TODO: Dont special setup for raspberry pi.
		// raspberrypi
		builder.setMessage(R.string.devicemanager_dialog)
				.setPositiveButton(R.string.devicemanager_edit_device,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Log.e(TAG,"Edit button pressed");
								EditText fieldName = (EditText) view.findViewById(R.id.dialog_device_name);
								Spinner fieldType = (Spinner) view.findViewById(R.id.dialog_device_type);
								
								String last4chars = fieldName.getEditableText().toString();
								Device.Type type = (Type) fieldType.getSelectedItem();
								
								Log.e(TAG,"Last 4 chars: " + last4chars);
								Log.e(TAG, "Type: " + type.getDescription());
								
								boolean updated = false;
								boolean found = false;

								if(last4chars != null && type != Device.Type.NULL) {
									Collection<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
									for (BluetoothDevice bluetoothDevice : bondedDevices) {
										Log.e(TAG, "Checking device: " + bluetoothDevice.toString());
										if (type.equals(Device.Type.RASPBERRY) && device.getPosition().equals(Device.Position.SERVER)){
											if(bluetoothDevice.getName().equals(RASPBERRY)){
												device.setMac(bluetoothDevice.getAddress());
												device.setName(RASPBERRY);
												device.setType(Device.Type.RASPBERRY.getId());
												found = true;
												break;
											}
										} else {
											if(bluetoothDevice.getAddress().replace(":", "").endsWith(last4chars)){
												device.setMac(bluetoothDevice.getAddress());
												device.setName(last4chars);
												device.setType(type.getId());
												found = true;
												break;
											}
										}
										
									}
										
									if(!db.getDevices().contains(device) && found == true) {
										if (device.getId() != 0) {
											db.update(device);
											Log.e(TAG, "Device updated: " + device.toString());
										} else {
											device = db.create(device);
											Log.e(TAG, "Device added: " + device.toString());
										}
										updated = true;
									}
								} else {
									Log.e(TAG,"Empty fields");
									Toast.makeText(getActivity(), "Fill the fields before editing.", Toast.LENGTH_SHORT).show();
									return ;
								}
								if(found == true && updated == true) {
									adapter.notifyDataSetChanged();
								} else if (found == true && updated == false) {
									Toast.makeText(getActivity(), "Device already added.", Toast.LENGTH_SHORT).show();
								} else if (found == false && updated == false) {
									Toast.makeText(getActivity(), "Device not found", Toast.LENGTH_SHORT).show();
								}
							}
						})
				.setNeutralButton(R.string.devicemanager_delete_device,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								device = db.delete(device);
								Log.e(TAG,"Delete button pressed");
								adapter.notifyDataSetChanged();
							}
						})
				.setNegativeButton(R.string.devicemanager_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								DeviceUpdater.this.getDialog().cancel();
							}
						});
				
		return builder.create();
	}
}
