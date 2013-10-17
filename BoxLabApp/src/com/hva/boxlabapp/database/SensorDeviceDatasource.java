package com.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.List;

import com.hva.boxlabapp.model.SensorDevice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SensorDeviceDatasource {

	public static final String TAG = SensorDeviceDatasource.class.getName();

	private SQLiteDatabase database;
	private DatabaseDevices dbHelper;
	private String[] allColumns = { DatabaseDevices.COLUMN_DEVICE_ID,
			DatabaseDevices.COLUMN_DEVICE_NAME,
			DatabaseDevices.COLUMN_DEVICE_TYPE };

	public SensorDeviceDatasource(Context context) {
		dbHelper = new DatabaseDevices(context);
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
			values.put(DatabaseDevices.COLUMN_DEVICE_NAME, device.getName());
			values.put(DatabaseDevices.COLUMN_DEVICE_TYPE, device.getType()
					.getId());

			long id = database
					.insert(DatabaseDevices.TABLE_DEVICE, null, values);
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
		String table = DatabaseDevices.TABLE_DEVICE;
		String where = DatabaseDevices.COLUMN_DEVICE_ID + " = " + id;
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(DatabaseDevices.COLUMN_DEVICE_NAME, device.getName());
			values.put(DatabaseDevices.COLUMN_DEVICE_TYPE, device.getType().getId());

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
		String table = DatabaseDevices.TABLE_DEVICE;
		String where = DatabaseDevices.COLUMN_DEVICE_ID + " = " + id;
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
			cursor = database.query(DatabaseDevices.TABLE_DEVICE, allColumns,
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