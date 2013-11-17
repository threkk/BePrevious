package nl.boxlab.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExerciseEntry extends Entity {

	private Integer id;
	private Date date;
	private Integer exerciseId;
	private double weight;
	private boolean support;
	private boolean done;

	private List<Integer> repetitions = new ArrayList<Integer>();
	private List<ExerciseNote> notes = new ArrayList<ExerciseNote>();

	public ExerciseEntry() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		firePropertyChange("id", this.id, this.id = id);
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
		firePropertyChange("exerciseId", this.exerciseId, this.exerciseId = exerciseId);
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		firePropertyChange("weight", this.weight, this.weight = weight);
	}

	public boolean isSupport() {
		return support;
	}

	public void setSupport(boolean support) {
		firePropertyChange("support", this.support, this.support = support);
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
}
