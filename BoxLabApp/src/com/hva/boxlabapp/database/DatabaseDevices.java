package com.hva.boxlabapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseDevices extends SQLiteOpenHelper {

	public static final String TAG = DatabaseDevices.class.getName();

	public static final String TABLE_DEVICE = "device";
	public static final String COLUMN_DEVICE_ID = "_id";
	public static final String COLUMN_DEVICE_NAME = "name";
	public static final String COLUMN_DEVICE_TYPE = "type";

	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_DEVICE + "(" 
			+ COLUMN_DEVICE_ID 	+ " integer primary key autoincrement, " 
			+ COLUMN_DEVICE_NAME + " text not null, " 
			+ COLUMN_DEVICE_TYPE + " integer not null"
			+ ");";

	public DatabaseDevices(Context context) {
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