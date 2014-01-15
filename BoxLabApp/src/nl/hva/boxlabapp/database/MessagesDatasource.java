package nl.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.boxlab.model.Message;
import nl.hva.boxlabapp.entities.MessageItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessagesDatasource {

	private final static String TAG = MessagesDatasource.class.getName();
	
	public enum Author {
		PATIENT,
		THERAPIST;
	}
	
	private SQLiteDatabase database;
	private Database dbHelper;
	
	public MessagesDatasource(Context context){
		dbHelper = new Database(context);
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
			values.put(Database.ENTITY_ID, message.getId());
			values.put(Database.ENTITY_CREATION_DATE, message.getCreated().getTime());
			values.put(Database.ENTITY_UPDATE_DATE, message.getUpdated().getTime());
			values.put(Database.MESSAGES_MSG, message.getMessage());
			
			if (message.isFromPatient()) {
				values.put(Database.MESSAGES_AUTHOR, Author.PATIENT.ordinal());
			} else {
				values.put(Database.MESSAGES_AUTHOR, Author.THERAPIST.ordinal());
			}
			
			long id = database.insert(Database.TABLE_MESSAGES, null, values);
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
		String where = Database.LOCAL_ID + " = " + id;
	
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(Database.ENTITY_ID, message.getId());
			values.put(Database.ENTITY_CREATION_DATE, message.getCreated().getTime());
			values.put(Database.ENTITY_UPDATE_DATE, message.getUpdated().getTime());
			values.put(Database.MESSAGES_MSG, message.getMessage());
			
			if (message.isFromPatient()) {
				values.put(Database.MESSAGES_AUTHOR, Author.PATIENT.ordinal());
			} else {
				values.put(Database.MESSAGES_AUTHOR, Author.THERAPIST.ordinal());
			}
			
			int amount = database.update(Database.TABLE_MESSAGES, values, where, null);
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
		String where = Database.LOCAL_ID + " = " + id;
		
		try {
			open();
			int rows = database.delete(Database.TABLE_MESSAGES, where, null);
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
				+ Database.LOCAL_ID + ", "
				+ Database.ENTITY_ID + ", "
				+ Database.ENTITY_CREATION_DATE + ", "
				+ Database.ENTITY_UPDATE_DATE + ", "
				+ Database.MESSAGES_AUTHOR + ", "
				+ Database.MESSAGES_MSG + " FROM "
				+ Database.TABLE_MESSAGES + ";";
		
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
				+ Database.LOCAL_ID + ", "
				+ Database.ENTITY_ID + ", "
				+ Database.ENTITY_CREATION_DATE + ", "
				+ Database.ENTITY_UPDATE_DATE + ", "
				+ Database.MESSAGES_AUTHOR + ", "
				+ Database.MESSAGES_MSG + " FROM "
				+ Database.TABLE_MESSAGES + ";";
		
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
		String ident = cursor.getString(1);
		Date creationDate = new Date(cursor.getLong(3));
		Date updateDate = new Date(cursor.getLong(4));
		Author author = Author.values()[cursor.getInt(4)];
		String msg = cursor.getString(5);
		
		MessageItem message = new MessageItem();
		message.set_id(id);
		message.setId(ident);
		message.setCreated(creationDate);
		message.setUpdated(updateDate);
		message.setMessage(msg);
		
		switch (author) {
		case PATIENT:message.setFromPatient(true);
			break;
		case THERAPIST:message.setFromPatient(false);
			break;
		}
		
		return message;
	}
	
//	private class Database extends SQLiteOpenHelper {
//		
//		public static final String TABLE_FEEDBACK = "feedback";
//		public static final String ID = "_id";
//		public static final String DATE = "date";
//		public static final String AUTHOR = "author";
//		public static final String MSG = "message";
//		
//		private static final String DATABASE_NAME = "feedback.db";
//		private static final int DATABASE_VERSION = 1;
//		
//		public static final String DATABASE_CREATE = "create table "
//				+ TABLE_FEEDBACK + "(" 
//				+ ID + " integer primary key autoincrement, "
//				+ DATE + " integer not null, "
//				+ AUTHOR + " integer not null, "
//				+ MSG + " text not null"
//				+ ");";
//		
//		public Database(Context context) {
//			super(context, DATABASE_NAME, null, DATABASE_VERSION);
//		}
//
//		@Override
//		public void onCreate(SQLiteDatabase db) {
//			db.execSQL(DATABASE_CREATE);
//			Log.e(TAG, "Database created");
//		}
//
//		@Override
//		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
//					+ newVersion + ", which will destroy all old data");
//			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
//			onCreate(db);			
//		}
//		
//	}
}
