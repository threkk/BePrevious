package nl.boxlab;

import nl.boxlab.remote.ExerciseEntryProvider;
import nl.boxlab.remote.PatientProvider;

public interface ClientContext {

	public boolean isAuthenticated();

	public void authenticate(String username, char[] password);

	public PatientProvider getPatientProvider();

	public ExerciseEntryProvider getExerciseEntryProvider();
}
