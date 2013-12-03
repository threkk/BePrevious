package nl.boxlab.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Message extends Entity implements Serializable {

	private static final long serialVersionUID = -5653322729105126232L;

	public static final Comparator<Date> COMPARATOR_DATE = new Comparator<Date>() {

		@Override
		public int compare(Date o1, Date o2) {
			return o1.compareTo(o2);
		}

	};

	private Date date;
	private String identity;
	private String message;

	private boolean fromPatient;
	private boolean read;

	public Message() {

	}

	public Message(Date date, String message) {
		this.date = date;
		this.message = message;
	}

	public Message(Date date, String message, boolean fromPatient) {
		this.date = date;
		this.message = message;
		this.fromPatient = fromPatient;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isFromPatient() {
		return fromPatient;
	}

	public void setFromPatient(boolean fromPatient) {
		this.fromPatient = fromPatient;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
		        + ((identity == null) ? 0 : identity.hashCode());
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
		Message other = (Message) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (identity == null) {
			if (other.identity != null)
				return false;
		} else if (!identity.equals(other.identity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getMessage();
	}
}
