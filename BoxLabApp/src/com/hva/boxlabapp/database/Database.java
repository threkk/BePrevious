package com.hva.boxlabapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_SCHEDULE = "schedule";
	public static final String COLUMN_SCHEDULE_ID = "_id";
	public static final String COLUMN_SCHEDULE_RID = "_remoteId";
	public static final String COLUMN_SCHEDULE_DATE = "date";
	public static final String COLUMN_SCHEDULE_EXID = "exercise_id";
	public static final String COLUMN_SCHEDULE_REPS = "set_repetitions";
	public static final String COLUMN_SCHEDULE_DONE = "is_done";
	public static final String COLUMN_SCHEDULE_NOTE = "notes";
	
	
	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
