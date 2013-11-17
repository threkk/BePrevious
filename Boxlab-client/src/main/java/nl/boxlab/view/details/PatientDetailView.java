package nl.boxlab.view.details;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.controller.details.PatientDetailController;
import nl.boxlab.model.Patient;
import nl.boxlab.view.TitlePanel;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;

@SuppressWarnings("serial")
public class PatientDetailView extends JPanel {

	private BindingGroup m_bindingGroup;
	private nl.boxlab.model.Patient patient = new nl.boxlab.model.Patient();

	private JLabel lblIdentification;
	private JTextField txtFirstName;
	private JTextField txtLastName;
	private JButton btnSave;
	private JButton btnCancel;

	public PatientDetailView() {
		this(null);
	}

	public PatientDetailView(Patient patient) {
		initComponents();
		initPanel();

		setPatient(patient, true);
	}

	private void initComponents() {
		this.lblIdentification = new JLabel("-");
		this.txtFirstName = new JTextField();
		this.txtLastName = new JTextField();
		this.btnSave = new JButton("Save");
		this.btnSave.setActionCommand(PatientDetailController.ACTION_SAVE);
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setActionCommand(PatientDetailController.ACTION_CANCEL);
	}

	private void initPanel() {
		TitlePanel titlePanel = new TitlePanel();
		titlePanel.setTitle("Patient details");
		titlePanel.setDescription("Change a patients details" + " and add items to the schedule");

		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(new MigLayout("", "[][200px]", "[][][]"));
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
		add(titlePanel, BorderLayout.NORTH);
		add(panelCenter, BorderLayout.CENTER);
		add(panelButton, BorderLayout.SOUTH);
	}

	protected BindingGroup initDataBindings() {
		BeanProperty<Patient, String> pIdentification = BeanProperty.create("identification");
		BeanProperty<Patient, String> pFirstName = BeanProperty.create("firstName");
		BeanProperty<Patient, String> pLastName = BeanProperty.create("lastName");
		BeanProperty<JLabel, String> pLabelText = BeanProperty.create("text");
		BeanProperty<JTextField, String> pTextFieldText = BeanProperty.create("text");

		BindingGroup bindingGroup = new BindingGroup();
		bindingGroup.addBinding(Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ,
		        patient, pIdentification, lblIdentification, pLabelText));
		bindingGroup.addBinding(Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE,
		        patient, pFirstName, txtFirstName, pTextFieldText));
		bindingGroup.addBinding(Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE,
		        patient, pLastName, txtLastName, pTextFieldText));
		bindingGroup.bind();

		return bindingGroup;
	}
	
	public void setListener(PatientDetailController listener) {
		this.btnSave.addActionListener(listener);
		this.btnCancel.addActionListener(listener);
	}

	public nl.boxlab.model.Patient getPatient() {
		return patient;
	}

	public void setPatient(nl.boxlab.model.Patient newPatient) {
		setPatient(newPatient, true);
	}

	public void setPatient(nl.boxlab.model.Patient newPatient, boolean update) {
		patient = newPatient;
		if (update) {
			if (m_bindingGroup != null) {
				m_bindingGroup.unbind();
				m_bindingGroup = null;
			}
			if (patient != null) {
				m_bindingGroup = initDataBindings();
			}
		}
	}
}
