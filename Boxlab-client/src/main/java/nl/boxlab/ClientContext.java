package nl.boxlab;

import nl.boxlab.remote.ExerciseEntryProvider;
import nl.boxlab.remote.PatientProvider;

public class ClientContext {

	private String host;
	private int port;
	private boolean authenticated;

	private PatientProvider patientProvider;
	private ExerciseEntryProvider exerciseEntryProvider;

	public ClientContext() {
		this.patientProvider = new PatientProvider(this);
		this.exerciseEntryProvider = new ExerciseEntryProvider(this);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void authenticate(String username, char[] password) {
		this.authenticated = true;
	}

	public PatientProvider getPatientProvider() {
		return patientProvider;
	}

	public ExerciseEntryProvider getExerciseEntryProvider() {
		return exerciseEntryProvider;
	}
}
