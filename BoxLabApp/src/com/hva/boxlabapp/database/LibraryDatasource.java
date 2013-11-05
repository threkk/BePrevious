package com.hva.boxlabapp.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LibraryDatasource {
	
    protected static final String TAG = LibraryDatasource.class.getName();
    private SQLiteDatabase database;
    private LibraryDatabase dbhelper;
    
    public LibraryDatasource (Context context){
    	this.dbhelper = new LibraryDatabase(context);
    }
    
//    public ExercisesDatasource createDatabase() throws SQLException, IOException{
//    	try {
//    		dbhelper.createDataBase();
//    	} catch (SQLException oops) {
//    		Log.e(TAG, oops.getMessage() + " Unable to create database.");
//    	}
//    	return this;
//    }
    
    public void open() throws SQLException {
    	try {
    		dbhelper.openDataBase();
    		database = dbhelper.getReadableDatabase();
    	} catch (SQLException oops){
    		Log.e(TAG, oops.getMessage());
    	}
    }
    
    public void close(){
    	database = null;
    	dbhelper.close();
    }
    
    public String getName(int id){
    	String name = "";
    	Cursor cursor = null;
    	String sql = "SELECT name FROM exercises WHERE _id = " + id + ";";
    	
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
    
    public List<String> getNames(){
    	List<String> names = new ArrayList<String>();
    	Cursor cursor = null;
    	String sql = "SELECT name FROM exercises;";
    	
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
    
    public String getURIById(int id){
    	String uri = null;
    	Cursor cursor = null;
    	String sql = "SELECT desc_uri FROM exercises WHERE _id = " + id + ";";
    	
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
}
