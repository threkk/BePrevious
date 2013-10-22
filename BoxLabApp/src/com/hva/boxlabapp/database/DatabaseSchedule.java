package com.hva.boxlabapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseSchedule extends SQLiteOpenHelper {

	public static final String TAG = DatabaseSchedule.class.getName();

	public static final String TABLE_SCHEDULE = "schedule";
	public static final String COLUMN_DEVICE_ID = "_id";
	public static final String COLUMN_DEVICE_TITLE = "title";
	public static final String COLUMN_DEVICE_DATE = "date";
	public static final String COLUMN_DEVICE_EXID = "exercise_id";
	public static final String COLUMN_DEVICE_TIPS = "tips";
	public static final String COLUMN_DEVICE_REPS = "repetitions";
	public static final String COLUMN_DEVICE_DONE = "done";


	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SCHEDULE + "(" 
			+ COLUMN_DEVICE_ID 	+ " integer primary key autoincrement, " 
			+ COLUMN_DEVICE_TITLE + " text not null, " 
			+ COLUMN_DEVICE_DATE + " integer not null,"
			+ COLUMN_DEVICE_EXID + " integer not null,"
			+ COLUMN_DEVICE_TIPS + " text,"
			+ COLUMN_DEVICE_REPS + " integer,"
			+ COLUMN_DEVICE_DONE + " integer"
			+ ");";

	public DatabaseSchedule(Context context) {
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
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
		onCreate(database);
	}
}