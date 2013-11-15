package nl.boxlab.view.details;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.controller.details.PatientDetailController;
import nl.boxlab.model.Patient;

@SuppressWarnings("serial")
public class PatientDetailDialog extends JDialog {

	private JTextField txtFirstName;
	private JTextField txtLastName;
	private JLabel lblIdentification;
	private JButton btnShowSchedule;
	private JButton btnSave;
	private JButton btnCancel;

	private Patient patient;

	/**
	 * Create the panel.
	 */
	public PatientDetailDialog() {
		initComponents();
		setModal(true);
	}

	private void initComponents() {
		this.lblIdentification = new JLabel("-");
		this.txtFirstName = new JTextField();
		this.txtLastName = new JTextField();
		this.btnShowSchedule = new JButton("Show schedule");
		this.btnSave = new JButton("Save");
		this.btnSave.setActionCommand(PatientDetailController.ACTION_SAVE);
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setActionCommand(PatientDetailController.ACTION_CANCEL);

		JLabel lblTitle = new JLabel("Patient details");
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 24));

		JPanel panelHeader = new JPanel(new MigLayout("", "[]", "[][]"));
		panelHeader.setBackground(Color.WHITE);
		panelHeader.add(lblTitle, "cell 0 0");
		panelHeader.add(new JLabel(
				"Change a patients details and add items to the schedule"),
				"cell 0 1");

		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(new MigLayout("", "[][200px]", "[][][][]"));
		panelCenter.setBorder(BorderFactory.createEtchedBorder());
		panelCenter.add(new JLabel("Identification:"), "cell 0 0");
		panelCenter.add(lblIdentification, "cell 1 0");
		panelCenter.add(new JLabel("First name:"), "cell 0 1");
		panelCenter.add(txtFirstName, "cell 1 1,growx");
		panelCenter.add(new JLabel("Last name:"), "cell 0 2");
		panelCenter.add(txtLastName, "cell 1 2,growx");

		JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		panelButton.add(btnSave);
		panelButton.add(btnCancel);

		setLayout(new BorderLayout(0, 0));
		add(panelHeader, BorderLayout.NORTH);
		add(panelCenter, BorderLayout.CENTER);

		panelCenter.add(btnShowSchedule, "cell 1 3");
		add(panelButton, BorderLayout.SOUTH);
	}

	private void initForm() {
		this.lblIdentification.setText(patient.getIdentification());
		this.txtFirstName.setText(patient.getFirstName());
		this.txtLastName.setText(patient.getLastName());
	}

	public void setListener(PatientDetailController controller) {
		this.btnSave.addActionListener(controller);
		this.btnCancel.addActionListener(controller);
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
		if (patient != null) {
			initForm();
		}
	}
}
