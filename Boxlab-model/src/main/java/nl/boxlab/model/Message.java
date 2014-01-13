package nl.boxlab.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Message extends Entity implements Serializable {

	private static final long serialVersionUID = -5653322729105126232L;

	public static final Comparator<Date> COMPARATOR_DATE = new Comparator<Date>() {

		public int compare(Date o1, Date o2) {
			return o1.compareTo(o2);
		}
	};

	private String identity;
	private String message;

	private boolean fromPatient;
	private boolean read;

	public Message() {

	}

	public Message(String message) {
		this.message = message;
	}

	public Message(String message, boolean fromPatient) {
		this.message = message;
		this.fromPatient = fromPatient;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		firePropertyChange("identity", this.identity, this.identity = identity);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		firePropertyChange("message", this.message, this.message = message);
	}

	public boolean isFromPatient() {
		return fromPatient;
	}

	public void setFromPatient(boolean fromPatient) {
		firePropertyChange("fromPatient", this.fromPatient, this.fromPatient = fromPatient);
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		firePropertyChange("read", this.read, this.read = read);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identity == null) ? 0 : identity.hashCode());
		result = prime * result + ((getCreated() == null) ? 0 : getCreated().hashCode());
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
		if (identity == null) {
			if (other.identity != null)
				return false;
		} else if (!identity.equals(other.identity))
			return false;
		if (getCreated() == null) {
			if (other.getCreated() != null)
				return false;
		} else if (!getCreated().equals(other.getCreated()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getMessage();
	}
}
