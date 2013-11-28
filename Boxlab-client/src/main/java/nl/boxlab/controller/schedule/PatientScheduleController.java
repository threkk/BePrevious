package nl.boxlab.controller.schedule;

import java.awt.Component;
import java.util.List;

import javax.swing.JDialog;

import nl.boxlab.ClientContext;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.Patient;
import nl.boxlab.remote.ExerciseEntryProvider;
import nl.boxlab.view.schedule.PatientScheduleView;

public class PatientScheduleController {

	private ClientContext context;
	private PatientScheduleView view;
	private Patient patient;
	private JDialog dialog;

	public PatientScheduleController(ClientContext context) {
		this.context = context;
		this.view = new PatientScheduleView();
	}

	public void showView(Component owner, Patient patient) {
		String identification = patient.getIdentification();
		ExerciseEntryProvider exerciseEntryProvider = this.context.getExerciseEntryProvider();
		List<ExerciseEntry> entries = exerciseEntryProvider.getEntries(identification);

		this.patient = patient;
		this.view.setEntries(entries);
		this.dialog = new JDialog();
		this.dialog.setTitle("Showing patient schedule");
		this.dialog.setContentPane(view);
		this.dialog.pack();
		this.dialog.setModal(true);
		this.dialog.setLocationRelativeTo(owner);
		this.dialog.setVisible(true);
	}

	public void hideView() {
		this.dialog.dispose();
		this.dialog = null;
		// this.view.setPatient(null);
	}

}
