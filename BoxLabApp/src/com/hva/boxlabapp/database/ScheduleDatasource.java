package com.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.hva.boxlabapp.entities.ExerciseEntryItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ScheduleDatasource {

	public static final String TAG = ScheduleDatasource.class.getName();

	private SQLiteDatabase database;
	private ScheduleDatabase dbhelper;

	public ScheduleDatasource(Context context) {
		dbhelper = new ScheduleDatabase(context);
	}

	private void open() throws SQLException {
		database = dbhelper.getReadableDatabase();
	}

	private void close() {
		database = null;
		dbhelper.close();
	}

	public List<ExerciseEntryItem> getExercisesByDate(Date date) {
		List<ExerciseEntryItem> calendar = new ArrayList<ExerciseEntryItem>();
		Cursor cursor = null;

		Calendar min = Calendar.getInstance();
		min.setTime(date);
		min.set(Calendar.HOUR_OF_DAY, 0);
		min.set(Calendar.MINUTE, 0);
		min.set(Calendar.SECOND, 0);
		min.set(Calendar.MILLISECOND, 0);

		Calendar max = Calendar.getInstance();
		max.setTime(date);
		max.set(Calendar.HOUR_OF_DAY, 23);
		max.set(Calendar.MINUTE, 59);
		max.set(Calendar.SECOND, 59);
		max.set(Calendar.MILLISECOND, 999);

		String sql = " SELECT "
				+ ScheduleDatabase.COLUMN_SCHEDULE_ID + ", "
				+ ScheduleDatabase.COLUMN_SCHEDULE_RID + ", "
				+ ScheduleDatabase.COLUMN_SCHEDULE_DATE + ", " 
				+ ScheduleDatabase.COLUMN_SCHEDULE_EXID + ", "
				+ ScheduleDatabase.COLUMN_SCHEDULE_REPS + ", "
				+ ScheduleDatabase.COLUMN_SCHEDULE_NOTES + ", " 
				+ ScheduleDatabase.COLUMN_SCHEDULE_DONE + " FROM "
				+ ScheduleDatabase.TABLE_SCHEDULE + " WHERE "
				+ ScheduleDatabase.COLUMN_SCHEDULE_DATE + " <= "
				+ max.getTimeInMillis() + " AND "
				+ ScheduleDatabase.COLUMN_SCHEDULE_DATE + " >= "
				+ min.getTimeInMillis() + ";";
		try {
			this.open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				boolean done = cursor.getInt(6) == 1 ? true : false;
				ExerciseEntryItem schedule = new ExerciseEntryItem(cursor.getString(1), new Date(cursor.getInt(2)), cursor.getInt(3), cursor.getString(4), cursor.getString(5), done);
				schedule.set_id(cursor.getInt(0));
				calendar.add(schedule);
				cursor.moveToNext();
			}
		} catch (SQLException e) {
			Log.e(TAG, "Failed to retrieve schedule", e);
		} finally {
			this.close();
			if (cursor != null) {
				cursor.close();
			}
		}

		return calendar;
	}

	public ExerciseEntryItem create(ExerciseEntryItem entry) {
		long row = -1;
		
		try {
			this.open();
			ContentValues content = new ContentValues();
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_RID, 
					entry.getIdentification());
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_DATE,
					entry.getDate().getTime());
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_EXID,
					entry.getExerciseId());
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_REPS, 
					entry.getReps());
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_DONE, 
					entry.isDone());
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_NOTES,
					entry.getNote());

			Log.e(TAG, content.toString());
			row = database.insert(ScheduleDatabase.TABLE_SCHEDULE, null,
					content);
			if (row > 0) {
				entry.set_id((int) row);
				Log.e(TAG, "Entry added to the database.");
				Log.e(TAG, entry.toString() + " fecha: " + entry.getDate());
			} else {
				Log.e(TAG, "Error adding the entry to the database.");
				throw new SQLException("Error updating the entry.");
			}
		} catch (SQLException oops) {
			Log.e(TAG, "Error inserting in the database");
		} finally {
			this.close();
		}

		return entry;
	}

	private class ScheduleDatabase extends SQLiteOpenHelper {

		public static final String TABLE_SCHEDULE = "schedule";
		public static final String COLUMN_SCHEDULE_ID = "_id"; // key
		public static final String COLUMN_SCHEDULE_RID = "_remoteId"; // remote key
		public static final String COLUMN_SCHEDULE_DATE = "date"; // long
		public static final String COLUMN_SCHEDULE_EXID = "exercise_id";
		public static final String COLUMN_SCHEDULE_REPS = "set_repetitions";
		public static final String COLUMN_SCHEDULE_DONE = "is_done"; // boolean
		public static final String COLUMN_SCHEDULE_NOTES = "notes"; // optional

		private static final String DATABASE_NAME = "schedule.db";
		private static final int DATABASE_VERSION = 1;

		private static final String DATABASE_CREATE = "create table "
				+ TABLE_SCHEDULE + "(" 
				+ COLUMN_SCHEDULE_ID + " integer primary key autoincrement, " 
				+ COLUMN_SCHEDULE_RID + " text not null, "
				+ COLUMN_SCHEDULE_DATE + " integer not null, " 
				+ COLUMN_SCHEDULE_EXID + " integer not null, " 
				+ COLUMN_SCHEDULE_REPS + " text not null, " 
				+ COLUMN_SCHEDULE_DONE + " integer not null, " 
				+ COLUMN_SCHEDULE_NOTES + " text "
				+ ");";

		public ScheduleDatabase(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE);
			Log.e(TAG, "Database created");
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
}
