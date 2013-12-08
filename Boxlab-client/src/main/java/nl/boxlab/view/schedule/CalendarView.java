package nl.boxlab.view.schedule;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.Message;

@SuppressWarnings("serial")
public class CalendarView extends JPanel {

	private List<ExerciseEntry> entries = new ArrayList<>();
	private List<Message> messages = new ArrayList<>();

	private CalendarItemModel model;
	private JTable table;

	private int year;
	private int month;

	public CalendarView() {
		Calendar calendar = Calendar.getInstance();

		this.year = calendar.get(Calendar.YEAR);
		this.month = calendar.get(Calendar.MONTH);

		initComponents();
		initPanel();
	}

	private void initComponents() {
		this.table = new JTable(this.model = new CalendarItemModel());
		this.table.setDefaultRenderer(CalendarItem.class,
		        new CalendarItemTableCellRenderer());
		this.table.getSelectionModel().setSelectionMode(
		        ListSelectionModel.SINGLE_SELECTION);
		this.table.getTableHeader().setReorderingAllowed(false);
		this.table.getTableHeader().setResizingAllowed(false);
		this.table.setIntercellSpacing(new Dimension(0, 0));
		this.table.setShowHorizontalLines(false);
		this.table.setShowVerticalLines(false);
		this.table.setRowHeight(64);
	}

	private void initPanel() {
		JPanel panelHeader = new JPanel();
		panelHeader.setLayout(new BorderLayout(0, 0));

		setLayout(new BorderLayout(0, 0));
		add(panelHeader, BorderLayout.NORTH);
		add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
		        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

		updateView();
	}

	public List<ExerciseEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<ExerciseEntry> entries) {
		this.entries = entries;
		updateView();
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
		if (messages != null) {
			updateView();
		}
	}

	public void updateView() {
		this.model.updateModel(entries, messages, month, year);
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public Date getSelectedDate() {
		int row = this.table.getSelectedRow();
		int column = this.table.getSelectedColumn();
		if (row < 0 && column < 0) {
			return null;
		}

		CalendarItem item = (CalendarItem) this.model.getValueAt(row, column);
		if (item != null) {
			return item.getDate();
		}

		return null;
	}

	public void previousMonth() {
		if (this.month == Calendar.JANUARY) {
			this.month = Calendar.DECEMBER;
			this.year--;
		} else {
			this.month--;
		}
		updateView();
	}

	public void nextMonth() {
		if (this.month == Calendar.DECEMBER) {
			this.month = Calendar.JANUARY;
			this.year++;
		} else {
			this.month++;
		}
		updateView();
	}

	public void previousYear() {
		this.year--;
		updateView();
	}

	public void nextYear() {
		this.year++;
		updateView();
	}
}
