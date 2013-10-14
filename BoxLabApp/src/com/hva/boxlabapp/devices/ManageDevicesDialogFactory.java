package com.hva.boxlabapp.devices;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.hva.boxlabapp.R;
import com.hva.boxlabapp.model.SensorDevice;
import com.hva.boxlabapp.model.SensorDevice.Type;

public class ManageDevicesDialogFactory {

	private SensorDevice device;
	private Activity activity;
	private FinishedCommand finishedCommand;

	public SensorDevice getDevice() {
		return device;
	}

	public void setDevice(SensorDevice device) {
		this.device = device;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	private View createView() {
		LayoutInflater inflater = this.activity.getLayoutInflater();
		View view = inflater.inflate(R.layout.device_manager_dialog, null);

		// set the field with the name of the device (if the device has a name)
		EditText fieldName = (EditText) view
				.findViewById(R.id.dialog_device_name);
		if (this.device.getName() != null) {
			fieldName.setText(this.device.getName());
		}

		// add a list adapter to the spinner field, and select the correct type
		Spinner fieldType = (Spinner) view
				.findViewById(R.id.dialog_device_type);
		fieldType.setAdapter(new ArrayAdapter<SensorDevice.Type>(activity,
				android.R.layout.simple_spinner_item, SensorDevice.Type
						.values()));
		if (this.device.getType() != null) {
			SensorDevice.Type type = this.device.getType();
			SensorDevice.Type[] types = SensorDevice.Type.values();
			int index = Arrays.asList(types).indexOf(type);
			fieldType.setSelection(index);
		}

		return view;
	}

	public void showDialog() {
		String title = "Edit device";
		if (this.device == null) {
			this.device = new SensorDevice();
			title = "Add a new device";
		}

		View view = createView();
		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
				.setTitle(title).setView(view)
				.setPositiveButton("Ok", new OKListener(view))
				.setNegativeButton("Cancel", new CancelListener());
		builder.show();
	}

	public void finish(boolean cancelled) {
		if (this.finishedCommand != null) {
			this.finishedCommand.finished(cancelled);
		}
	}

	public void onFinish(FinishedCommand finishedCommand) {
		this.finishedCommand = finishedCommand;
	}

	public interface FinishedCommand {
		public void finished(boolean cancelled);
	}

	protected class OKListener implements DialogInterface.OnClickListener {

		private View view;

		private OKListener(View view) {
			this.view = view;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {

			final EditText fieldName = (EditText) view
					.findViewById(R.id.dialog_device_name);
			final Spinner fieldType = (Spinner) view
					.findViewById(R.id.dialog_device_type);

			String name = fieldName.getEditableText().toString();
			SensorDevice.Type type = (Type) fieldType.getSelectedItem();

			device.setName(name);
			device.setType(type);

			finish(false);
		}
	}

	protected class CancelListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			finish(true);
		}
	}
}
