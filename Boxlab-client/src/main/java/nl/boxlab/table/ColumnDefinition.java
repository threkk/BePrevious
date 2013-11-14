package nl.boxlab.table;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ColumnDefinition {

	public static Comparator<ColumnDefinition> COMPARATOR_ORDER = new Comparator<ColumnDefinition>() {

		public int compare(ColumnDefinition o1, ColumnDefinition o2) {
			return o1.getOrder() - o2.getOrder();
		}
	};

	private static final Logger logger = LoggerFactory
			.getLogger(ColumnDefinition.class);

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final String key;
	private final int minimumWidth;
	private final int maximumWidth;
	private int preferredWidth;
	private int order;
	private String title;
	private boolean visible;
	private TableCellRenderer renderer;
	private int lastCheckedRow = -1;

	/**
	 * @param info
	 */
	public ColumnDefinition(Column info) {
		this.key = info.key();
		this.preferredWidth = info.preferredWidth();
		this.minimumWidth = info.minWidth();
		this.maximumWidth = info.maxWidth();
		this.order = info.order();
		this.title = info.title();
		this.visible = info.visible();
		this.renderer = createRenderer(info);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	private TableCellRenderer createRenderer(Column info) {
		Class<? extends TableCellRenderer> renderClass = info.renderer();
		TableCellRenderer renderer = null;
		if (!renderClass.isInterface()) {
			try {
				Constructor<? extends TableCellRenderer> constructor = renderClass
						.getDeclaredConstructor();
				constructor.setAccessible(true);
				renderer = constructor.newInstance();
			} catch (InvocationTargetException e) {
				logger.error(
						"Constructor for render class {} could not be executed",
						info.renderer());
			} catch (InstantiationException e) {
				logger.error("Can not instantiate renderer for column {}",
						info.key());
			} catch (IllegalAccessException e) {
				logger.error(
						"Constructor for render class {} is not accessible",
						info.renderer());
			} catch (NoSuchMethodException e) {
				logger.error("Constructor for render class {} was not found",
						info.renderer());
			}
		}

		return renderer;
	}

	public String getKey() {
		return key;
	}

	public int getLastCheckedRow() {
		return this.lastCheckedRow;
	}

	public int getMaximumWidth() {
		return maximumWidth;
	}

	public int getMinimumWidth() {
		return minimumWidth;
	}

	public int getOrder() {
		return order;
	}

	public int getPreferredWidth() {
		return preferredWidth;
	}

	public TableCellRenderer getRenderer() {
		return renderer;
	}

	public final String getTitle() {
		return this.title;
	}

	public abstract Object getValue(Object o);

	public abstract Class<?> getValueClass();

	public abstract boolean isEditable();

	public boolean isVisible() {
		return visible;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public void setLastCheckedRow(int row) {
		this.lastCheckedRow = row;
	}

	public void setOrder(int order) {
		this.pcs.firePropertyChange("order", this.order, this.order = order);
	}

	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}

	public final void setTitle(String title) {
		this.pcs.firePropertyChange("title", this.title, this.title = title);
	}

	public abstract void setValue(Object dest, Object value);

	public void setVisible(boolean visible) {
		this.pcs.firePropertyChange("visible", this.visible,
				this.visible = visible);
	}
}
