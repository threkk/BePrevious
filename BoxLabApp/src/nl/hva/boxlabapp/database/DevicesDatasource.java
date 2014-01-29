package nl.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.List;

import nl.hva.boxlabapp.devices.Device;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Manages the devices attached to the tablet.
 * 
 * @author Alberto Mtnz de Murga
 * @version 1
 * @see Database
 * @see Device
 */
public class DevicesDatasource {

	public static final String TAG = DevicesDatasource.class.getName();

	private SQLiteDatabase database;
	private Database dbHelper;

	public DevicesDatasource(Context context) {
		dbHelper = new Database(context);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		database = null;
		dbHelper.close();
	}

	/**
	 * Add the device to the database.
	 * 
	 * @param device
	 * @return The device with the id updated with its position in the database.
	 */
	public Device create(Device device) {
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(Database.DEVICE_NAME, device.getName());
			values.put(Database.DEVICE_TYPE, device.getType().getId());
			values.put(Database.DEVICE_POSITION, device.getPosition().getId());
			values.put(Database.DEVICE_MAC, device.getMac());

			long id = database.insert(Database.TABLE_DEVICES, null, values);
			device.setId(id);
			Log.d(TAG, "device with id " + id + " was inserted");
		} catch (SQLException e) {
			Log.e(TAG, "Failed to create new device", e);
		} finally {
			close();
		}

		return device;
	}

	/**
	 * Updates the device in the database, based on the id of the class, with
	 * the actual values.
	 * 
	 * @param device
	 * @return <code>true</code> if it is updated. <code>false</code> if not found.
	 */
	public boolean update(Device device) {
		long id = device.getId();
		if (id == 0) {
			Log.i(TAG, "The id value is 0");
			return false;
		}
		int rows = 0;
		String table = Database.TABLE_DEVICES;
		String where = Database.LOCAL_ID + " = " + id;
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(Database.DEVICE_NAME, device.getName());
			values.put(Database.DEVICE_TYPE, device.getType().getId());
			values.put(Database.DEVICE_POSITION, device.getPosition().getId());
			values.put(Database.DEVICE_MAC, device.getMac());

			rows = database.update(table, values, where, null);

			Log.d(TAG, "device with id " + id + " was updated");
		} catch (SQLException e) {
			Log.e(TAG, "Failed to update device with id " + id, e);
			return false;
		} finally {
			close();
		}
		return rows > 0;
	}

	/**
	 * Removes the device from the database based on the id.
	 * 
	 * @param device
	 * @return A new device with all the fields empty but the position.
	 */
	public Device delete(Device device) {
		long id = device.getId();
		Device.Position position = device.getPosition();
		String table = Database.TABLE_DEVICES;
		String where = Database.LOCAL_ID + " = " + id;
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

	/**
	 * List with all the devices in the database. Expected size 4.
	 * @return All the devices in the database.
	 */
	public List<Device> getDevices() {
		List<Device> devices = new ArrayList<Device>();
		Cursor cursor = null;

		String sql = "SELECT " + Database.LOCAL_ID + ", "
				+ Database.DEVICE_NAME + ", " + Database.DEVICE_POSITION + ", "
				+ Database.DEVICE_TYPE + ", " + Database.DEVICE_MAC + " FROM "
				+ Database.TABLE_DEVICES + "; ";

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

	/**
	 * Creates a new device based on the information got after querying the
	 * database.
	 * 
	 * @param cursor
	 * @return A device with the information from the cursor.
	 */
	private Device cursorToDevice(Cursor cursor) {
		int id = cursor.getInt(0);
		String name = cursor.getString(1);
		int position = cursor.getInt(2);
		int type = cursor.getInt(3);
		String mac = cursor.getString(4);

		Device device = new Device(id, name, position, type, mac);
		return device;
	}

}