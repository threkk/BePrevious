package nl.boxlab.view.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.boxlab.DateUtilities;
import nl.boxlab.model.ExerciseEntry;

@SuppressWarnings("serial")
public class CalendarItemModel extends DefaultTableModel {

	private static final String[] DAY_NAMES = {
	        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
	};

	private static final Logger logger = LoggerFactory.getLogger(CalendarItemModel.class);

	private List<CalendarItem> items;

	public CalendarItemModel() {
		this.items = new ArrayList<CalendarItem>();
	}

	public void updateModel(List<ExerciseEntry> entries, int month, int year) {
		int leadDays = DateUtilities.getLeadingDays(month, year);
		int trailDays = DateUtilities.getTrailingDays(month, year);

		Date start = DateUtilities.addDay(DateUtilities.getStartOfMonth(month, year), -leadDays);
		Date end = DateUtilities.addDay(DateUtilities.getEndOfMonth(month, year), trailDays);

		this.items.clear();
		Date current = start;
		while (!current.after(end)) {
			CalendarItem item = new CalendarItem();
			item.setUsable(DateUtilities.getMonth(current) == month);
			item.setDate(current);

			for (ExerciseEntry entry : entries) {
				if (DateUtilities.equalDay(entry.getDate(), item.getDate())) {
					logger.debug("entry found on {}", entry.getDate());
					item.setEntry(entry);
					break;
				}
			}

			this.items.add(item);
			current = DateUtilities.addDay(current, 1);
		}

		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return DAY_NAMES.length;
	}

	@Override
	public String getColumnName(int column) {
		return DAY_NAMES[column];
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (this.items.isEmpty()) {
			return null;
		} else {
			return this.items.get(column + (row * 7));
		}
	}

	@Override
	public int getRowCount() {
		if (this.items == null) {
			return 0;
		} else {
			return this.items.size() / 7;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return CalendarItem.class;
	}
}
