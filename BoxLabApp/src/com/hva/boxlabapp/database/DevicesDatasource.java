package com.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.List;

import com.hva.boxlabapp.devices.SensorDevice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DevicesDatasource {

	public static final String TAG = DevicesDatasource.class.getName();

	private SQLiteDatabase database;
	private DevicesDatabase dbHelper;
	private String[] allColumns = { DevicesDatabase.COLUMN_DEVICE_ID,
			DevicesDatabase.COLUMN_DEVICE_NAME,
			DevicesDatabase.COLUMN_DEVICE_TYPE };

	public DevicesDatasource(Context context) {
		dbHelper = new DevicesDatabase(context);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		database = null;
		dbHelper.close();
	}

	public SensorDevice create(SensorDevice device) {
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(DevicesDatabase.COLUMN_DEVICE_NAME, device.getName());
			values.put(DevicesDatabase.COLUMN_DEVICE_TYPE, device.getType()
					.getId());

			long id = database
					.insert(DevicesDatabase.TABLE_DEVICE, null, values);
			device.setId(id);
			Log.d(TAG, "device with id " + id + " was inserted");
		} catch (SQLException e) {
			Log.e(TAG, "Failed to create new device", e);
		} finally {
			close();
		}

		return device;
	}

	public void update(SensorDevice device) {
		long id = device.getId();
		String table = DevicesDatabase.TABLE_DEVICE;
		String where = DevicesDatabase.COLUMN_DEVICE_ID + " = " + id;
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(DevicesDatabase.COLUMN_DEVICE_NAME, device.getName());
			values.put(DevicesDatabase.COLUMN_DEVICE_TYPE, device.getType().getId());

			database.update(table, values, where, null);
			
			Log.d(TAG, "device with id " + id + " was updated");
		} catch (SQLException e) {
			Log.e(TAG, "Failed to update device with id " + id, e);
		} finally {
			close();
		}

	}

	public void delete(SensorDevice device) {
		long id = device.getId();
		String table = DevicesDatabase.TABLE_DEVICE;
		String where = DevicesDatabase.COLUMN_DEVICE_ID + " = " + id;
		try {
			open();
			database.delete(table, where, null);
			Log.d(TAG, "device with id " + id + " was deleted");
		} catch (SQLException e) {
			Log.e(TAG, "Failed to delete device with id " + id, e);
		} finally {
			close();
		}
	}

	public List<SensorDevice> getDevices() {
		List<SensorDevice> devices = null;
		Cursor cursor = null;
		try {
			open();
			devices = new ArrayList<SensorDevice>();
			cursor = database.query(DevicesDatabase.TABLE_DEVICE, allColumns,
					null, null, null, null, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				SensorDevice device = cursorToDevice(cursor);
				devices.add(device);
				cursor.moveToNext();
			}
		} catch (SQLException e) {
			Log.e(TAG, "Failed to retrieve devices", e);
		} finally {
			close();
			if (cursor != null) {
				cursor.close();
			}
		}

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