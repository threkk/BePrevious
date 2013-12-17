package com.hva.boxlabapp.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LibraryDatasource {
	
    protected static final String TAG = LibraryDatasource.class.getName();
    private SQLiteDatabase database;
    private LibraryDatabase dbhelper;
    
    public LibraryDatasource (Context context){
    	this.dbhelper = new LibraryDatabase(context);
    }
    
    public LibraryDatasource createDatabase() throws SQLException {
    	try {
    		dbhelper.createDataBase();
    	} catch (SQLException oops) {
    		Log.e(TAG, oops.getMessage() + " Unable to create database.");
    	} 
    	
    	return this;
    }
    
    public void open() throws SQLException {
    	try {
    		dbhelper.openDataBase();
    		database = dbhelper.getReadableDatabase();
    		
    	} catch (SQLException oops){
    		Log.e(TAG, oops.getMessage());
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void close(){
    	database = null;
    	dbhelper.close();
    }
    
    public String getName(int id) {
    	String name = "";
    	Cursor cursor = null;
    	String sql = "SELECT name FROM library WHERE _id = " + id + ";";
    	
    	try {
			this.open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();
			name = cursor.getString(0);
		} catch (SQLException e) {
			Log.e(TAG, "Failed to retrieve exercises", e);
			
		} finally {
			this.close();
			if (cursor != null) {
				cursor.close();
			}
		}
    	return name;
    }
    
    public List<String> getNames() {
    	List<String> names = new ArrayList<String>();
    	Cursor cursor = null;
    	String sql = "SELECT name FROM library;";
    	
    	try {
			this.open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				names.add(cursor.getString(0));
				cursor.moveToNext();
			}
		} catch (SQLException e) {
			Log.e(TAG, "Failed to retrieve exercises", e);
		} finally {
			close();
			if (cursor != null) {
				cursor.close();
			}
		}
    	
    	return names;
    }
    
    public String getURIById(int id) {
    	String uri = null;
    	Cursor cursor = null;
    	String sql = "SELECT desc_uri FROM library WHERE _id = " + id + ";";
    	
    	try {
			this.open();
			cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();
			uri = cursor.getString(0);
			
		} catch (SQLException e) {
			Log.e(TAG, "Failed to retrieve exercises", e);
		} finally {
			this.close();
			if (cursor != null) {
				cursor.close();
			}
		}
    	return uri;
    }
    
	@SuppressLint("SdCardPath")
	private class LibraryDatabase extends SQLiteOpenHelper{

    	private String DB_PATH = ""; 
    	private static final String DB_NAME = "dbexercises.db";
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


    	public void createDataBase() {

    		boolean mDataBaseExist = checkDataBase();

    		if(mDataBaseExist){
    			this.getReadableDatabase();
    			this.close();
    		} else {
    			try {
    				copyDataBase();
    				Log.e(TAG, "createDatabase database created");
    			} catch (IOException mIOException) {
    				throw new Error("ErrorCopyingDataBase");
    			}
    		}
    	}

    	public boolean checkDataBase(){
    		File dbFile = new File(DB_PATH + DB_NAME);
    		return dbFile.exists();
    	}
    	
    	private void copyDataBase() throws IOException{
    		InputStream mInput = null;
    		String[] assets = mContext.getAssets().list("");
    		for(String asset : assets){
    			Log.e("Asset",asset);
    		}
    		try{
    			mInput = mContext.getAssets().open(DB_NAME);
    		} catch(NullPointerException npe){
    			Log.e("ERROR", "Error abriendo la DB");
    		}
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
    	public boolean openDataBase() throws SQLException, IOException {
    		String mPath = DB_PATH + DB_NAME;
    		this.createDataBase();
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
}
