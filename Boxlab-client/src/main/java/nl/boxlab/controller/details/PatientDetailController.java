package nl.boxlab.controller.details;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import nl.boxlab.ClientContext;
import nl.boxlab.controller.Command;
import nl.boxlab.model.Patient;
import nl.boxlab.view.details.PatientDetailDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientDetailController implements ActionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(PatientDetailController.class);

	public static final String ACTION_SAVE = "save";
	public static final String ACTION_CANCEL = "cancel";

	private ClientContext context;
	private PatientDetailDialog view;
	private Command saveCommand;

	public PatientDetailController(ClientContext context) {
		this.context = context;

		this.view = new PatientDetailDialog();
		this.view.pack();
		this.view.setListener(this);
	}

	public void showView(Component owner, Patient patient, Command saveCommand) {
		this.saveCommand = saveCommand;
		this.view.setLocationRelativeTo(owner);
		this.view.setPatient(patient);
		this.view.setVisible(true);
	}

	public void hideView() {
		this.saveCommand = null;
		this.view.setPatient(null);
		this.view.setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (ACTION_SAVE.equals(e.getActionCommand())) {
			try {
				context.getPatientProvider().save(this.view.getPatient());
				saveCommand.execute();
				hideView();
			} catch (Exception ex) {
				logger.error("failed to save patient to the database", ex);
				String localizedMessage = ex.getLocalizedMessage();
				String message = "Failed to save the patient to the database\n";
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
