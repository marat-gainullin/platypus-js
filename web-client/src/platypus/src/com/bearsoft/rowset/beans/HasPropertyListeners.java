package com.bearsoft.rowset.beans;

public interface HasPropertyListeners {

	public PropertyChangeListener[] getPropertyChangeListeners();
		
	public void addPropertyChangeListener(String aPropertyName, PropertyChangeListener pl);
	
	public void removePropertyChangeListener(String aPropertyName, PropertyChangeListener pl);
	
	public void addPropertyChangeListener(PropertyChangeListener pl);

	public void removePropertyChangeListener(PropertyChangeListener pl);
}
