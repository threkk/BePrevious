package nl.boxlab.view.schedule;

import java.util.List;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.model.ExerciseEntry;

@SuppressWarnings("serial")
public class PatientScheduleView extends JPanel {

	private CalendarView calendarView;

	public PatientScheduleView() {
		initComponents();
		initPanel();
	}

	private void initComponents() {
		this.calendarView = new CalendarView();
	}

	private void initPanel() {

		JPanel panelDetails = new JPanel();

		setLayout(new MigLayout("", "[700px:n,grow][300px:n,grow]", "[][grow]"));
		add(calendarView, "cell 0 1,grow");
		add(panelDetails, "cell 1 1,grow");
	}

	public void setEntries(List<ExerciseEntry> entries) {
		this.calendarView.setEntries(entries);
	}

}
