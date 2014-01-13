package nl.boxlab.model;

import java.io.Serializable;
import java.util.List;

/**
 * A data transfer object used for communication. This DTO contains all relevant
 * classes for all systems.
 * 
 * @author Maarten
 * 
 */
public class BoxlabDTO extends Entity implements Serializable {

	private static final long serialVersionUID = 9048968249688873048L;

	private List<ExerciseEntry> exerciseEntries;
	private List<Message> messages;
	private List<SensorFeedback> sensorFeedback;

	public BoxlabDTO() {

	}

	public List<ExerciseEntry> getExerciseEntries() {
		return exerciseEntries;
	}

	public void setExerciseEntries(List<ExerciseEntry> exerciseEntries) {
		this.exerciseEntries = exerciseEntries;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public List<SensorFeedback> getSensorFeedback() {
		return sensorFeedback;
	}

	public void setSensorFeedback(List<SensorFeedback> sensorFeedback) {
		this.sensorFeedback = sensorFeedback;
	}
}
