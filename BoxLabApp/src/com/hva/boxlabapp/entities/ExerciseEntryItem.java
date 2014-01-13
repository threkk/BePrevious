package com.hva.boxlabapp.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.boxlab.model.ExerciseEntry;

public class ExerciseEntryItem extends ExerciseEntry {

	private static final long serialVersionUID = 7798051013083433708L;
	private int _id;
	private String reps;

	public ExerciseEntryItem(String idenString, Date original, Date date, int exercise, String repetitions, String notes, boolean done) {
		super();
		this.setIdentification(idenString);
		this.setCreated(original);
		this.setUpdated(new Date());
		
		this.setDate(date);
		this.setNote(notes);
		this.setExerciseId(exercise);
		this.setDone(done);
		this.setRepetitions(reptsToList(repetitions));
		this.setReps(repetitions);
	}
	
	public ExerciseEntryItem(ExerciseEntry entry){
		super();
		this.setIdentification(entry.getIdentification());
		this.setCreated(getCreated());
		this.setUpdated(new Date());
		
		this.setDate(entry.getDate());
		this.setNote(entry.getNote());
		this.setExerciseId(entry.getExerciseId());
		this.setDone(entry.isDone());
		this.setRepetitions(entry.getRepetitions());
		this.setReps(listToReps(entry.getRepetitions()));
	}
	
	public int get_id() {
		return _id;
	}

	public void set_id(int id) {
		this._id = id;
	}

	public String getReps() {
		return reps;
	}
	
	public void setReps(String reps) {
		this.reps = reps;
	}

	@Override
	public String toString() {
		return "Schedule [_id=" + _id + ", reps=" + reps
				+ ", getIdentification()=" + getIdentification()
				+ ", getDate()=" + getDate() + ", getNote()=" + getNote()
				+ ", getExerciseId()=" + getExerciseId() + ", isDone()="
				+ isDone() + "]";
	}

	private List<Integer> reptsToList(String mReps) {
		List<Integer> repetitions = new ArrayList<Integer>();
		
		for(String rep : mReps.split(" ") ){
			repetitions.add(Integer.parseInt(rep));
		}
		return repetitions;
	}
	
	private String listToReps(List<Integer> repetitions) {
		String reps = "";
		
		for(Integer i : repetitions) {
			reps += i + " ";
		}
		
		return reps.substring(0, reps.length()-1);
	}
}
