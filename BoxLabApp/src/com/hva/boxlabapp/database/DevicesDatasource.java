package com.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.List;

import com.hva.boxlabapp.devices.Device;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DevicesDatasource {

	public static final String TAG = DevicesDatasource.class.getName();

	private SQLiteDatabase database;
	private DevicesDatabase dbHelper;

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

	public Device create(Device device) {
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(DevicesDatabase.COLUMN_DEVICE_NAME, device.getName());
			values.put(DevicesDatabase.COLUMN_DEVICE_TYPE, device.getType().getId());
			values.put(DevicesDatabase.COLUMN_DEVICE_POSITION, device.getPosition().getId());
			values.put(DevicesDatabase.COLUMN_DEVICE_MAC, device.getMac());

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

	public void update(Device device) {
		long id = device.getId();
		String table = DevicesDatabase.TABLE_DEVICE;
		String where = DevicesDatabase.COLUMN_DEVICE_ID + " = " + id;
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(DevicesDatabase.COLUMN_DEVICE_NAME, device.getName());
			values.put(DevicesDatabase.COLUMN_DEVICE_TYPE, device.getType().getId());
			values.put(DevicesDatabase.COLUMN_DEVICE_POSITION, device.getPosition().getId());
			values.put(DevicesDatabase.COLUMN_DEVICE_MAC, device.getMac());

			database.update(table, values, where, null);
			
			Log.d(TAG, "device with id " + id + " was updated");
		} catch (SQLException e) {
			Log.e(TAG, "Failed to update device with id " + id, e);
		} finally {
			close();
		}

	}

	public Device delete(Device device) {
		long id = device.getId();
		Device.Position position = device.getPosition();
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
		Device ret = new Device();
		ret.setPosition(position.getId());
		return ret;
	}

	public List<Device> getDevices() {
		List<Device> devices =  new ArrayList<Device>();
		Cursor cursor = null;

		String sql = "SELECT " 
				+ DevicesDatabase.COLUMN_DEVICE_ID + ", "
				+ DevicesDatabase.COLUMN_DEVICE_NAME + ", "
				+ DevicesDatabase.COLUMN_DEVICE_POSITION + ", "
				+ DevicesDatabase.COLUMN_DEVICE_TYPE + ", "
				+ DevicesDatabase.COLUMN_DEVICE_MAC + " FROM "
				+ DevicesDatabase.TABLE_DEVICE + "; ";
		
		try {
			open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				Device device = cursorToDevice(cursor);
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

	private Device cursorToDevice(Cursor cursor) {
		int id = cursor.getInt(0);
		String name = cursor.getString(1);
		int position = cursor.getInt(2);
		int type = cursor.getInt(3);
		String mac = cursor.getString(4);
		
		Device device = new Device(id, name, position, type, mac);
		return device;
	}
	
	private class DevicesDatabase extends SQLiteOpenHelper {

		public static final String TABLE_DEVICE = "device";
		public static final String COLUMN_DEVICE_ID = "_id";
		public static final String COLUMN_DEVICE_NAME = "name";
		public static final String COLUMN_DEVICE_POSITION = "position";
		public static final String COLUMN_DEVICE_TYPE = "type";
		public static final String COLUMN_DEVICE_MAC = "mac";

		private static final String DATABASE_NAME = "database.db";
		private static final int DATABASE_VERSION = 1;

		private static final String DATABASE_CREATE = "create table "
				+ TABLE_DEVICE + "(" 
				+ COLUMN_DEVICE_ID 	+ " integer primary key autoincrement, " 
				+ COLUMN_DEVICE_NAME + " text not null, " 
				+ COLUMN_DEVICE_TYPE + " integer not null, "
				+ COLUMN_DEVICE_POSITION + " integer not null, "
				+ COLUMN_DEVICE_MAC + " text not null"
				+ ");";

		public DevicesDatabase(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion,
				int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			database.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
			onCreate(database);
		}
	}
}