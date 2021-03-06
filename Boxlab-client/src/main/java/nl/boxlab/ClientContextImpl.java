package nl.boxlab;

import nl.boxlab.remote.ExerciseEntryProvider;
import nl.boxlab.remote.MessageProvider;
import nl.boxlab.remote.PatientProvider;

public class ClientContextImpl implements ClientContext {

	private boolean authenticated;

	private PatientProvider patientProvider;
	private ExerciseEntryProvider exerciseEntryProvider;
	private MessageProvider messageProvider;

	public ClientContextImpl() {

	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void authenticate(String username, char[] password) {
		// TODO: implement authentication
		this.authenticated = true;
	}

	public PatientProvider getPatientProvider() {
		return patientProvider;
	}

	public void setPatientProvider(PatientProvider patientProvider) {
		this.patientProvider = patientProvider;
	}

	public ExerciseEntryProvider getExerciseEntryProvider() {
		return exerciseEntryProvider;
	}

	public void setExerciseEntryProvider(
			ExerciseEntryProvider exerciseEntryProvider) {
		this.exerciseEntryProvider = exerciseEntryProvider;
	}

	public MessageProvider getMessageProvider() {
		return messageProvider;
	}

	public void setMessageProvider(MessageProvider messageProvider) {
		this.messageProvider = messageProvider;
	}
}
