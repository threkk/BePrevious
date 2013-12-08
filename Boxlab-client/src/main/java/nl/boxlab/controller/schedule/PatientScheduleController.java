package nl.boxlab.controller.schedule;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.boxlab.ClientContext;
import nl.boxlab.DateUtilities;
import nl.boxlab.MessageUtilities;
import nl.boxlab.controller.exercise.ExerciseController;
import nl.boxlab.controller.message.MessageController;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.Message;
import nl.boxlab.model.Patient;
import nl.boxlab.remote.ExerciseEntryProvider;
import nl.boxlab.remote.MessageProvider;
import nl.boxlab.view.DialogBuilder;
import nl.boxlab.view.schedule.CalendarView;
import nl.boxlab.view.schedule.PatientScheduleView;

public class PatientScheduleController implements ActionListener {

	private static final Logger logger = LoggerFactory.getLogger(PatientScheduleController.class);

	public static final String ACTION_PREV_MONTH = "prev-month";
	public static final String ACTION_PREV_YEAR = "prev-year";
	public static final String ACTION_NEXT_MONTH = "next-month";
	public static final String ACTION_NEXT_YEAR = "next-year";

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

	private List<Message> messages;

	public PatientScheduleController(ClientContext context) {
		this.context = context;
		this.view = new PatientScheduleView();

		this.exerciseController = new ExerciseController(context);
		this.messageController = new MessageController(context);
	}

	public void showView(Component owner, Patient patient) {

		MessageProvider messageProvider = this.context.getMessageProvider();

		this.messages = messageProvider.getMessages(patient.getIdentification());
		this.patient = patient;
		this.view.setMessages(messages);
		this.view.setListener(this);

		updateEntries();

		this.dialog = new DialogBuilder()
		        .setTitle("Showing patient schedule")
		        .setView(view)
		        .setOwner(owner).build();
		this.dialog.setVisible(true);
	}

	public void hideView() {
		this.dialog.dispose();
		this.dialog = null;
		this.view.removeListener(this);
	}

	private ExerciseEntry getEntry(Date date) {
		for (ExerciseEntry entry : this.view.getEntries()) {
			if (entry.getDate().equals(date)) {
				return entry;
			}
		}
		return null;
	}

	private void addExercise(Date date) {
		ExerciseEntry entry = new ExerciseEntry();
		entry.setDate(date);
		entry.setIdentification(this.patient.getIdentification());

		this.exerciseController.showView(dialog, entry);
		if (this.exerciseController.isCancelled()) {
			return;
		}

		ExerciseEntryProvider provider = this.context.getExerciseEntryProvider();
		try {
			provider.save(entry);
			this.view.getEntries().add(entry);
			this.view.updateView();
		} catch (Exception e) {
			logger.error("Failed to add entry", e);
			MessageUtilities.showErrorMessage(dialog, "Failed to add exercise entry", e);
		}
	}

	private void editExercise(ExerciseEntry exercise) {
		if (exercise == null) {
			return;
		}

		this.exerciseController.showView(dialog, exercise);
		if (this.exerciseController.isCancelled()) {
			return;
		}

		ExerciseEntryProvider provider = this.context.getExerciseEntryProvider();
		try {
			provider.save(exercise);
			this.view.updateView();
		} catch (Exception e) {
			logger.error("Failed to edit entry", e);
			MessageUtilities.showErrorMessage(dialog, "Failed to edit exercise entry", e);
		}

	}

	private void removeExercise(ExerciseEntry exercise) {
		if (exercise == null) {
			return;
		}

		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String formattedDate = dateFormat.format(exercise.getDate());
		String message = "You are about to remove an exercise for the following date:"
		        + "\n" + formattedDate
		        + "\n\nAre you sure you want to continue?";
		if (!MessageUtilities.showConfirmMessage(this.view, message)) {
			return;
		}

		ExerciseEntryProvider provider = this.context.getExerciseEntryProvider();
		try {
			this.view.getEntries().remove(exercise);
			provider.delete(exercise);
			this.view.updateView();
		} catch (Exception e) {
			logger.error("Failed to remove entry", e);
			MessageUtilities.showErrorMessage(dialog, "Failed to edit remove entry", e);
		}
	}

	private void updateEntries() {
		CalendarView calendarView = this.view.getCalendarView();
		int month = calendarView.getMonth();
		int year = calendarView.getYear();
		Date start = DateUtilities.getStartOfMonth(month, year);
		Date end = DateUtilities.getEndOfMonth(month, year);

		ExerciseEntryProvider provider = this.context.getExerciseEntryProvider();
		List<ExerciseEntry> entries = provider.getEntries(this.patient.getIdentification(), start, end);
		this.view.setEntries(entries);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Date date = this.view.getSelectedDate();

		if (date != null) {
			ExerciseEntry selected = getEntry(date);
			if (ACTION_ADD_EXERCISE.equals(e.getActionCommand())) {
				addExercise(date);
			} else if (ACTION_EDIT_EXERCISE.equals(e.getActionCommand())) {
				editExercise(selected);
			} else if (ACTION_REMOVE_EXERCISE.equals(e.getActionCommand())) {
				removeExercise(selected);
			}
		}

		if (ACTION_SHOW_MESSAGES.equals(e.getActionCommand())) {
			this.messageController.showView(dialog, patient);
		} else if (ACTION_CLOSE.equals(e.getActionCommand())) {
			hideView();
		}

		if (ACTION_PREV_MONTH.equals(e.getActionCommand())) {
			this.view.getCalendarView().previousMonth();
			this.view.updateView();
			updateEntries();
		} else if (ACTION_PREV_YEAR.equals(e.getActionCommand())) {
			this.view.getCalendarView().previousYear();
			updateEntries();
			this.view.updateView();
		} else if (ACTION_NEXT_MONTH.equals(e.getActionCommand())) {
			this.view.getCalendarView().nextMonth();
			updateEntries();
			this.view.updateView();
		} else if (ACTION_NEXT_YEAR.equals(e.getActionCommand())) {
			this.view.getCalendarView().nextYear();
			updateEntries();
			this.view.updateView();
		}

		this.view.setMessages(messages);
	}
}
