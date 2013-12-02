package nl.boxlab.view.schedule;

import java.util.Date;
import java.util.List;

import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.Message;

/**
 * @author Maarten
 * 
 */
public class CalendarItem {

	private Date date;
	private boolean usable;
	private ExerciseEntry entry;
	private List<Message> messages;

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

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}
