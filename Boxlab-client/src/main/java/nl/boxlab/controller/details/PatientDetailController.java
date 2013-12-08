package nl.boxlab.controller.details;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import nl.boxlab.ClientContext;
import nl.boxlab.ModelUtilities;
import nl.boxlab.model.Patient;
import nl.boxlab.view.DialogBuilder;
import nl.boxlab.view.details.PatientDetailView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientDetailController implements ActionListener {

	private static final Logger logger = LoggerFactory
	        .getLogger(PatientDetailController.class);

	public static final String ACTION_SAVE = "save";
	public static final String ACTION_CANCEL = "cancel";

	private ClientContext context;
	private PatientDetailView view;
	private JDialog dialog;

	private Patient patient;

	public PatientDetailController(ClientContext context) {
		this.context = context;
		this.view = new PatientDetailView();
		this.view.setListener(this);
	}

	public void showView(Component owner, Patient patient) {
		this.patient = patient;
		this.view.setPatient(ModelUtilities.deepClone(patient));
		this.dialog = new DialogBuilder()
		        .setTitle("Showing patient details")
		        .setView(view)
		        .setOwner(owner).build();
		this.dialog.setVisible(true);
	}

	public void hideView() {
		this.dialog.dispose();
		this.dialog = null;
		this.view.setPatient(null);
	}

	public void actionPerformed(ActionEvent e) {
		if (ACTION_SAVE.equals(e.getActionCommand())) {
			try {
				ModelUtilities.merge(this.view.getPatient(), this.patient);
				context.getPatientProvider().save(this.patient);

				hideView();
			} catch (Exception ex) {
				String localizedMessage = ex.getLocalizedMessage();
				String message = "Failed to save the patient to the database\n";
				logger.error(message, ex);
				if (localizedMessage != null) {
					message += localizedMessage;
				}
				JOptionPane.showMessageDialog(view, message,
				        "An exception occured", JOptionPane.ERROR_MESSAGE);
			}
		} else if (ACTION_CANCEL.equals(e.getActionCommand())) {
			hideView();
		}
	}
}
