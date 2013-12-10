package nl.boxlab.view.schedule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.DateUtilities;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.Message;
import nl.boxlab.resources.Images;

public class CalendarItemTableCellRenderer implements TableCellRenderer {

	public static final Font FONT_PLAIN = new Font("Arial", Font.PLAIN, 11);
	public static final Font FONT_BOLD = new Font("Arial", Font.BOLD, 11);

	public static final Color COLOR_BACKGROUND_USABLE = Color.WHITE;
	public static final Color COLOR_BACKGROUND_UNUSABLE = Color.LIGHT_GRAY;
	public static final Color COLOR_BACKGROUND_SELECTED = new Color(100, 185,
			255);
	public static final Color COLOR_BACKGROUND_TODAY = new Color(200, 215, 255);

	private final Calendar cal = Calendar.getInstance();
	private JPanel panel;
	private JLabel lblDayOfMonth;
	private JLabel lblNotes;
	private JLabel lblNotDone;
	private JLabel lblDone;

	public CalendarItemTableCellRenderer() {
		this.panel = new JPanel();
		this.panel
				.setLayout(new MigLayout("insets 0 0 0 0", "[grow]", "[][][]"));
		this.panel.setBorder(BorderFactory.createEtchedBorder());

		this.lblDayOfMonth = new JLabel("-");
		this.lblDayOfMonth.setFont(FONT_BOLD);

		this.lblNotes = new JLabel();
		this.lblNotes.setFont(FONT_PLAIN);
		this.lblNotes.setIcon(Images.ICONS_MESSAGE_SMALL);

		this.lblNotDone = new JLabel("Not completed");
		this.lblNotDone.setFont(FONT_PLAIN);
		this.lblNotDone.setIcon(Images.ICONS_DELETE_SMALL);

		this.lblDone = new JLabel("Completed");
		this.lblDone.setFont(FONT_PLAIN);
		this.lblDone.setIcon(Images.ICONS_APPROVE_SMALL);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (!(value instanceof CalendarItem)) {
			return null;
		}

		// fix for jtables different notion of a cellected cell
		int selectedRow = table.getSelectedRow();
		int selectedColumn = table.getSelectedColumn();
		isSelected = (row == selectedRow && column == selectedColumn);

		CalendarItem item = (CalendarItem) value;
		if (!item.isUsable()) {
			this.panel.setBackground(COLOR_BACKGROUND_UNUSABLE);
		} else if (isSelected) {
			this.panel.setBackground(COLOR_BACKGROUND_SELECTED);
		} else {
			if (DateUtilities.equalDay(item.getDate(), new Date())) {
				this.panel.setBackground(COLOR_BACKGROUND_TODAY);
			} else {
				this.panel.setBackground(COLOR_BACKGROUND_USABLE);
			}
		}

		initPanelItem(item);
		if (item.getEntry() != null && item.isUsable()) {
			initPanelEntry(item);
		}

		return panel;
	}

	private void initPanelItem(CalendarItem item) {
		this.cal.setTime(item.getDate());
		this.lblDayOfMonth.setText("" + cal.get(Calendar.DAY_OF_MONTH));
		this.panel.removeAll();
		this.panel.add(lblDayOfMonth, "flowx,cell 0 0,alignx left");
	}

	private void initPanelEntry(CalendarItem item) {
		int unreadNotes = 0;
		ExerciseEntry entry = item.getEntry();
		List<Message> messages = item.getMessages();

		for (Message message : messages) {
			if (!message.isRead()) {
				unreadNotes++;
			}
		}

		int row = 1;
		if (unreadNotes > 0) {
			lblNotes.setText(unreadNotes + " unread");
			this.panel.add(lblNotes, "cell 0 " + row++);
		}
		if (entry.isDone()) {
			this.panel.add(lblDone, "cell 0 " + row++);
		} else {
			this.panel.add(lblNotDone, "cell 0 " + row++);
		}
	}

}
