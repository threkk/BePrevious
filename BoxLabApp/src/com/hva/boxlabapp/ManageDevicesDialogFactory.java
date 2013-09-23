package com.hva.boxlabapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.hva.boxlabapp.model.SensorDevice;
import com.hva.boxlabapp.model.SensorDevice.Type;

public class ManageDevicesDialogFactory {

	private SensorDevice device;
	private Activity activity;
	private FinishedCommand finishedCommand;

	public void setDevice(SensorDevice device) {
		this.device = device;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void showDialog() {
		String title = "Edit device";
		if (this.device == null) {
			this.device = new SensorDevice();
			title = "Add a new device";
		}

		LayoutInflater inflater = this.activity.getLayoutInflater();
		View view = inflater.inflate(R.layout.device_manager_dialog, null);

		Spinner spinnerType = (Spinner) view
				.findViewById(R.id.device_dialog_type);
		spinnerType.setAdapter(new ArrayAdapter<SensorDevice.Type>(activity,
				android.R.layout.simple_spinner_item, SensorDevice.Type
						.values()));

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

			final EditText editID = (EditText) view
					.findViewById(R.id.device_dialog_id);
			final Spinner spinnerType = (Spinner) view
					.findViewById(R.id.device_dialog_type);

			String deviceID = editID.getEditableText().toString();
			SensorDevice.Type type = (Type) spinnerType.getSelectedItem();

			device.setName(deviceID);
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
