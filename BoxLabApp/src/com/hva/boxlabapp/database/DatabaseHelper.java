//package com.hva.boxlabapp.database;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//
//	public static final String TAG = DatabaseHelper.class.getName();
//
//	private static final String DATABASE_NAME = "devices.db";
//	private static final int DATABASE_VERSION = 1;
//
//	public static final String TABLE_DEVICE = "device";
//	public static final String COLUMN_DEVICE_ID = "_id";
//	public static final String COLUMN_DEVICE_NAME = "name";
//	public static final String COLUMN_DEVICE_TYPE = "type";
//
//	public static final String TABLE_DEVICE_TYPE = "devicetype";
//	public static final String COLUMN_DEVICE_TYPE_ID = "_id";
//	public static final String COLUMN_DEVICE_TYPE_DESCRIPTION = "description";
//
//	private static final String DATABASE_CREATE_DEVICE = "create table "
//			+ TABLE_DEVICE + "(" + COLUMN_ID
//			+ " integer primary key autoincrement, " + COLUMN_DEVICE_COMMENT
//			+ " text not null);";
//
//	private static final String DATABASE_CREATE_DEVICE_TYPE = "create table "
//			+ TABLE_DEVICE + "(" + COLUMN_ID
//			+ " integer primary key autoincrement, " + COLUMN_DEVICE_COMMENT
//			+ " text not null);";
//
//	private static final String[] DATABASE_CREATE = { DATABASE_CREATE_DEVICE,
//			DATABASE_CREATE_DEVICE_TYPE };
//
//	public DatabaseHelper(Context context) {
//		super(context, DATABASE_NAME, null, DATABASE_VERSION);
//	}
//
//	@Override
//	public void onCreate(SQLiteDatabase database) {
//		for (String sql : DATABASE_CREATE) {
//			database.execSQL(sql);
//		}
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
//				+ newVersion + ", which will destroy all old data");
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE_TYPE);
//		onCreate(db);
//	}
//}