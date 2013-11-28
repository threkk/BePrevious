package nl.boxlab.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Entity {

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private String _id;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, int oldValue, int newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return pcs.getPropertyChangeListeners();
	}
}
