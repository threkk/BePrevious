package com.hva.boxlabapp.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ExercisesDatasource {
	
    protected static final String TAG = "ExerciseDatasource";
    private SQLiteDatabase sqldb;
    private DatabaseExercises database;
    
    public ExercisesDatasource (Context context){
    	this.database = new DatabaseExercises(context);
    }
    
    public ExercisesDatasource createDatabase() throws SQLException, IOException{
    	try {
    		database.createDataBase();
    	} catch (SQLException oops) {
    		Log.e(TAG, oops.getMessage() + " Unable to create database.");
    	}
    	return this;
    }
    
    public void open() throws SQLException {
    	try {
    		database.openDataBase();
    		sqldb = database.getReadableDatabase();
    	} catch (SQLException oops){
    		Log.e(TAG, oops.getMessage());
    	}
    }
    
    public void close(){
    	sqldb = null;
    	database.close();
    }
    
    public List<String> getNames(){
    	List<String> names = new ArrayList<String>();
    	Cursor cursor = null;
    	String sql = "SELECT name FROM exercises;";
    	
    	try {
			open();
			cursor = sqldb.rawQuery(sql, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				names.add(cursor.getString(0));
				cursor.moveToNext();
			}
		} catch (SQLException e) {
			Log.e(TAG, "Failed to retrieve devices", e);
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
			open();
			cursor = sqldb.rawQuery(sql, null);
			cursor.moveToFirst();
			uri = cursor.getString(0);
			
		} catch (SQLException e) {
			Log.e(TAG, "Failed to retrieve devices", e);
		} finally {
			close();
			if (cursor != null) {
				cursor.close();
			}
		}
    	return uri;
    }
}
