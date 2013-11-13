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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + exercise;
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result
				+ ((repetitions == null) ? 0 : repetitions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Schedule other = (Schedule) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (exercise != other.exercise)
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (repetitions == null) {
			if (other.repetitions != null)
				return false;
		} else if (!repetitions.equals(other.repetitions))
			return false;
		return true;
	}
	
	
}
