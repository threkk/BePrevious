package nl.boxlab.model;

import java.io.Serializable;

public class Patient implements Serializable {

	private static final long serialVersionUID = -6529007671943127008L;

	private String identification;
	private String firstName;
	private String lastName;

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
