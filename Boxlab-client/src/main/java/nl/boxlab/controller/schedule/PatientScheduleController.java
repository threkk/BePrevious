package nl.boxlab.controller.schedule;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;

import nl.boxlab.ClientContext;
import nl.boxlab.controller.exercise.ExerciseController;
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

	private PatientScheduleView scheduleView;
	private JDialog scheduleDialog;
	private ExerciseController exerciseController;

	private List<ExerciseEntry> entries;
	private List<Message> messages;

	public PatientScheduleController(ClientContext context) {
		this.context = context;
		this.scheduleView = new PatientScheduleView();
		this.exerciseController = new ExerciseController(context);
	}

	public void showScheduleView(Component owner, Patient patient) {
		ExerciseEntryProvider entryProvider = this.context
				.getExerciseEntryProvider();
		MessageProvider messageProvider = this.context.getMessageProvider();
		
		this.entries = entryProvider.getEntries(patient.getIdentification());
		this.messages = messageProvider.getMessages();

		this.patient = patient;
		this.scheduleView.setEntries(entries);
		this.scheduleView.setMessages(messages);
		this.scheduleView.setListener(this);
		this.scheduleDialog = new JDialog();
		this.scheduleDialog.setTitle("Showing patient schedule");
		this.scheduleDialog.setContentPane(scheduleView);
		this.scheduleDialog.pack();
		this.scheduleDialog.setModal(true);
		this.scheduleDialog.setLocationRelativeTo(owner);
		this.scheduleDialog.setVisible(true);
	}

	public void hideView() {
		this.scheduleDialog.dispose();
		this.scheduleDialog = null;
		this.scheduleView.removeListener(this);
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
		Date date = this.scheduleView.getSelectedDate();

		if (date == null) {
			return;
		}

		ExerciseEntry selected = getEntry(date);
		if (ACTION_ADD_EXERCISE.equals(e.getActionCommand())) {
			ExerciseEntry entry = new ExerciseEntry();
			entry.setDate(date);

			this.exerciseController.showView(scheduleDialog, entry);
			if (!this.exerciseController.isCancelled()) {
				this.entries.add(entry);
			}
			this.scheduleView.updateView();
		} else if (ACTION_EDIT_EXERCISE.equals(e.getActionCommand())) {
			if (selected == null) {
				return;
			}

			this.exerciseController.showView(scheduleDialog, selected);
			this.scheduleView.updateView();
		} else if (ACTION_REMOVE_EXERCISE.equals(e.getActionCommand())) {
			this.entries.remove(selected);
			this.scheduleView.updateView();
		} else if (ACTION_CLOSE.equals(e.getActionCommand())) {
			hideView();
		}

		this.scheduleView.setEntries(entries);
		this.scheduleView.setMessages(messages);
	}
}
