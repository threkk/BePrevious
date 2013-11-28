package com.hva.boxlabapp.database.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Schedule implements Serializable{

	private static final long serialVersionUID = 7798051013083433708L;
	private int _id;
	private final long millis;
	private final int exercise;
	private final List<Integer> repetitions;
	private final String reps;
	private final String notes;
	private boolean done;

	public Schedule(Date date, int exercise, String repetitions, String notes) {
		this._id = -1; // By default -1, which means that it's not in the database.. yet
		this.millis = date.getTime();
		this.exercise = exercise;
		this.notes = notes;
		this.reps = repetitions;
		this.repetitions = new ArrayList<Integer>();
		this.done = false; // False by default

		// Better create it only once
		for (String rep : repetitions.split(" ")) {
			this.repetitions.add(Integer.parseInt(rep));
		}

	}

	public Schedule(long date, int exercise, String repetitions, String notes) {
		this._id = -1;
		this.millis = date;
		this.exercise = exercise;
		this.notes = notes;
		this.reps = repetitions;
		this.repetitions = new ArrayList<Integer>();
		this.done = false; // False by default

		for (String rep : repetitions.split(" ")) {
			this.repetitions.add(Integer.parseInt(rep));
		}

	}

	public Date getDate() {
		return new Date(millis);
	}

	public int getExercise() {
		return exercise;
	}

	public List<Integer> getRepetitions() {
		return repetitions;
	}

	public String getNotes() {
		return notes;
	}

	public long getMillis() {
		return millis;
	}

	public String getReps() {
		return reps;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean arg) {
		done = arg;
	}

	public int getId(){
		return _id;
	}
	
	public void setId(int id){
		this._id = id;
	}
	
	// Testing thing.
	@Override
	public String toString() {
		return "Schedule [date=" + getDate() + ", exercise=" + exercise
				+ ", repetitions=" + repetitions + ", notes=" + notes + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (done ? 1231 : 1237);
		result = prime * result + exercise;
		result = prime * result + (int) (millis ^ (millis >>> 32));
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result
				+ ((repetitions == null) ? 0 : repetitions.hashCode());
		result = prime * result + ((reps == null) ? 0 : reps.hashCode());
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
		if (done != other.done)
			return false;
		if (exercise != other.exercise)
			return false;
		if (millis != other.millis)
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
		if (reps == null) {
			if (other.reps != null)
				return false;
		} else if (!reps.equals(other.reps))
			return false;
		return true;
	}

}
