package nl.boxlab.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExerciseEntry extends Entity implements Serializable {

	private static final long serialVersionUID = 881167821394433023L;

	private String identification;
	private Date date;
	private String note;
	private Integer exerciseId;
	private boolean done;
	private List<Integer> repetitions = new ArrayList<Integer>();

	public ExerciseEntry() {

	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		firePropertyChange("identification", this.identification, this.identification = identification);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		firePropertyChange("date", this.date, this.date = date);
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		firePropertyChange("note", this.note, this.note = note);
	}

	public Integer getExerciseId() {
		return exerciseId;
	}

	public void setExerciseId(Integer exerciseId) {
		firePropertyChange("exerciseId", this.exerciseId, this.exerciseId = exerciseId);
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

	@Override
	public String toString() {
		return "ExerciseEntry [date=" + date + ", exerciseId=" + exerciseId
		        + ", done=" + done + ", repetitions=" + repetitions + "]";
	}
}
