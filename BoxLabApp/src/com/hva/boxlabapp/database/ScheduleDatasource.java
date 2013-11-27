package com.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.hva.boxlabapp.database.entities.Schedule;

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

	public List<Schedule> getExercisesByDate(Date date) {
		List<Schedule> calendar = new ArrayList<Schedule>();
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
				// + ScheduleDatabase.COLUMN_SCHEDULE_DATE + ", " we already
				// have the date
				+ ScheduleDatabase.COLUMN_SCHEDULE_EXID + ", "
				+ ScheduleDatabase.COLUMN_SCHEDULE_REPS + ", "
				+ ScheduleDatabase.COLUMN_SCHEDULE_NOTES + " " + " FROM "
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
				// Schedule schedule = new Schedule(cursor.getLong(0),
				// cursor.getInt(1), cursor.getString(2), cursor.getString(3));
				Schedule schedule = new Schedule(date, cursor.getInt(0),
						cursor.getString(1), cursor.getString(2));
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

	public boolean create(Schedule entry) {
		long ret = -1;
		try {
			this.open();
			ContentValues content = new ContentValues();
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_DATE,
					entry.getMillis());
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_EXID,
					entry.getExercise());
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_REPS, entry.getReps());
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_DONE, 0);
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_NOTES,
					entry.getNotes());

			Log.e(TAG, content.toString());
			ret = database.insert(ScheduleDatabase.TABLE_SCHEDULE, null,
					content);
			if (ret > 0) {
				Log.e(TAG, "Entry added to the database.");
				Log.e(TAG, entry.toString() + " fecha: " + entry.getDate());
			} else {
				Log.e(TAG, "Error adding the entry to the database.");
			}
		} catch (SQLException oops) {
			Log.e(TAG, "Error inserting in the database");
		} finally {
			this.close();
		}

		return ret > 0;
	}

//	TODO: Thinking about how to implement
//	public boolean update (Schedule entry, boolean done) {
//		
//	}
//	
	private class ScheduleDatabase extends SQLiteOpenHelper {

		public static final String TABLE_SCHEDULE = "schedule";
		public static final String COLUMN_SCHEDULE_ID = "_id"; // key
		public static final String COLUMN_SCHEDULE_DATE = "date"; // long
		public static final String COLUMN_SCHEDULE_EXID = "exercise_id";
		public static final String COLUMN_SCHEDULE_REPS = "set_repetitions";
		// format 10 10 10 -> 3 sets of 10
		public static final String COLUMN_SCHEDULE_DONE = "is_done"; // boolean
		public static final String COLUMN_SCHEDULE_NOTES = "notes"; // optional

		private static final String DATABASE_NAME = "schedule.db";
		private static final int DATABASE_VERSION = 1;

		private static final String DATABASE_CREATE = "create table "
				+ TABLE_SCHEDULE + "(" + COLUMN_SCHEDULE_ID
				+ " integer primary key autoincrement, " + COLUMN_SCHEDULE_DATE
				+ " integer not null, " + COLUMN_SCHEDULE_EXID
				+ " integer not null, " + COLUMN_SCHEDULE_REPS
				+ " text not null, " + COLUMN_SCHEDULE_DONE
				+ " integer not null, " + COLUMN_SCHEDULE_NOTES + " text "
				+ ");";

		public ScheduleDatabase(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE);

			// ONLY FOR TESTING
			// 18th december
			String sql1 = "INSERT INTO schedule(date, exercise_id, set_repetitions, is_done, notes) "
					+ "VALUES (1387321200000, 1, '10 10 10', 0, 'The patient must use something to support"
					+ "himself or herself during the exercise. A break must be done between sets.')";
			String sql2 = "INSERT INTO schedule(date, exercise_id, set_repetitions, is_done) "
					+ "VALUES (1387321200000, 2, '10 20 30', 0)";
			String sql3 = "INSERT INTO schedule(date, exercise_id, set_repetitions, is_done, notes) "
					+ "VALUES (1387321200000, 3, '20 10', 1, 'Only two sets of exercises must be done,"
					+ "but with a high intensity')";
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
}
