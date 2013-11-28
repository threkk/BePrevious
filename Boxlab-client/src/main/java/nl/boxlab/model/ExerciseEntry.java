package nl.boxlab.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import nl.boxlab.remote.JSONEntitySerializer;

public class ExerciseEntry extends Entity {

	private Date date;
	private Integer exerciseId;
	private boolean done;
	private List<Integer> repetitions = new ArrayList<Integer>();
	private List<ExerciseNote> notes = new ArrayList<ExerciseNote>();

	public ExerciseEntry() {

	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		firePropertyChange("date", this.date, this.date = date);
	}

	public Integer getExerciseId() {
		return exerciseId;
	}

	public void setExerciseId(Integer exerciseId) {
		firePropertyChange("exerciseId", this.exerciseId,
		        this.exerciseId = exerciseId);
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		firePropertyChange("done", this.done, this.done = done);
	}

	public List<Integer> getRepetitions() {
		return repetitions;
	}

	public void setRepetitions(List<Integer> repetitions) {
		this.repetitions = repetitions;
	}

	public List<ExerciseNote> getNotes() {
		return notes;
	}

	public void setNotes(List<ExerciseNote> notes) {
		this.notes = notes;
	}
	
	public static void main(String[] args) {
	    ExerciseEntry entry = new ExerciseEntry();
	    entry.setId("KJHH4I343HUI424HI2HJK3H4UI");
	    entry.setDate(new Date());
	    entry.setDone(true);
	    entry.setNotes(Collections.singletonList(new ExerciseNote("hallo")));
	    entry.setRepetitions(Arrays.asList(10,10,10));
	    entry.setExerciseId(1);
	    
	    JSONEntitySerializer serializer = new JSONEntitySerializer();
	    String json = serializer.serialize(entry);
	    System.out.println(json);
    }
}
