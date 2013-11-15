package nl.boxlab.view.library;

import nl.boxlab.model.Patient;
import nl.boxlab.table.Column;
import nl.boxlab.table.TableViewDefinition;

public class PatientLibraryTableView extends TableViewDefinition {

	@Column(key = "id", title = "Identification", order = 1)
	public String getIdentification(Patient patient) {
		return patient.getIdentification();
	}

	@Column(key = "firstname", title = "First name", order = 2)
	public String getFirstName(Patient patient) {
		return patient.getFirstName();
	}

	@Column(key = "lastname", title = "Last name", order = 3)
	public String getLastname(Patient patient) {
		return patient.getLastName();
	}
}
