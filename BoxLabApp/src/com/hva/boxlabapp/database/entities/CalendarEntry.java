package com.hva.boxlabapp.database.entities;

public class CalendarEntry {
	private long date;
	private int exercise;
	private String reps;
	private String notes;
	
	public CalendarEntry(long date, int exercise, String reps, String notes){
		this.date = date;
		this.exercise = exercise;
		this.reps = reps;
		this.notes = notes;
	}

	public long getDate() {
		return date;
	}

	public int getExercise() {
		return exercise;
	}

	public String getReps() {
		return reps;
	}

	public String getNotes() {
		return notes;
	}
	
	
}
