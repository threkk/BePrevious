package com.hva.boxlabapp.database.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Schedule {
	
	private Date date;
	private int exercise;
	private List<Integer> repetitions;
	
	public Schedule(Date date, int exercise, String repetitions){
		this.date = date;
		this.exercise = exercise;
		this.repetitions = new ArrayList<Integer>();
		
		for(String rep : repetitions.split(" ")){
			this.repetitions.add(Integer.parseInt(rep));
		}

	}
	
	public Schedule(long date, int exercise, String repetitions){
		this.date = new Date(date);
		this.exercise = exercise;
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
	
	
}
