package nl.hva.boxlabapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper{
	
	private static final String TAG = Database.class.getName();
	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 1;

	// Tables
	public static final String TABLE_SCHEDULE = "schedule";
	public static final String TABLE_MESSAGES = "messages";
	public static final String TABLE_DEVICES = "devices";

	// Local
	public static final String LOCAL_ID = "_id";
	// Entity
	public static final String ENTITY_ID = "_rid";
	public static final String ENTITY_CREATION_DATE = "_cdate";
	public static final String ENTITY_UPDATE_DATE = "_udate";
	// Schedule
	public static final String SCHEDULE_DATE = "date";
	public static final String SCHEDULE_EXID = "exercise";
	public static final String SCHEDULE_REPS = "repetitions";
	public static final String SCHEDULE_DONE = "is_done";
	public static final String SCHEDULE_NOTE = "notes";
	// Message
	public static final String MESSAGES_AUTHOR = "author";
	public static final String MESSAGES_MSG = "message";
	// Devices
	public static final String DEVICE_NAME = "name";
	public static final String DEVICE_POSITION = "position";
	public static final String DEVICE_TYPE = "type";
	public static final String DEVICE_MAC = "mac";
	
	// Creation
	private static final String CREATE_SCHEDULE = "create table "
			+ TABLE_SCHEDULE + " ( " 
			+ LOCAL_ID + " integer primary key autoincrement, "
			+ ENTITY_ID + " text, "
			+ ENTITY_CREATION_DATE + " integer not null, "
			+ ENTITY_UPDATE_DATE + " integer, "
			+ SCHEDULE_DATE + " integer not null, "
			+ SCHEDULE_EXID + " integer not null, "
			+ SCHEDULE_REPS + " text not null, "
			+ SCHEDULE_DONE + " integer, "
			+ SCHEDULE_NOTE + " text "
			+ ");";
	
	private static final String CREATE_MESSAGES = "create table "
			+ TABLE_MESSAGES + " ( " 
			+ LOCAL_ID + " integer primary key autoincrement, "
			+ ENTITY_ID + " text, "
			+ ENTITY_CREATION_DATE + " integer not null, "
			+ ENTITY_UPDATE_DATE + " integer, "
			+ MESSAGES_MSG + " text not null, "
			+ MESSAGES_AUTHOR + " integer not null "
			+ ");";
	
	private static final String CREATE_DEVICES = "create table "
			+ TABLE_DEVICES + "(" 
			+ LOCAL_ID + " integer primary key autoincrement, "
			+ DEVICE_NAME + " text not null, " 
			+ DEVICE_TYPE + " integer not null, "
			+ DEVICE_POSITION + " integer not null, "
			+ DEVICE_MAC + " text not null"
			+ ");";
	
	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(TAG, "Database created");

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SCHEDULE);
		Log.d(TAG, "Schedule created.");
		db.execSQL(CREATE_MESSAGES);
		Log.d(TAG, "Messages created.");
		db.execSQL(CREATE_DEVICES);
		Log.d(TAG, "Devices created.");	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
		onCreate(db);		
	}

}
