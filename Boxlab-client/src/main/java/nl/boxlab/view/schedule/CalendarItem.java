package nl.boxlab.view.schedule;

import java.util.Date;

import nl.boxlab.model.ExerciseEntry;

/**
 * @author Maarten
 * 
 */
public class CalendarItem {

	private Date date;
	private boolean usable;
	private ExerciseEntry entry;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isUsable() {
		return usable;
	}

	public void setUsable(boolean usable) {
		this.usable = usable;
	}

	public ExerciseEntry getEntry() {
		return entry;
	}

	public void setEntry(ExerciseEntry entry) {
		this.entry = entry;
	}

	@Override
	public String toString() {
		return usable ? ":)" : "x";
	}

}
