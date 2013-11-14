package nl.boxlab.remote;

import java.util.Arrays;
import java.util.List;

import nl.boxlab.ClientContext;
import nl.boxlab.model.Patient;

public class PatientProvider extends AbstractProvider {

	public PatientProvider(ClientContext context) {
		super(context);
	}

	public List<Patient> getPatients() {
		Patient patientA = new Patient();
		patientA.setIdentification("fdfs8df9jsdf");
		patientA.setFirstName("John");
		patientA.setLastName("Doe");

		Patient patientB = new Patient();
		patientB.setIdentification("kfdgfa62la9h");
		patientB.setFirstName("Jane");
		patientB.setLastName("Doe");

		return Arrays.asList(patientA, patientB);
	}

	public void save(Patient patient) {
		
	}

}
