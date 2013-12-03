package nl.boxlab.controller.schedule;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;

import nl.boxlab.ClientContext;
import nl.boxlab.controller.exercise.ExerciseController;
import nl.boxlab.controller.message.MessageController;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.Message;
import nl.boxlab.model.Patient;
import nl.boxlab.remote.ExerciseEntryProvider;
import nl.boxlab.remote.MessageProvider;
import nl.boxlab.view.schedule.PatientScheduleView;

public class PatientScheduleController implements ActionListener {

	public static final String ACTION_ADD_EXERCISE = "add-exercise";
	public static final String ACTION_EDIT_EXERCISE = "edit-exercise";
	public static final String ACTION_REMOVE_EXERCISE = "delete-exercise";
	public static final String ACTION_SHOW_MESSAGES = "show-messages";
	public static final String ACTION_SHOW_ENVIRONMENT = "show-environment";
	public static final String ACTION_CLOSE = "close";

	private ClientContext context;
	private Patient patient;

	private PatientScheduleView view;
	private JDialog dialog;

	private ExerciseController exerciseController;
	private MessageController messageController;

	private List<ExerciseEntry> entries;
	private List<Message> messages;

	public PatientScheduleController(ClientContext context) {
		this.context = context;
		this.view = new PatientScheduleView();

		this.exerciseController = new ExerciseController(context);
		this.messageController = new MessageController(context);
	}

	public void showView(Component owner, Patient patient) {
		ExerciseEntryProvider entryProvider = this.context
		        .getExerciseEntryProvider();
		MessageProvider messageProvider = this.context.getMessageProvider();

		this.entries = entryProvider.getEntries(patient.getIdentification());
		this.messages = messageProvider.getMessages(patient.getIdentification());

		this.patient = patient;
		this.view.setEntries(entries);
		this.view.setMessages(messages);
		this.view.setListener(this);
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
		this.view.removeListener(this);
	}

	private ExerciseEntry getEntry(Date date) {
		for (ExerciseEntry entry : this.entries) {
			if (entry.getDate().equals(date)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Date date = this.view.getSelectedDate();

		if (date == null) {
			return;
		}

		ExerciseEntry selected = getEntry(date);
		if (ACTION_ADD_EXERCISE.equals(e.getActionCommand())) {
			ExerciseEntry entry = new ExerciseEntry();
			entry.setDate(date);

			this.exerciseController.showView(dialog, entry);
			if (!this.exerciseController.isCancelled()) {
				this.entries.add(entry);
			}
			this.view.updateView();
		} else if (ACTION_EDIT_EXERCISE.equals(e.getActionCommand())) {
			if (selected == null) {
				return;
			}

			this.exerciseController.showView(dialog, selected);
			this.view.updateView();
		} else if (ACTION_REMOVE_EXERCISE.equals(e.getActionCommand())) {
			this.entries.remove(selected);
			this.view.updateView();
		} else if (ACTION_SHOW_MESSAGES.equals(e.getActionCommand())) {
			this.messageController.showView(dialog, patient);
		} else if (ACTION_CLOSE.equals(e.getActionCommand())) {
			hideView();
		}

		this.view.setEntries(entries);
		this.view.setMessages(messages);
	}
}
