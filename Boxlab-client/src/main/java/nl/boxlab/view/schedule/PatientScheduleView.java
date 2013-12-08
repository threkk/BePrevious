package nl.boxlab.view.schedule;

import static nl.boxlab.controller.schedule.PatientScheduleController.ACTION_ADD_EXERCISE;
import static nl.boxlab.controller.schedule.PatientScheduleController.ACTION_CLOSE;
import static nl.boxlab.controller.schedule.PatientScheduleController.ACTION_EDIT_EXERCISE;
import static nl.boxlab.controller.schedule.PatientScheduleController.ACTION_NEXT_MONTH;
import static nl.boxlab.controller.schedule.PatientScheduleController.ACTION_NEXT_YEAR;
import static nl.boxlab.controller.schedule.PatientScheduleController.ACTION_PREV_MONTH;
import static nl.boxlab.controller.schedule.PatientScheduleController.ACTION_PREV_YEAR;
import static nl.boxlab.controller.schedule.PatientScheduleController.ACTION_REMOVE_EXERCISE;
import static nl.boxlab.controller.schedule.PatientScheduleController.ACTION_SHOW_ENVIRONMENT;
import static nl.boxlab.controller.schedule.PatientScheduleController.ACTION_SHOW_MESSAGES;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.controller.schedule.PatientScheduleController;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.Message;
import nl.boxlab.resources.Images;

@SuppressWarnings("serial")
public class PatientScheduleView extends JPanel {

	private JButton btnPrevMonth;
	private JButton btnPrevYear;
	private JButton btnNextMonth;
	private JButton btnNextYear;
	private JLabel lblDate;

	private JButton btnAddExercise;
	private JButton btnEditExercise;
	private JButton btnRemoveExercise;
	private JButton btnShowMessages;
	private JButton btnShowEnvironment;

	private CalendarView calendarView;
	private JButton btnClose;

	public PatientScheduleView() {
		initComponents();
		initPanel();

		updateView();
	}

	private void initComponents() {
		this.lblDate = new JLabel("-");

		this.btnPrevMonth = new JButton("Previous month", Images.ICONS_PREVIOUS_SMALL);
		this.btnPrevMonth.setActionCommand(ACTION_PREV_MONTH);
		this.btnPrevYear = new JButton("Previous year", Images.ICONS_PREVIOUS_2_SMALL);
		this.btnPrevYear.setActionCommand(ACTION_PREV_YEAR);
		this.btnNextMonth = new JButton("Next month", Images.ICONS_FORWARD_SMALL);
		this.btnNextMonth.setActionCommand(ACTION_NEXT_MONTH);
		this.btnNextYear = new JButton("Next year", Images.ICONS_FORWARD_2_SMALL);
		this.btnNextYear.setActionCommand(ACTION_NEXT_YEAR);

		this.btnAddExercise = new JButton("Add exercise", Images.ICONS_ADD_SMAL);
		this.btnAddExercise.setActionCommand(ACTION_ADD_EXERCISE);
		this.btnEditExercise = new JButton("Edit exercise", Images.ICONS_EDIT_SMALL);
		this.btnEditExercise.setActionCommand(ACTION_EDIT_EXERCISE);
		this.btnRemoveExercise = new JButton("Remove exercise", Images.ICONS_DELETE_SMALL);
		this.btnRemoveExercise.setActionCommand(ACTION_REMOVE_EXERCISE);

		this.btnShowMessages = new JButton("Show messages", Images.ICONS_MESSAGE_SMALL);
		this.btnShowMessages.setActionCommand(ACTION_SHOW_MESSAGES);
		this.btnShowEnvironment = new JButton("Show environment", Images.ICONS_GRAPH_SMALL);
		this.btnShowEnvironment.setActionCommand(ACTION_SHOW_ENVIRONMENT);

		this.btnClose = new JButton("Close");
		this.btnClose.setActionCommand(ACTION_CLOSE);

		this.calendarView = new CalendarView();
	}

	private void initPanel() {
		JPanel panelControls = new JPanel();
		panelControls.setLayout(new MigLayout("",
		        "[100px][100px:n,grow][41px]", "[23px][]"));
		panelControls.add(btnPrevMonth, "cell 0 0,alignx left,aligny top");
		panelControls.add(btnNextMonth, "cell 2 0,alignx left,aligny top");
		panelControls.add(lblDate, "cell 1 0 1 2,alignx center");
		panelControls.add(btnPrevYear, "cell 0 1,growx,aligny top");
		panelControls.add(btnNextYear, "cell 2 1,growx,aligny top");

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(btnAddExercise);
		toolBar.add(btnEditExercise);
		toolBar.add(btnRemoveExercise);
		toolBar.add(btnShowMessages);
		toolBar.add(btnShowEnvironment);

		JPanel panelDetails = new JPanel();

		setLayout(new MigLayout("", "[700px:n,grow][300px:n,grow]",
		        "[][][grow][]"));
		add(panelControls, "cell 0 0,grow");
		add(toolBar, "flowx,cell 0 1");
		add(calendarView, "cell 0 2,grow");
		add(panelDetails, "cell 1 2,grow");
		add(btnClose, "cell 1 3,alignx trailing");
	}

	public void updateView() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, this.calendarView.getYear());
		cal.set(Calendar.MONTH, this.calendarView.getMonth());

		String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
		        Locale.getDefault());
		this.lblDate.setText(monthName + " " + this.calendarView.getYear());
		this.calendarView.updateView();
	}

	public List<ExerciseEntry> getEntries() {
		return this.calendarView.getEntries();
	}

	public void setEntries(List<ExerciseEntry> entries) {
		this.calendarView.setEntries(entries);
	}

	public void setMessages(List<Message> messages) {
		this.calendarView.setMessages(messages);
	}

	public Date getSelectedDate() {
		return this.calendarView.getSelectedDate();
	}

	public void setListener(PatientScheduleController listener) {
		this.btnPrevMonth.addActionListener(listener);
		this.btnPrevYear.addActionListener(listener);
		this.btnNextMonth.addActionListener(listener);
		this.btnNextYear.addActionListener(listener);

		this.btnAddExercise.addActionListener(listener);
		this.btnEditExercise.addActionListener(listener);
		this.btnRemoveExercise.addActionListener(listener);

		this.btnShowMessages.addActionListener(listener);
		this.btnShowEnvironment.addActionListener(listener);
		this.btnClose.addActionListener(listener);
	}

	public void removeListener(PatientScheduleController listener) {
		this.btnPrevMonth.removeActionListener(listener);
		this.btnPrevYear.removeActionListener(listener);
		this.btnNextMonth.removeActionListener(listener);
		this.btnNextYear.removeActionListener(listener);

		this.btnAddExercise.removeActionListener(listener);
		this.btnEditExercise.removeActionListener(listener);
		this.btnRemoveExercise.removeActionListener(listener);
		this.btnClose.removeActionListener(listener);
	}

	public CalendarView getCalendarView() {
		return calendarView;
	}

	public void setCalendarView(CalendarView calendarView) {
		this.calendarView = calendarView;
	}

}
