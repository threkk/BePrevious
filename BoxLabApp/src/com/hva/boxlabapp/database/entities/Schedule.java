package com.hva.boxlabapp.database.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Schedule {
	
	private Date date;
	private int exercise;
	private List<Integer> repetitions;
	private String notes;
	
	public Schedule(Date date, int exercise, String repetitions, String notes){
		this.date = date;
		this.exercise = exercise;
		this.notes = notes;
		this.repetitions = new ArrayList<Integer>();
	
		for(String rep : repetitions.split(" ")){
			this.repetitions.add(Integer.parseInt(rep));
		}

	}
	
	public Schedule(long date, int exercise, String repetitions, String notes){
		this.date = new Date(date);
		this.exercise = exercise;
		this.notes = notes;
		this.repetitions = new ArrayList<Integer>();
		
		for(String rep : repetitions.split(" ")){
			this.repetitions.add(Integer.parseInt(rep));
		}

	}

	public Date getDate() {
		return date;
	}

	public int getExercise() {
		return exercise;
	}

	public List<Integer> getRepetitions() {
		return repetitions;
	}
	
	public String getNotes(){
		return notes;
	}

	// Testing thing.
	@Override
	public String toString() {
		return "Schedule [date=" + date + ", exercise=" + exercise
				+ ", repetitions=" + repetitions + ", notes=" + notes + "]";
	}
	
	
}
