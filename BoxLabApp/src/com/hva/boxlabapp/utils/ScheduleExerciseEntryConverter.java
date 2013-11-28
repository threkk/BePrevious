package com.hva.boxlabapp.utils;

import java.util.ArrayList;
import java.util.List;

import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.ExerciseNote;

import com.hva.boxlabapp.database.entities.Schedule;

public class ScheduleExerciseEntryConverter {

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
		entry.setNotes(notes);
		return entry;
	}

	public static Schedule fromExerciseEntryToSchedule(ExerciseEntry entry) {
		String reps = "";
		for (Integer rep : entry.getRepetitions()) {
			reps += rep + " ";
		}
		reps = reps.substring(0, reps.length() - 1);

		String notes = "";
		for (ExerciseNote ex : entry.getNotes()) {
			notes += ex.getMessage() + "\n";
		}

		Schedule schedule = new Schedule(entry.getDate(),
				entry.getExerciseId(), reps, notes);
		return schedule;
	}
}
