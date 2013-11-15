package nl.boxlab.table;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

class TableViewModel<T> extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5231159368802378983L;

	private static boolean isShiftDown() {
		int modifiers = 0;
		AWTEvent currentEvent = EventQueue.getCurrentEvent();
		if (currentEvent instanceof InputEvent) {
			modifiers = ((InputEvent) currentEvent).getModifiers();
		} else if (currentEvent instanceof ActionEvent) {
			modifiers = ((ActionEvent) currentEvent).getModifiers();
		}
		return (modifiers & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK;
	}

	private final TableController<T> controller;

	private List<T> rowData = new ArrayList<T>();

	/**
    *
    */
	public TableViewModel(TableController<T> controller) {
		this.controller = controller;
	}

	public void add(T rowData) {
		this.rowData.add(rowData);

		// notify the table that data has been changed
		int firstIndex = this.rowData.size();
		super.fireTableRowsInserted(firstIndex - 1, firstIndex - 1);
	}

	public void addAll(Collection<? extends T> rowData) {
		if (rowData.isEmpty()) {
			return;
		}
		int firstRow = this.rowData.size();
		this.rowData.addAll(rowData);
		int lastRow = this.rowData.size() - 1;

		// notify the table that data has been changed
		super.fireTableRowsInserted(firstRow, lastRow);
	}

	private void checkRange(int rowIndex) {
		if (rowIndex < 0 || rowIndex >= rowData.size()) {
			throw new IndexOutOfBoundsException("row index " + rowIndex
					+ "is out of bounds");
		}
	}

	public void clear() {
		this.rowData.clear();
		super.fireTableDataChanged();
	}

	public boolean contains(T rowData) {
		return this.rowData.contains(rowData);
	}

	/*
	 * (non-Javadoc) @see
	 * javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int column) {
		List<ColumnDefinition> columnDefinitions = controller
				.getVisibleColumns();
		ColumnDefinition def = columnDefinitions.get(column);
		return def.getValueClass();
	}

	/*
	 * (non-Javadoc) @see javax.swing.table.TableModel#getColumnCount()
	 */

	public int getColumnCount() {
		return controller.getVisibleColumns().size();
	}

	/*
	 * (non-Javadoc) @see
	 * javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		List<ColumnDefinition> columnDefinitions = controller
				.getVisibleColumns();
		ColumnDefinition def = columnDefinitions.get(column);
		return def.getTitle();
	}

	public T getRowAt(int rowIndex) {
		checkRange(rowIndex);

		return this.rowData.get(rowIndex);
	}

	public int getRowCount() {
		return this.rowData.size();
	}

	public int getRowOf(T rowData) {
		return this.rowData.indexOf(rowData);
	}

	public Object getValueAt(int row, int column) {
		List<ColumnDefinition> columnDefinitions = controller
				.getVisibleColumns();
		ColumnDefinition def = columnDefinitions.get(column);
		Object data = this.rowData.get(row);

		return def.getValue(data);
	}

	/*
	 * (non-Javadoc) @see
	 * javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int column) {
		List<ColumnDefinition> columnDefinitions = controller
				.getVisibleColumns();
		ColumnDefinition def = columnDefinitions.get(column);
		return def.isEditable();
	}

	public boolean remove(int rowIndex) {
		checkRange(rowIndex);
		if (this.rowData.remove(rowIndex) != null) {
			super.fireTableRowsDeleted(rowIndex, rowIndex);
			return true;
		}

		return false;
	}

	public boolean remove(T rowData) {
		int rowIndex = this.rowData.indexOf(rowData);
		if (rowIndex >= 0) {
			return remove(rowIndex);
		}
		return false;
	}

	public void replace(T oldData, T newData) {
		int rowIndex = this.rowData.indexOf(oldData);

		if (rowIndex >= 0) {
			update(rowIndex, newData);
		}
	}

	public void set(Collection<? extends T> rowData) {
		this.rowData = new ArrayList<T>(rowData);
		super.fireTableDataChanged();
	}

	private void setRange(Object value, int column, int curRow, int prevRow) {
		ColumnDefinition def = controller.getVisibleColumns().get(column);

		// go from model indices to view
		int viewRow1 = this.controller.getInstalledTable()
				.convertRowIndexToView(curRow);
		int viewRow2 = this.controller.getInstalledTable()
				.convertRowIndexToView(prevRow);

		// calculate the indexes to iterate
		int viewFrom = Math.min(viewRow1, viewRow2);
		int viewTo = Math.max(viewRow1, viewRow2);

		for (int viewIndex = viewFrom; viewIndex <= viewTo; viewIndex++) {

			// go from view index to model
			int modelIndex = this.controller.getInstalledTable()
					.convertRowIndexToModel(viewIndex);

			// set value and update
			def.setValue(this.rowData.get(modelIndex), value);
			fireTableCellUpdated(modelIndex, column);
		}
	}

	/*
	 * (non-Javadoc) @see
	 * javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int,
	 * int)
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {
		List<ColumnDefinition> columnDefinitions = controller
				.getVisibleColumns();
		ColumnDefinition def = columnDefinitions.get(column);

		if (Boolean.class.equals(def.getValueClass())) { // checkbox
			if (isShiftDown() && def.getLastCheckedRow() >= 0) {
				setRange(value, column, row, def.getLastCheckedRow());
			}
			def.setLastCheckedRow(row);
		}

		Object data = this.rowData.get(row);
		def.setValue(data, value);
	}

	public void update(int rowIndex, T rowData) {
		checkRange(rowIndex);
		this.rowData.set(rowIndex, rowData);
		super.fireTableRowsUpdated(rowIndex, rowIndex);
	}

	public void update(T rowData) {
		int rowIndex = this.rowData.indexOf(rowData);
		if (rowIndex >= 0) {
			update(rowIndex, rowData);
		}
	}
}
