package nl.boxlab.view;

import nl.boxlab.model.Patient;
import nl.boxlab.table.Column;
import nl.boxlab.table.TableViewDefinition;

public class PatientLibraryTableView extends TableViewDefinition {

	@Column(key = "id", title = "Identification", order = 1)
	public String getIdentification(Patient patient) {
		return patient.getIdentification();
	}

	@Column(key = "surname", title = "Surname", order = 2)
	public String getSurname(Patient patient) {
		return patient.getSurname();
	}

	@Column(key = "lastname", title = "Lastname", order = 3)
	public String getLastname(Patient patient) {
		return patient.getLastName();
	}
}