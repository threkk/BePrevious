package nl.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.boxlab.model.Entity;
import nl.boxlab.model.Message;
import nl.hva.boxlabapp.entities.MessageItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class manages the messages stored in the database. As the
 * synchronization with the master server only happens one time per day, and the
 * synchronization with the BoxLab is done via bluetooth, so all messages are
 * stored in the database.
 * 
 * @author Alberto Mtnz de Murga
 * @version 1
 * @see Message
 * @see MessageItem
 * @see Database
 * @see Entity
 */
public class MessagesDatasource {

	private final static String TAG = MessagesDatasource.class.getName();

	/**
	 * To distinguish between the patient and therapist messages.
	 * 
	 * @author Alberto Mtnz de Murga
	 * 
	 */
	public enum Author {
		PATIENT, THERAPIST;
	}

	private SQLiteDatabase database;
	private Database dbHelper;

	public MessagesDatasource(Context context) {
		dbHelper = new Database(context);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		database = null;
		dbHelper.close();
	}

	/**
	 * Creates an entry in the database with the original message. The return is
	 * the same object but with the id from the database.
	 * 
	 * @param message
	 *            Original message.
	 * @return A <code>MessageItem</code> with the same information as the
	 *         original message plus the id from the database.
	 */
	public MessageItem create(Message message) {

		MessageItem msg = null;
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(Database.ENTITY_ID, message.getId());
			values.put(Database.ENTITY_CREATION_DATE, message.getCreated()
					.getTime());
			values.put(Database.ENTITY_UPDATE_DATE, message.getUpdated()
					.getTime());
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
		} catch (SQLException e) {
			Log.e(TAG, "Failed to add message " + message.toString());
		} finally {
			close();
		}
		return msg;
	}

	/**
	 * Updates the entry with the same id with the actual values of the message.
	 * 
	 * @param message
	 *            Message to update.
	 * @return <code>true</code> if updated.
	 */
	public boolean update(MessageItem message) {
		long id = message.get_id();
		boolean update = false;
		String where = Database.LOCAL_ID + " = " + id;

		if (id == 0) {
			Log.i(TAG, "Message not found.");
			return false;
		}

		try {
			open();
			ContentValues values = new ContentValues();
			values.put(Database.ENTITY_ID, message.getId());
			values.put(Database.ENTITY_CREATION_DATE, message.getCreated()
					.getTime());
			values.put(Database.ENTITY_UPDATE_DATE, message.getUpdated()
					.getTime());
			values.put(Database.MESSAGES_MSG, message.getMessage());

			if (message.isFromPatient()) {
				values.put(Database.MESSAGES_AUTHOR, Author.PATIENT.ordinal());
			} else {
				values.put(Database.MESSAGES_AUTHOR, Author.THERAPIST.ordinal());
			}

			int amount = database.update(Database.TABLE_MESSAGES, values,
					where, null);
			update = amount > 0 ? true : false;
		} catch (SQLException oops) {
			Log.e(TAG, "Failed to update the message with id " + id);
		} finally {
			close();
		}

		return update;
	}

	/**
	 * Deletes the entry with same id from the database.
	 * 
	 * @param message
	 *            The message to delete.
	 * @return <code>true</code> if deleted.
	 */
	public boolean delete(MessageItem message) {
		long id = message.get_id();
		boolean removed = false;
		String where = Database.LOCAL_ID + " = " + id;

		if (id == 0) {
			Log.i(TAG, "Message not found.");
			return false;
		}

		try {
			open();
			int rows = database.delete(Database.TABLE_MESSAGES, where, null);
			removed = rows > 0 ? true : false;
		} catch (SQLException oops) {
			Log.e(TAG, "Error trying to delete message with id " + id);
		} finally {
			close();
		}
		return removed;
	}

	public List<MessageItem> getMessages() {
		List<MessageItem> messages = new ArrayList<MessageItem>();
		Cursor cursor = null;

		String sql = "SELECT " + Database.LOCAL_ID + ", " + Database.ENTITY_ID
				+ ", " + Database.ENTITY_CREATION_DATE + ", "
				+ Database.ENTITY_UPDATE_DATE + ", " + Database.MESSAGES_AUTHOR
				+ ", " + Database.MESSAGES_MSG + " FROM "
				+ Database.TABLE_MESSAGES + ";";

		try {
			open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				MessageItem item = cursorToMessageItem(cursor);
				messages.add(item);
				cursor.moveToNext();
			}
		} catch (SQLException oops) {
			Log.e(TAG, "Failed to retrieve the messages.");
		} finally {
			close();
			if (cursor != null) {
				cursor.close();
			}
		}

		return messages;
	}

	/**
	 * Gets all the messages based on the author which can be the patient or the
	 * therapist.
	 * 
	 * @param author
	 *            Patient or therapist
	 * @return List with all the messages from that author.
	 */
	public List<MessageItem> getMessages(Author author) {
		List<MessageItem> messages = new ArrayList<MessageItem>();
		Cursor cursor = null;

		String sql = "SELECT " + Database.LOCAL_ID + ", " + Database.ENTITY_ID
				+ ", " + Database.ENTITY_CREATION_DATE + ", "
				+ Database.ENTITY_UPDATE_DATE + ", " + Database.MESSAGES_AUTHOR
				+ ", " + Database.MESSAGES_MSG + " FROM "
				+ Database.TABLE_MESSAGES + ";";

		try {
			open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				MessageItem item = cursorToMessageItem(cursor);
				if ((item.isFromPatient() && author.equals(Author.PATIENT))
						|| (!item.isFromPatient() && author
								.equals(Author.THERAPIST))) {
					messages.add(item);
				}
				cursor.moveToNext();
			}
		} catch (SQLException oops) {
			Log.e(TAG, "Failed to retrieve the messages.");
		} finally {
			close();
			if (cursor != null) {
				cursor.close();
			}
		}

		return messages;
	}

	/**
	 * Creates a MessageItem based on the information of the Cursor after the
	 * query.
	 * 
	 * @param cursor
	 *            Cursor with the information from the database.
	 * @return A MessageItem with that information.
	 */
	public MessageItem cursorToMessageItem(Cursor cursor) {
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
		case PATIENT:
			message.setFromPatient(true);
			break;
		case THERAPIST:
			message.setFromPatient(false);
			break;
		}

		return message;
	}
}
