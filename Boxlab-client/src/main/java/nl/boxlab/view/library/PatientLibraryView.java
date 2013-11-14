package nl.boxlab.view.library;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.controller.library.PatientLibraryController;
import nl.boxlab.model.Patient;
import nl.boxlab.table.TableController;

@SuppressWarnings("serial")
public class PatientLibraryView extends JPanel {

	private TableController<Patient> tableController;
	private JTable table;
	private JButton btnShowPatient;

	public PatientLibraryView(TableController<Patient> tableController) {
		this.tableController = tableController;

		initComponents();
	}

	private void initComponents() {
		this.table = new JTable();
		this.tableController.setup(table);
		this.btnShowPatient = new JButton("Show patient");

		JLabel lblTitle = new JLabel("Patient overview");
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 24));

		JPanel panelHeader = new JPanel(new MigLayout("", "[]", "[][]"));
		panelHeader.setBackground(Color.WHITE);
		panelHeader.add(lblTitle, "cell 0 0");
		panelHeader.add(new JLabel(
				"A list of all patients currently associated to you"),
				"cell 0 1");

		JPanel panelCenter = new JPanel(new MigLayout("", "[grow]", "[][grow]"));
		panelCenter.setBorder(BorderFactory.createEtchedBorder());
		panelCenter.add(btnShowPatient, "cell 0 0");
		panelCenter.add(new JScrollPane(table), "cell 0 1,grow");

		setLayout(new BorderLayout(0, 0));
		add(panelHeader, BorderLayout.NORTH);
		add(panelCenter, BorderLayout.CENTER);
	}

	public void setListener(PatientLibraryController controller) {
		this.table.addMouseListener(controller);
		this.btnShowPatient.addActionListener(controller);
	}
}
