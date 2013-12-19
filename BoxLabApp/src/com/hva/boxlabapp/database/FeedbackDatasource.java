package com.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hva.boxlabapp.entities.MessageItem;
import com.hva.boxlabapp.entities.client.Message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FeedbackDatasource {

	private final static String TAG = FeedbackDatasource.class.getName();
	
	public enum Author {
		PATIENT,
		THERAPIST;
	}
	
	private SQLiteDatabase database;
	private FeedbackDatabase dbHelper;
	
	public FeedbackDatasource(Context context){
		dbHelper = new FeedbackDatabase(context);
	}
	
	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		database = null;
		dbHelper.close();
	}
	
	public MessageItem create(Message message){
		
		MessageItem msg = null;
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(FeedbackDatabase.COLUMN_DATE, message.getDate().getTime());
			values.put(FeedbackDatabase.COLUMN_MSG, message.getMessage());
			
			if (message.isFromPatient()) {
				values.put(FeedbackDatabase.COLUMN_AUTHOR, Author.PATIENT.ordinal());
			} else {
				values.put(FeedbackDatabase.COLUMN_AUTHOR, Author.THERAPIST.ordinal());
			}
			
			long id = database.insert(FeedbackDatabase.TABLE_FEEDBACK, null, values);
			msg = new MessageItem(message);
			msg.set_id((int) id);
			Log.d(TAG, "Message with id " + id + " was inserted.");
		} catch (SQLException e){
			Log.e(TAG, "Failed to add message " + message.toString());
		} finally {
			close();
		}
		return msg;
	}
	
	public boolean update(MessageItem message) {
		long id = message.get_id();
		boolean update = false;
		String where = FeedbackDatabase.COLUMN_ID + " = " + id;
	
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(FeedbackDatabase.COLUMN_DATE, message.getDate().getTime());
			values.put(FeedbackDatabase.COLUMN_MSG, message.getMessage());
			
			if (message.isFromPatient()) {
				values.put(FeedbackDatabase.COLUMN_AUTHOR, Author.PATIENT.ordinal());
			} else {
				values.put(FeedbackDatabase.COLUMN_AUTHOR, Author.THERAPIST.ordinal());
			}
			
			int amount = database.update(FeedbackDatabase.TABLE_FEEDBACK, values, where, null);
			update = amount > 0 ? true : false;
		} catch (SQLException oops) {
			Log.e(TAG, "Failed to update the message with id " + id);
		} finally {
			close();
		}
		
		return update;
	}
	
	public boolean delete(MessageItem message) {
		long id = message.get_id();
		boolean removed = false;
		String where = FeedbackDatabase.COLUMN_ID + " = " + id;
		
		try {
			open();
			int rows = database.delete(FeedbackDatabase.TABLE_FEEDBACK, where, null);
			removed = rows > 0 ? true : false;
		} catch(SQLException oops) {
			Log.e(TAG, "Error trying to delete message with id " + id);
		} finally {
			close();
		}
		return removed;
	}
	
	public List<MessageItem> getMessages(){
		List<MessageItem> messages = new ArrayList<MessageItem>();
		Cursor cursor = null;
		
		String sql = "SELECT "
				+ FeedbackDatabase.COLUMN_ID + ", "
				+ FeedbackDatabase.COLUMN_DATE + ", "
				+ FeedbackDatabase.COLUMN_AUTHOR + ", "
				+ FeedbackDatabase.COLUMN_MSG + " FROM "
				+ FeedbackDatabase.DATABASE_NAME + ";";
		
		try {
			open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();
			
			while(!cursor.isAfterLast()){
				MessageItem item = cursorToMessageItem(cursor);
				messages.add(item);
				cursor.moveToNext();
			}
		} catch (SQLException oops) {
			Log.e(TAG,"Failed to retrieve the messages.");
		} finally {
			close();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return messages;
	}
	
	public List<MessageItem> getMessages(Author author){
		List<MessageItem> messages = new ArrayList<MessageItem>();
		Cursor cursor = null;
		
		String sql = "SELECT "
				+ FeedbackDatabase.COLUMN_ID + ", "
				+ FeedbackDatabase.COLUMN_DATE + ", "
				+ FeedbackDatabase.COLUMN_AUTHOR + ", "
				+ FeedbackDatabase.COLUMN_MSG + " FROM "
				+ FeedbackDatabase.DATABASE_NAME + ";";
		
		try {
			open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();
			
			while(!cursor.isAfterLast()){
				MessageItem item = cursorToMessageItem(cursor);
				if((item.isFromPatient() && author.equals(Author.PATIENT)) 
						||(!item.isFromPatient() && author.equals(Author.THERAPIST)) ) {
					messages.add(item);
				}
				cursor.moveToNext();
			}
		} catch (SQLException oops) {
			Log.e(TAG,"Failed to retrieve the messages.");
		} finally {
			close();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return messages;
	}
	
	public MessageItem cursorToMessageItem(Cursor cursor){
		int id = cursor.getInt(0);
		Date date = new Date(cursor.getLong(1));
		Author author = Author.values()[cursor.getInt(2)];
		String msg = cursor.getString(3);
		
		MessageItem message = new MessageItem();
		message.set_id(id);
		message.setDate(date);
		message.setMessage(msg);
		
		switch (author) {
		case PATIENT:message.setFromPatient(true);
			break;
		case THERAPIST:message.setFromPatient(false);
			break;
		}
		
		return message;
	}
	
	private class FeedbackDatabase extends SQLiteOpenHelper {
		
		public static final String TABLE_FEEDBACK = "feedback";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_DATE = "date";
		public static final String COLUMN_AUTHOR = "author";
		public static final String COLUMN_MSG = "message";
		
		private static final String DATABASE_NAME = "database.db";
		private static final int DATABASE_VERSION = 1;
		
		public static final String DATABASE_CREATE = "create table "
				+ TABLE_FEEDBACK + " (" 
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_DATE + " integer not null, "
				+ COLUMN_AUTHOR + " integer not null, "
				+ COLUMN_MSG + " string not null"
				+ ");";
		
		public FeedbackDatabase(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			Log.e(TAG, "Database created");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
			onCreate(db);			
		}
		
	}
}
