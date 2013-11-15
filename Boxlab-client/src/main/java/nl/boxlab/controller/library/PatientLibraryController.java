package nl.boxlab.controller.library;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import nl.boxlab.ClientContext;
import nl.boxlab.ModelUtilities;
import nl.boxlab.controller.Command;
import nl.boxlab.controller.details.PatientDetailController;
import nl.boxlab.model.Patient;
import nl.boxlab.remote.PatientProvider;
import nl.boxlab.table.TableController;
import nl.boxlab.view.library.PatientLibraryTableView;
import nl.boxlab.view.library.PatientLibraryView;

public class PatientLibraryController extends MouseAdapter implements
		ActionListener {

	private ClientContext context;
	private PatientDetailController detailController;
	private TableController<Patient> tableController;
	private PatientLibraryView view;
	private JFrame frame;

	public PatientLibraryController(ClientContext context) {
		this.context = context;
		this.detailController = new PatientDetailController(context);
		this.tableController = new TableController<Patient>(
				new PatientLibraryTableView(), null);

		this.view = new PatientLibraryView(tableController);
		this.view.setListener(this);

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

	public void showPatient(final Patient patient) {
		final Patient clone = ModelUtilities.deepClone(patient);
		System.out.println("clone: " + clone);
		this.detailController.showView(view, clone, new Command() {
			public void execute() {
				ModelUtilities.merge(clone, patient);
			}
		});
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			Patient selected = this.tableController.getSelectedRow();
			System.out.println("selected: " + selected);
			showPatient(this.tableController.getSelectedRow());
		}
	}

	public void actionPerformed(ActionEvent e) {
		showPatient(this.tableController.getSelectedRow());
	}
}
