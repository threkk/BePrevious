package com.hva.boxlabapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ScheduleDatabase extends SQLiteOpenHelper {

	public static final String TAG = ScheduleDatabase.class.getName();

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

	private static final String DATABASE_NAME = "schedule3.db"; // CHANGE THIS
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SCHEDULE + "(" 
			+ COLUMN_SCHEDULE_ID 	+ " integer primary key autoincrement, " 
			+ COLUMN_SCHEDULE_DATE + " integer not null, "
			+ COLUMN_SCHEDULE_EXID + " integer not null, "
			+ COLUMN_SCHEDULE_REPS + " text not null, "
			+ COLUMN_SCHEDULE_DONE + " integer not null, "
			+ COLUMN_SCHEDULE_NOTES + " text, "
			+ COLUMN_SCHEDULE_WEIGHT + " text, "
			+ COLUMN_SCHEDULE_SUPP + " text, "
			+ COLUMN_SCHEDULE_POS + " text "
			+ ");";

	public ScheduleDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		
		// ONLY FOR TESTING
		// 18th december
		String sql1 = "INSERT INTO schedule(date, exercise_id, set_repetitions, is_done) " +
				"VALUES (1387321200000, 1, '10 10 10', 0)";
		String sql2 = "INSERT INTO schedule(date, exercise_id, set_repetitions, is_done) " +
				"VALUES (1387321200000, 2, '10 20 30', 0)";
		String sql3 = "INSERT INTO schedule(date, exercise_id, set_repetitions, is_done) " +
				"VALUES (1387321200000, 3, '20 10', 1)";
		database.execSQL(sql1);
		database.execSQL(sql2);
		database.execSQL(sql3);
		Log.e(TAG, "Data added");
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