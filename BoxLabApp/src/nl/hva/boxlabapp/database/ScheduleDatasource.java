package nl.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nl.boxlab.model.ExerciseEntry;
import nl.hva.boxlabapp.entities.ExerciseEntryItem;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ScheduleDatasource {

	public static final String TAG = ScheduleDatasource.class.getName();

	private SQLiteDatabase database;
	private Database dbhelper;

	public ScheduleDatasource(Context context) {
		dbhelper = new Database(context);
	}

	private void open() throws SQLException {
		database = dbhelper.getReadableDatabase();
	}

	private void close() {
		database = null;
		dbhelper.close();
	}

	public ExerciseEntryItem create(ExerciseEntry original) {
		
		ExerciseEntryItem entry = new ExerciseEntryItem(original);
		
		try {
			this.open();
			ContentValues content = new ContentValues();
			content.put(Database.ENTITY_ID, entry.getIdentification());
			content.put(Database.ENTITY_CREATION_DATE, entry.getCreated().getTime());
			content.put(Database.ENTITY_UPDATE_DATE, new Date().getTime());
			content.put(Database.SCHEDULE_DATE, entry.getDate().getTime());
			content.put(Database.SCHEDULE_EXID, entry.getExerciseId());
			content.put(Database.SCHEDULE_REPS, entry.getReps());
			content.put(Database.SCHEDULE_DONE, entry.isDone());
			content.put(Database.SCHEDULE_NOTE,entry.getNote());

			Log.d(TAG, content.toString());
			long id = database.insert(Database.TABLE_SCHEDULE, null,
					content);
			if (id > 0) {
				entry.set_id(((int) id));
				Log.d(TAG, "Entry added to the database.");
				Log.d(TAG, entry.toString() + " date: " + entry.getDate());
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
	
	public boolean update(ExerciseEntryItem entry) {
		int rows = 0;
		long id = entry.get_id();
		String table = Database.TABLE_DEVICES;
		String where = Database.LOCAL_ID + " = " + id;		
		
		try {
			this.open();
			ContentValues content = new ContentValues();
			content.put(Database.ENTITY_ID, entry.getIdentification());
			content.put(Database.ENTITY_CREATION_DATE, entry.getCreated().getTime());
			content.put(Database.ENTITY_UPDATE_DATE, new Date().getTime());
			content.put(Database.SCHEDULE_DATE, entry.getDate().getTime());
			content.put(Database.SCHEDULE_EXID, entry.getExerciseId());
			content.put(Database.SCHEDULE_REPS, entry.getReps());
			content.put(Database.SCHEDULE_DONE, entry.isDone());
			content.put(Database.SCHEDULE_NOTE,entry.getNote());
			
			rows = database.update(table, content, where, null);
			Log.d(TAG, "Entry with id " + id + " was updated.");
		} catch (SQLException e) {
			Log.e(TAG, "Failed to update entry with id " + id, e);
		} finally {
			close();
		}
		return rows > 0;
	}
	
	public boolean remove(ExerciseEntryItem entry){
		long id = entry.get_id();
		int rows = 0;
		String table = Database.TABLE_DEVICES;
		String where = Database.LOCAL_ID + " = " + id;
		
		try {
			open();
			rows = database.delete(table, where, null);
			Log.d(TAG, "device with id " + id + " was deleted");
		} catch (SQLException e) {
			Log.e(TAG, "Failed to delete device with id " + id, e);
		} finally {
			close();
		}
		return rows > 0;
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
				+ Database.LOCAL_ID + ", "
				+ Database.ENTITY_ID + ", "
				+ Database.ENTITY_CREATION_DATE + ", "
				+ Database.ENTITY_UPDATE_DATE + ", "
				+ Database.SCHEDULE_DATE + ", " 
				+ Database.SCHEDULE_EXID + ", "
				+ Database.SCHEDULE_REPS + ", "
				+ Database.SCHEDULE_DONE + ", " 
				+ Database.SCHEDULE_NOTE + " FROM "
				+ Database.TABLE_SCHEDULE + " WHERE "
				+ Database.SCHEDULE_DATE + " <= "
				+ max.getTimeInMillis() + " AND "
				+ Database.SCHEDULE_DATE + " >= "
				+ min.getTimeInMillis() + ";";
		try {
			this.open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				ExerciseEntryItem schedule = cursorToExerciseEntry(cursor);
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

	
	public ExerciseEntryItem cursorToExerciseEntry(Cursor cursor){
		int localId = cursor.getInt(0);
		String entityId = cursor.getString(1);
		Date entityCreationDate = new Date(cursor.getLong(2));
		Date entityUpdateDate = new Date(cursor.getLong(3));
		Date date = new Date(cursor.getLong(4));
		int exerciseId = cursor.getInt(5);
		String reps = cursor.getString(6);
		boolean done = cursor.getInt(7) > 0 ? true : false;
		String note = cursor.getString(8);
		
		ExerciseEntryItem entry = new ExerciseEntryItem(entityId, entityCreationDate, date, exerciseId, reps, note, done);
		entry.set_id(localId);
		entry.setUpdated(entityUpdateDate);
		
		return entry;
	}
}
