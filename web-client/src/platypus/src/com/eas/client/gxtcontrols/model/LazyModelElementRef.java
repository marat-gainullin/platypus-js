package com.eas.client.gxtcontrols.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Rowset;
import com.eas.client.beans.PropertyChangeEvent;
import com.eas.client.beans.PropertyChangeListener;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.xml.client.Element;

public class LazyModelElementRef extends ModelElementRef implements PropertyChangeListener {

	protected boolean rowsetPresent;

	public LazyModelElementRef(Element aTag, Model aModel) throws Exception {
		super(aTag, aModel);
	}

	public LazyModelElementRef(Model aModel, String aEntityId, String aFieldName, boolean aIsField) throws Exception {
		super(aModel, aEntityId, aFieldName, aIsField);
	}
	
	@Override
	protected void tryResolveField() throws Exception {
		if (entity instanceof ParametersEntity) {
			assert isField : "Parameter must be refereced as a field only! (Parameters entity has no own parameters)";
			field = entity.getFields().get(fieldName);
			rowsetPresent = true;
		} else {
			try {
				resolveField();
				entity.getChangeSupport().addPropertyChangeListener(this);
			} catch (Exception e) {
				Logger.getLogger(LazyControlBounder.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if ("rowset".equals(evt.getPropertyName()) && evt.getOldValue() == null && evt.getNewValue() != null) {
			assert evt.getNewValue() instanceof Rowset;
			assert evt.getSource() instanceof Entity;
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					((Entity) evt.getSource()).getChangeSupport().removePropertyChangeListener(LazyModelElementRef.this);
				}
			});
			rowsetPresent = true;
		}
	}
}
