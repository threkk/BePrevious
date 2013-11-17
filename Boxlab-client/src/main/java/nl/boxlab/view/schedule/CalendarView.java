package nl.boxlab.view.schedule;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.model.ExerciseEntry;

@SuppressWarnings("serial")
public class CalendarView extends JPanel implements ActionListener {

	private JToggleButton btnMonth;
	private JToggleButton btnWeek;

	private List<ExerciseEntry> entries = new ArrayList<ExerciseEntry>();

	private CalendarItemModel model;
	private JTable table;

	private JButton btnPrevMonth;
	private JButton btnPrevYear;
	private JButton btnNextMonth;
	private JButton btnNextYear;
	private JLabel lblDate;

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
		ButtonGroup group = new ButtonGroup();
		group.add(this.btnMonth = new JToggleButton("Month", true));
		group.add(this.btnWeek = new JToggleButton("Week"));

		this.table = new JTable(this.model = new CalendarItemModel());
		this.table.setDefaultRenderer(CalendarItem.class, new CalendarItemTableCellRenderer());
		this.table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.getTableHeader().setReorderingAllowed(false);
		this.table.getTableHeader().setResizingAllowed(false);
		this.table.setIntercellSpacing(new Dimension(0,0));
		this.table.setShowHorizontalLines(false);
		this.table.setShowVerticalLines(false);
		this.table.setRowHeight(64);

		this.lblDate = new JLabel("-");
		this.btnPrevMonth = new JButton("Previous month");
		this.btnPrevMonth.addActionListener(this);
		this.btnPrevYear = new JButton("Previous year");
		this.btnPrevYear.addActionListener(this);
		this.btnNextMonth = new JButton("Next month");
		this.btnNextMonth.addActionListener(this);
		this.btnNextYear = new JButton("Next year");
		this.btnNextYear.addActionListener(this);
	}

	private void initPanel() {
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new MigLayout("", "[63px][59px]", "[23px]"));
		panelButtons.add(btnMonth, "cell 0 0,alignx left,aligny top");
		panelButtons.add(btnWeek, "cell 1 0,alignx left,aligny top");

		JPanel panelControls = new JPanel();
		panelControls.setLayout(new MigLayout("", "[107px][100px:n,grow][41px]", "[23px][]"));
		panelControls.add(btnPrevMonth, "cell 0 0,alignx left,aligny top");
		panelControls.add(btnNextMonth, "cell 2 0,alignx left,aligny top");
		panelControls.add(lblDate, "cell 1 0 1 2,alignx center");
		panelControls.add(btnPrevYear, "cell 0 1,growx,aligny top");
		panelControls.add(btnNextYear, "cell 2 1,growx,aligny top");

		JPanel panelHeader = new JPanel();
		panelHeader.setLayout(new BorderLayout(0, 0));
		panelHeader.add(panelButtons, BorderLayout.EAST);
		panelHeader.add(panelControls, BorderLayout.CENTER);

		setLayout(new BorderLayout(0, 0));
		add(panelHeader, BorderLayout.NORTH);
		add(new JScrollPane(table,
		        JScrollPane.VERTICAL_SCROLLBAR_NEVER,
		        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

		updateView();
	}

	private void updateView() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);

		String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		lblDate.setText(monthName + " " + year);

		model.updateModel(entries, month, year);
	}

	public List<ExerciseEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<ExerciseEntry> entries) {
		this.entries = entries;
		updateView();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnPrevMonth) {
			if (month == Calendar.JANUARY) {
				month = Calendar.DECEMBER;
				year--;
			} else {
				month--;
			}
		} else if (e.getSource() == btnNextMonth) {
			if (month == Calendar.DECEMBER) {
				month = Calendar.JANUARY;
				year++;
			} else {
				month++;
			}
		} else if (e.getSource() == btnPrevYear) {
			year--;
		} else if (e.getSource() == btnNextYear) {
			year++;
		}
		updateView();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(new CalendarView());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
