package nl.boxlab.view.library;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.controller.library.PatientLibraryController;
import nl.boxlab.model.Patient;
import nl.boxlab.table.TableController;
import nl.boxlab.view.TitlePanel;

@SuppressWarnings("serial")
public class PatientLibraryView extends JPanel {

	private TableController<Patient> tableController;
	private JTable table;
	private JButton btnShowPatient;
	private JButton btnShowSchedule;

	public PatientLibraryView() {

		initComponents();
	}

	private void initComponents() {
		this.table = new JTable();
		this.table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tableController = new TableController<Patient>(
		        new PatientLibraryTableView(), null);
		this.tableController.setup(table);
		this.btnShowPatient = new JButton("Show patient");
		this.btnShowPatient.setActionCommand(PatientLibraryController.ACTION_SHOW_PATIENT);
		this.btnShowSchedule = new JButton("Show schedule");
		this.btnShowSchedule.setActionCommand(PatientLibraryController.ACTION_SHOW_SCHEDULE);

		TitlePanel titlePanel = new TitlePanel();
		titlePanel.setTitle("Patient overview");
		titlePanel.setDescription("A list of all patients currently associated to you");

		JPanel panelCenter = new JPanel(new MigLayout("", "[grow]", "[][grow]"));
		panelCenter.setBorder(BorderFactory.createEtchedBorder());
		panelCenter.add(btnShowPatient, "flowx,cell 0 0");
		panelCenter.add(btnShowSchedule, "cell 0 0");
		panelCenter.add(new JScrollPane(table), "cell 0 1,grow");

		setLayout(new BorderLayout(0, 0));
		add(titlePanel, BorderLayout.NORTH);
		add(panelCenter, BorderLayout.CENTER);

	}

	public void setListener(PatientLibraryController controller) {
		this.table.addMouseListener(controller);
		this.btnShowPatient.addActionListener(controller);
		this.btnShowSchedule.addActionListener(controller);
	}

	public void update() {
		this.table.repaint();
	}

	public TableController<Patient> getTableController() {
		return tableController;
	}
}
