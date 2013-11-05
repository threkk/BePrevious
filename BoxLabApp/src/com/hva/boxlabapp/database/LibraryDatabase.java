package com.hva.boxlabapp.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("SdCardPath")
public class LibraryDatabase extends SQLiteOpenHelper{

	private static String TAG = "DataBaseExercises";
	private static String DB_PATH = ""; 
	private static String DB_NAME = "db_exercises";
	// This uses other database because it is already filled.

	private SQLiteDatabase mDataBase;
	private final Context mContext;

	public LibraryDatabase (Context context){
		super(context, DB_NAME, null, 1);
		if(android.os.Build.VERSION.SDK_INT >= 4.2){
			DB_PATH = context.getApplicationInfo().dataDir + "/databases/";         
		} else {
			DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		}
		this.mContext = context;
	}


	public void createDataBase() throws IOException {

		boolean mDataBaseExist = checkDataBase();

		if(!mDataBaseExist){
			this.getReadableDatabase();
			this.close();

			try {
				copyDataBase();
				Log.e(TAG, "createDatabase database created");
			} catch (IOException mIOException) {
				throw new Error("ErrorCopyingDataBase");
			}
		}
	}

	private boolean checkDataBase(){
		File dbFile = new File(DB_PATH + DB_NAME);
		return dbFile.exists();
	}
	
	private void copyDataBase() throws IOException{
		InputStream mInput = mContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		
		OutputStream mOutput = new FileOutputStream(outFileName);
		byte[] mBuffer = new byte[1024];
		int mLength;
		
		while ((mLength = mInput.read(mBuffer))>0){
			mOutput.write(mBuffer, 0, mLength);
		}
		
		mOutput.flush();
		mOutput.close();
		mInput.close();
	}

	//Open the database, so we can query it
	public boolean openDataBase() throws SQLException {
		String mPath = DB_PATH + DB_NAME;
		mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		//mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return mDataBase != null;
	}

	@Override
	public synchronized void close() {
		if(mDataBase != null)
			mDataBase.close();
		super.close();
	}


	@Override
	public void onCreate(SQLiteDatabase db) {

	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
