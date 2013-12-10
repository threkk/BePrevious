package nl.boxlab.model;

import java.util.Comparator;
import java.util.Date;

public class ExerciseNote extends Entity {

	public static final Comparator<Date> COMPARATOR_DATE = new Comparator<Date>() {

		@Override
		public int compare(Date o1, Date o2) {
			return o1.compareTo(o2);
		}

	};

	private String message;
	private boolean patient;
	private boolean read;
	private Date date;

	public ExerciseNote() {

	}

	public ExerciseNote(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isPatient() {
		return patient;
	}

	public void setPatient(boolean patient) {
		this.patient = patient;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		ExerciseNote other = (ExerciseNote) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (getId() == null) {
			if (getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}
