package nl.boxlab.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * @author Nils Dijk
 * @author Maarten Blokker
 */
public class TableController<T> extends Observable implements
        PropertyChangeListener {

	/**
	 * A TableColumnModelListener to track changes in the column model. If a
	 * column gets resized or moved, the definitions get updated accordingly
	 * 
	 * @author mblokker
	 * 
	 */
	private class ColumnModelListener implements TableColumnModelListener {

		public void columnAdded(TableColumnModelEvent e) {
		}

		public void columnMarginChanged(ChangeEvent e) {
			updateDefinitions();
		}

		public void columnMoved(TableColumnModelEvent e) {
			updateDefinitions();
		}

		public void columnRemoved(TableColumnModelEvent e) {
		}

		public void columnSelectionChanged(ListSelectionEvent e) {
		}

		private ColumnDefinition getDefinition(TableColumn column) {
			String key = String.valueOf(column.getIdentifier());
			for (ColumnDefinition col : TableController.this.columns) {
				if (col.getKey().equals(key)) {
					return col;
				}
			}
			return null;
		}

		private void updateDefinitions() {
			TableColumnModel columnModel = installedTable.getColumnModel();

			for (int i = 0; i < columnModel.getColumnCount(); i++) {
				TableColumn column = columnModel.getColumn(i);
				ColumnDefinition def = getDefinition(column);
				if (def == null) {
					continue;
				}

				def.setOrder(installedTable.convertColumnIndexToView(column
				        .getModelIndex()));
				def.setPreferredWidth(column.getPreferredWidth());
			}

			setChanged();
		}
	}

	/**
	 * A mouse listener used to show a menu to hide/show columns
	 * 
	 * @author mblokker
	 * 
	 */
	private class ColumnMouseListener extends MouseAdapter {

		@SuppressWarnings("serial")
		@Override
		public void mouseReleased(MouseEvent e) {
			Object source = e.getSource();
			if (!(source instanceof Component)) {
				// source must be a component
				return;
			}

			if (!SwingUtilities.isRightMouseButton(e)) {
				// only handle right mouse clicks
				return;
			}

			if (columns == null || columns.size() <= 1) {
				// only show the menu when there is more then 1 column
				return;
			}

			JPopupMenu menu = new JPopupMenu();
			for (final ColumnDefinition c : columns) {
				JCheckBoxMenuItem item = new JCheckBoxMenuItem(
				        new AbstractAction(c.getTitle()) {

					        public void actionPerformed(ActionEvent e) {
						        boolean visible = c.isVisible();
						        if (visible && getVisibleColumns().size() == 1) {
							        // dont remove any columns if there is only
							        // 1 left
							        return;
						        }

						        c.setVisible(!visible);
					        }
				        });
				item.setSelected(c.isVisible());
				menu.add(item);
			}
			menu.show((Component) source, e.getX(), e.getY());
		}
	}

	private List<ColumnDefinition> columns;

	private List<ColumnDefinition> visibleColumns = null;
	private final List<T> rowData = new ArrayList<T>();
	private JTable installedTable;

	private TableViewModel<T> model;
	private TableRowFilter<T> filter;

	public TableController(TableViewDefinition view, ResourceBundle bundle) {
		this.columns = TableColumnFactory.createColumnDefinitions(view, bundle);

		for (ColumnDefinition column : this.columns) {
			column.addPropertyChangeListener(this);
		}

		updateColumns();
	}

	public void add(T data) {
		this.rowData.add(data);

		if (this.model == null) {
			return;
		}

		if (this.filter == null || this.filter.accept(data)) {
			this.model.add(data);
		}
	}

	public void addAll(Collection<? extends T> data) {
		this.rowData.addAll(data);

		if (this.model == null) {
			return;
		}

		List<T> rowData = new ArrayList<T>(data);
		if (this.filter != null) {
			Iterator<T> it = rowData.iterator();
			while (it.hasNext()) {
				if (!this.filter.accept(it.next())) {
					it.remove();
				}
			}
		}

		this.model.addAll(rowData);
	}

	public void clear() {
		this.rowData.clear();

		if (this.model != null) {
			this.model.clear();
		}
	}

	public void fireDataChanged() {
		this.model.fireTableDataChanged();
	}

	public TableRowFilter<T> getFilter() {
		return filter;
	}

	protected JTable getInstalledTable() {
		return this.installedTable;
	}

	public Properties getProperties() {
		Properties p = new Properties();

		for (ColumnDefinition column : columns) {
			p.put(column.getKey() + ".preferredWidth",
			        Integer.toString(column.getPreferredWidth()));
			p.put(column.getKey() + ".order ",
			        Integer.toString(column.getOrder()));
			p.put(column.getKey() + ".title ", column.getTitle());
			p.put(column.getKey() + ".visible",
			        Boolean.valueOf(column.isVisible()).toString());
		}

		return p;
	}

	public T getSelectedRow() {
		if (this.installedTable == null) {
			return null;
		}
		int selectedIndex = getSelectedRowIndex();
		if (selectedIndex >= 0) {
			return model.getRowAt(selectedIndex);
		} else {
			return null;
		}
	}

	public int getSelectedRowIndex() {
		if (this.installedTable == null) {
			return -1;
		}

		int index = this.installedTable.getSelectedRow();
		if (index >= 0) {
			return this.installedTable.convertRowIndexToModel(index);
		} else {
			return -1;
		}
	}

	protected List<ColumnDefinition> getVisibleColumns() {
		return visibleColumns;
	}

	public boolean hasNextItem() {
		int selectedRow = this.installedTable.getSelectedRow();
		int rowCount = this.installedTable.getRowCount();

		if (selectedRow < 0) {
			return false;
		}

		return (selectedRow + 1) < rowCount;
	}

	public boolean hasPreviousItem() {
		int selectedRow = this.installedTable.getSelectedRow();

		if (selectedRow < 0) {
			return false;
		}

		return (selectedRow - 1) >= 0;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		String property = evt.getPropertyName();
		if (!(source instanceof ColumnDefinition) || property == null
		        || this.model == null) {
			// monitor property changes of the column definitions
			return;
		}

		if (property.equals("title")) {
			this.model.fireTableDataChanged();
		} else if (property.equals("visible")) {
			updateColumns();
		}

		setChanged();
		notifyObservers();
	}

	public void remove(T data) {
		int index = this.rowData.indexOf(data);

		if (index < 0) {
			return;
		}

		this.rowData.remove(index);

		if (this.model != null) {
			this.model.remove(data);
		}
	}

	public void replace(T oldData, T newData) {
		int index = this.rowData.indexOf(oldData);

		if (index < 0) {
			return;
		}

		this.rowData.set(index, newData);

		if (this.model != null) {
			this.model.replace(oldData, newData);
		}
	}

	public T selectNextItem() {
		if (!hasNextItem()) {
			return null;
		}

		int selectedRow = this.installedTable.getSelectedRow();

		this.installedTable.getSelectionModel().setSelectionInterval(
		        selectedRow + 1, selectedRow + 1);

		return getSelectedRow();
	}

	public T selectPreviousItem() {
		if (!hasPreviousItem()) {
			return null;
		}

		int selectedRow = this.installedTable.getSelectedRow();

		this.installedTable.getSelectionModel().setSelectionInterval(
		        selectedRow - 1, selectedRow - 1);

		return getSelectedRow();
	}

	public void setFilter(TableRowFilter<T> filter) {
		this.filter = filter;

		List<T> rowData = this.rowData;
		if (filter != null) {
			rowData = new ArrayList<T>(this.rowData.size());
			for (T t : this.rowData) {
				if (this.filter.accept(t)) {
					rowData.add(t);
				}
			}
		}

		this.model.set(rowData);
	}

	public void setProperties(Properties p) {
		if (p == null) {
			return;
		}

		for (ColumnDefinition column : this.columns) {
			try {
				column.setPreferredWidth(Integer.parseInt(p.getProperty(column
				        .getKey() + ".preferredWidth")));
				column.setOrder(Integer.parseInt(p.getProperty(column.getKey()
				        + ".order ")));
				column.setTitle(p.getProperty(column.getKey() + ".title "));
				column.setVisible(Boolean.valueOf(p.getProperty(column.getKey()
				        + ".visible")));
			} catch (NumberFormatException e) {
				continue;
			}
		}

		updateColumns();

		if (this.installedTable != null) {
			this.model = new TableViewModel<T>(this);
			this.model.addAll(this.rowData);

			this.installedTable.setModel(this.model);
			this.installedTable.getColumnModel().addColumnModelListener(
			        new ColumnModelListener());
			this.installedTable.getTableHeader().addMouseListener(
			        new ColumnMouseListener());

			updateColumnModel();
		}
	}

	public void setSelectedRow(T selected) {
		if (this.installedTable == null) {
			return;
		}

		int index = this.model.getRowOf(selected);
		if (index >= 0) {
			setSelectedRowIndex(this.installedTable
			        .convertRowIndexToView(index));
		}
	}

	public void setSelectedRowIndex(int rowIndex) {
		if (rowIndex < 0 || rowIndex > this.installedTable.getRowCount()) {
			return;
		}

		this.installedTable.getSelectionModel().setSelectionInterval(rowIndex,
		        rowIndex);
		this.installedTable.scrollRectToVisible(this.installedTable
		        .getCellRect(rowIndex, 0, true));
	}

	public JTable setup(JTable table) {
		if (this.installedTable != null) {
			throw new IllegalStateException(
			        "The TableViewController is already installed on a JTable");
		}

		this.model = new TableViewModel<T>(this);
		this.model.addAll(this.rowData);

		table.setModel(this.model);
		table.getColumnModel()
		        .addColumnModelListener(new ColumnModelListener());
		table.getTableHeader().addMouseListener(new ColumnMouseListener());

		this.installedTable = table;
		this.installedTable
		        .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.installedTable.setFillsViewportHeight(true);

		updateColumns();

		return table;
	}

	public void update(int index, T newData) {
		T oldData = this.rowData.get(index);

		this.rowData.set(index, newData);

		if (this.model != null) {
			int modelIndex = this.model.getRowOf(oldData);
			this.model.update(modelIndex, newData);
		}
	}

	public void update(T newData) {
		int index = this.rowData.indexOf(newData);

		if (index < 0) {
			return;
		}

		update(index, newData);
	}

	private void updateColumnModel() {
		if (this.installedTable == null) {
			return;
		}

		this.model.fireTableStructureChanged();

		TableColumnModel columnModel = this.installedTable.getColumnModel();
		for (int i = 0; i < this.visibleColumns.size(); i++) {
			ColumnDefinition def = this.visibleColumns.get(i);
			TableColumn column = columnModel.getColumn(i);

			TableCellRenderer renderer = def.getRenderer();
			String key = def.getKey();
			int min = def.getMinimumWidth();
			int pref = def.getPreferredWidth();
			int max = def.getMaximumWidth();

			column.setIdentifier(key);

			if (renderer != null) {
				column.setCellRenderer(renderer);
			}

			if (min >= 0) {
				column.setMinWidth(min);
			}
			if (pref >= 0) {
				column.setPreferredWidth(pref);
			}
			if (max >= 0) {
				column.setMaxWidth(max);
			}
		}

		notifyObservers();
	}

	private void updateColumns() {
		Collections.sort(this.columns, ColumnDefinition.COMPARATOR_ORDER);

		this.visibleColumns = new ArrayList<ColumnDefinition>();
		for (ColumnDefinition column : TableController.this.columns) {
			if (column.isVisible()) {
				this.visibleColumns.add(column);
			}
		}
		updateColumnModel();
	}
}
