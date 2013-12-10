package com.hva.boxlabapp.database.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.ExerciseNote;

public class Schedule implements Serializable{

	private static final long serialVersionUID = 7798051013083433708L;
	private int _id;
	private String _remoteId;
	private final long millis;
	private final int exercise;
	private final String reps;
	private final String notes;
	private boolean done;

	public Schedule(String rId, Date date, int exercise, String repetitions, String notes, boolean done) {
		this._id = -1; // By default -1, which means that it's not in the database.. yet
		this._remoteId = rId; // Provided by the therapist
		this.millis = date.getTime();
		this.exercise = exercise;
		this.notes = notes;
		this.reps = repetitions;
		this.done = done;
	}

	public Date getDate() {
		return new Date(millis);
	}
	
	public List<Integer> getRepetitions() {
		List<Integer> repetitions = new ArrayList<Integer>();
		
		for(String rep : reps.split(" ") ){
			repetitions.add(Integer.parseInt(rep));
		}
		return repetitions;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_remoteId() {
		return _remoteId;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public long getMillis() {
		return millis;
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
	
	@Override
	public String toString() {
		return "Schedule [_id=" + _id + ", _remoteId=" + _remoteId
				+ ", millis=" + millis + ", exercise=" + exercise + ", reps="
				+ reps + ", notes=" + notes + ", done=" + done + "]";
	}

	public static ExerciseEntry fromScheduleToExerciseEntry(Schedule schedule) {
		// Don't touch the id dude...
		ExerciseEntry entry = new ExerciseEntry();
		entry.setDate(schedule.getDate());
		entry.setDone(schedule.isDone());
		entry.setExerciseId(schedule.getExercise());
		entry.setRepetitions(schedule.getRepetitions());

		ExerciseNote ex = new ExerciseNote(schedule.getNotes());
		List<ExerciseNote> notes = new ArrayList<ExerciseNote>();
		notes.add(ex);
		// TODO: Temporary fix
		//entry.setNotes(notes);
		return entry;
	}

	public static Schedule fromExerciseEntryToSchedule(ExerciseEntry entry) {
		String reps = "";
		for (Integer rep : entry.getRepetitions()) {
			reps += rep + " ";
		}
		reps = reps.substring(0, reps.length() - 1);

		String notes = "";
//		for (ExerciseNote ex : entry.getNotes()) {
//			notes += ex.getMessage() + "\n";
//		}
		
		// TODO: Temporary fix
		notes = "Temporary fix";

		Schedule schedule = new Schedule(entry.getId(), entry.getDate(), entry.getExerciseId(), reps, notes, entry.isDone());
		return schedule;
	}
	
}
