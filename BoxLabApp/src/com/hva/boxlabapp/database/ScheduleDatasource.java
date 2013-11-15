package com.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hva.boxlabapp.database.entities.Schedule;
import com.hva.boxlabapp.database.entities.ScheduleInsert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ScheduleDatasource {

	public static final String TAG = ScheduleDatasource.class.getName();
	
	private SQLiteDatabase database;
	private ScheduleDatabase dbhelper;
	
	public ScheduleDatasource(Context context){
		dbhelper = new ScheduleDatabase(context);
	}
		
	private void open() throws SQLException {
		database = dbhelper.getReadableDatabase();
	}
	
	private void close(){
		database = null;
		dbhelper.close();
	}
	
	public List<Schedule> getExercisesByDate(Date date){
		List<Schedule> calendar = new ArrayList<Schedule>();
    	Cursor cursor = null;
    	String sql = " SELECT "
				//+ ScheduleDatabase.COLUMN_SCHEDULE_DATE + ", " we already have the date
				+ ScheduleDatabase.COLUMN_SCHEDULE_EXID + ", "
				+ ScheduleDatabase.COLUMN_SCHEDULE_REPS + ", "
				+ ScheduleDatabase.COLUMN_SCHEDULE_NOTES + " "
				+ " FROM " + ScheduleDatabase.TABLE_SCHEDULE
				+ " WHERE "
				+ ScheduleDatabase.COLUMN_SCHEDULE_DATE + " = " + date.getTime() + ";";
    	try {
			this.open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				//Schedule schedule = new Schedule(cursor.getLong(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
				Schedule schedule = new Schedule(date, cursor.getInt(0), cursor.getString(1), cursor.getString(2));
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
	
	public ScheduleInsert create(ScheduleInsert entry){
		
		try{
			this.open();
			ContentValues content = new ContentValues();
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_DATE, entry.getDate());
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_EXID, entry.getExercise());
			content.put(ScheduleDatabase.COLUMN_SCHEDULE_REPS, entry.getReps());
			// Depending on if there is more things to add to the database, this 
			// and the ScheduleEntry classes must be modified.
			
			database.insert(ScheduleDatabase.TABLE_SCHEDULE, null, content);
		} catch (SQLException oops){
			Log.e(TAG, "Error inserting in the database");
		} finally {
			this.close();
		}
		
		return entry;
	}
	
}
