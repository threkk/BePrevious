package com.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.List;

import com.hva.boxlabapp.model.SensorDevice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SensorDeviceDatasource {

	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private String[] allColumns = { DatabaseHelper.COLUMN_DEVICE_ID,
			DatabaseHelper.COLUMN_DEVICE_NAME,
			DatabaseHelper.COLUMN_DEVICE_TYPE };

	public SensorDeviceDatasource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		database = null;
		dbHelper.close();
	}

	public SensorDevice create(SensorDevice device) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_DEVICE_NAME, device.getName());
		values.put(DatabaseHelper.COLUMN_DEVICE_TYPE, device.getType().getId());

		device.setId(database.insert(DatabaseHelper.TABLE_DEVICE, null, values));

		return device;
	}

	public void deleteDevice(SensorDevice comment) {
		long id = comment.getId();
		database.delete(DatabaseHelper.TABLE_DEVICE,
				DatabaseHelper.COLUMN_DEVICE_ID + " = " + id, null);
		System.out.println("Device deleted with id: " + id);
	}

	public List<SensorDevice> getAllDevices() {
		List<SensorDevice> devices = new ArrayList<SensorDevice>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_DEVICE, allColumns,
				null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			SensorDevice device = cursorToDevice(cursor);
			devices.add(device);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		return devices;
	}

	private SensorDevice cursorToDevice(Cursor cursor) {
		SensorDevice device = new SensorDevice();
		device.setId(cursor.getLong(0));
		device.setName(cursor.getString(1));
		device.setType(SensorDevice.Type.valueOf(cursor.getInt(2)));
		return device;
	}
}