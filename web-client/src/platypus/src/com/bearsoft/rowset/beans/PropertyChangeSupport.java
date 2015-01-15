/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bearsoft.rowset.Rowset;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author mg
 */
public class PropertyChangeSupport {

	protected Object source;
	protected Set<PropertyChangeListener> listeners = new HashSet<>();
	protected Map<String, Set<PropertyChangeListener>> namedListeners = new HashMap<>();

	public PropertyChangeSupport(Object aSource) {
		source = aSource;
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return listeners.toArray(new PropertyChangeListener[] {});
	}

	public void addPropertyChangeListener(PropertyChangeListener pl) {
		listeners.add(pl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pl) {
		listeners.remove(pl);
	}

	public void addPropertyChangeListener(String aPropertyName, PropertyChangeListener pl) {
		Set<PropertyChangeListener> lses = namedListeners.get(aPropertyName);
		if (lses == null) {
			lses = new HashSet<>();
			namedListeners.put(aPropertyName, lses);
		}
		lses.add(pl);
	}

	public void removePropertyChangeListener(String aPropertyName, PropertyChangeListener pl) {
		Set<PropertyChangeListener> lses = namedListeners.get(aPropertyName);
		if (lses != null) {
			lses.remove(pl);
			if (lses.isEmpty())
				namedListeners.remove(aPropertyName);
		}
	}

	public void firePropertyChange(PropertyChangeEvent event) {
		Set<PropertyChangeListener> lses = namedListeners.get(event.getPropertyName());
		if (lses != null) {
			for (PropertyChangeListener l : lses.toArray(new PropertyChangeListener[] {}))
				l.propertyChange(event);
		}
		for (PropertyChangeListener l : listeners.toArray(new PropertyChangeListener[] {}))
			l.propertyChange(event);
	}

	public void firePropertyChange(String aPropertyName, boolean oldValue, boolean newValue) {
		firePropertyChange(new PropertyChangeEvent(source, aPropertyName, oldValue, newValue));
	}

	public void firePropertyChange(String aPropertyName, Rowset oldValue, Rowset newValue) {
		firePropertyChange(new PropertyChangeEvent(source, aPropertyName, oldValue, newValue));
	}

	public void firePropertyChange(String aPropertyName, String oldValue, String newValue) {
		firePropertyChange(new PropertyChangeEvent(source, aPropertyName, oldValue, newValue));
	}

	public void firePropertyChange(String aPropertyName, Object oldValue, Object newValue) {
		firePropertyChange(new PropertyChangeEvent(source, aPropertyName, oldValue, newValue));
	}

	public static native JavaScriptObject publishEvent(PropertyChangeEvent evt)/*-{
		return {
			propertyName : evt.@com.bearsoft.rowset.beans.PropertyChangeEvent::getPropertyName()(),
			oldValue : $wnd.P.boxAsJs(evt.@com.bearsoft.rowset.beans.PropertyChangeEvent::getJsOldValue()()),
			newValue : $wnd.P.boxAsJs(evt.@com.bearsoft.rowset.beans.PropertyChangeEvent::getJsNewValue()()),
			unwrap : function() {
				return evt;
			}
		};
	}-*/;

	public static native JavaScriptObject publishListener(PropertyChangeListener aListener)/*-{
		return function(aNewValue){
			var evt = @com.bearsoft.rowset.beans.PropertyChangeEvent::new(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)(null, null, null, $wnd.P.boxAsJava(aNewValue));
			aListener.@com.bearsoft.rowset.beans.PropertyChangeListener::propertyChange(Lcom/bearsoft/rowset/beans/PropertyChangeEvent;)(evt);
		};
	}-*/;
}
