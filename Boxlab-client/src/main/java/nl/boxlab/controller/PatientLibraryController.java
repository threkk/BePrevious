package nl.boxlab.controller;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import nl.boxlab.ClientContext;
import nl.boxlab.model.Patient;
import nl.boxlab.remote.PatientProvider;
import nl.boxlab.table.TableController;
import nl.boxlab.view.PatientLibraryTableView;
import nl.boxlab.view.PatientLibraryView;

public class PatientLibraryController {

	private ClientContext context;
	private PatientLibraryView view;
	private TableController<Patient> tableController;
	private JFrame frame;

	public PatientLibraryController(ClientContext context) {
		this.context = context;
		this.tableController = new TableController<Patient>(
				new PatientLibraryTableView(), null);
		this.view = new PatientLibraryView(tableController);

		initView();
	}

	private void initView() {
		PatientProvider patientProvider = this.context.getPatientProvider();

		this.tableController.clear();
		this.tableController.addAll(patientProvider.getPatients());
	}

	public void showView() {
		if (this.frame != null && this.frame.isVisible()) {
			this.frame.dispose();
		}

		this.frame = new JFrame("Patient overview");
		this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.frame.setContentPane(view);
		this.frame.pack();
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
	}
}
