package com.hva.boxlabapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseSchedule extends SQLiteOpenHelper {

	public static final String TAG = DatabaseSchedule.class.getName();

	public static final String TABLE_SCHEDULE = "schedule";
	public static final String COLUMN_SCHEDULE_ID = "_id"; // key
	public static final String COLUMN_SCHEDULE_DATE = "date"; // long
	public static final String COLUMN_SCHEDULE_EXID = "exercise_id"; // in other db.
	public static final String COLUMN_SCHEDULE_REPS = "set_repetitions"; // format 10 10 10 -> 3 sets of 10
	public static final String COLUMN_SCHEDULE_DONE = "is_done"; // boolean
	public static final String COLUMN_SCHEDULE_NOTES = "notes"; // optional text
	public static final String COLUMN_SCHEDULE_WEIGHT = "weight"; // optional text
	public static final String COLUMN_SCHEDULE_SUPP = "support"; // optional text 
	public static final String COLUMN_SCHEDULE_POS = "position"; // optional text

	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SCHEDULE + "(" 
			+ COLUMN_SCHEDULE_ID 	+ " integer primary key autoincrement, " 
			+ COLUMN_SCHEDULE_DATE + " integer not null, "
			+ COLUMN_SCHEDULE_EXID + " integer not null, "
			+ COLUMN_SCHEDULE_REPS + " text not null, "
			+ COLUMN_SCHEDULE_DONE + " integer not null, "
			+ COLUMN_SCHEDULE_NOTES + "text, "
			+ COLUMN_SCHEDULE_WEIGHT + "text, "
			+ COLUMN_SCHEDULE_SUPP + "text, "
			+ COLUMN_SCHEDULE_POS + "text "
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