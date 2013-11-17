package nl.boxlab.controller.library;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import nl.boxlab.ClientContext;
import nl.boxlab.controller.details.PatientDetailController;
import nl.boxlab.controller.schedule.PatientScheduleController;
import nl.boxlab.model.Patient;
import nl.boxlab.remote.PatientProvider;
import nl.boxlab.view.library.PatientLibraryView;

public class PatientLibraryController extends MouseAdapter implements
        ActionListener {

	public static final String ACTION_SHOW_PATIENT = "show-selected-patient";
	public static final String ACTION_SHOW_SCHEDULE = "show-selected-schedule";

	private ClientContext context;
	private PatientDetailController detailController;
	private PatientScheduleController scheduleController;
	private PatientLibraryView view;
	private JFrame frame;

	public PatientLibraryController(ClientContext context) {
		this.context = context;

		this.detailController = new PatientDetailController(context);
		this.scheduleController = new PatientScheduleController(context);

		this.view = new PatientLibraryView();
		this.view.setListener(this);

		initView();
	}

	private void initView() {
		PatientProvider patientProvider = this.context.getPatientProvider();

		List<Patient> patients = patientProvider.getPatients();
		this.view.getTableController().clear();
		this.view.getTableController().addAll(patients);

		if (!patients.isEmpty()) {
			this.view.getTableController().setSelectedRowIndex(0);
		}
	}

	public void showView() {
		if (this.frame != null && this.frame.isVisible()) {
			this.frame.dispose();
		}

		this.frame = new JFrame("Patient overview");
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.frame.setContentPane(view);
		this.frame.pack();
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
	}

	public void showSelectedPatient() {
		Patient selected = this.view.getTableController().getSelectedRow();
		if (selected == null) {
			return;
		}

		this.detailController.showView(view, selected);
		this.view.repaint();
	}

	public void showSelectedSchedule() {
		Patient selected = this.view.getTableController().getSelectedRow();
		if (selected == null) {
			return;
		}

		this.scheduleController.showView(view, selected);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			showSelectedPatient();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (ACTION_SHOW_PATIENT.equals(e.getActionCommand())) {
			showSelectedPatient();
		} else if (ACTION_SHOW_SCHEDULE.equals(e.getActionCommand())) {
			showSelectedSchedule();
		}
	}
}
