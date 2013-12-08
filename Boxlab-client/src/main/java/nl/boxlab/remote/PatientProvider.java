package nl.boxlab.remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.boxlab.model.Patient;

public class PatientProvider extends AbstractProvider {

	public static final String PATH_PATIENTS = "patients";

	public PatientProvider() {

	}

	public List<Patient> getPatients() {
		String data = getClient().get(PATH_PATIENTS);
		Patient[] patients = getSerializer().deserializeArray(Patient[].class, data);

		return new ArrayList<>(Arrays.asList(patients));
	}

	public void save(Patient patient) {
		String data = getSerializer().serialize(patient);
		getClient().post(PATH_PATIENTS, data);
	}
}
