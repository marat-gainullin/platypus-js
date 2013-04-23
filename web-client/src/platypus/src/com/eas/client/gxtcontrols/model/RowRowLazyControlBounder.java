package com.eas.client.gxtcontrols.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.eas.client.beans.PropertyChangeEvent;
import com.eas.client.beans.PropertyChangeListener;
import com.eas.client.gxtcontrols.grid.valueproviders.RowRowValueProvider;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.xml.client.Element;

public class RowRowLazyControlBounder extends LazyControlBounder<Row> {

	protected class RowsetsChangeAlerter extends RowsetAdapter implements PropertyChangeListener {

		protected ModelElementRef fieldRef;

		public RowsetsChangeAlerter(ModelElementRef aFieldRef) {
			super();
			fieldRef = aFieldRef;
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if ("rowset".equals(evt.getPropertyName()) && evt.getOldValue() == null && evt.getNewValue() != null) {
				assert evt.getNewValue() instanceof Rowset;
				assert evt.getSource() instanceof Entity;
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						((Entity) evt.getSource()).getChangeSupport().removePropertyChangeListener(RowsetsChangeAlerter.this);
					}
				});
			}
			Rowset rowset = (Rowset) evt.getNewValue();
			rowset.addRowsetListener(this);
			RowsetsChangeAlerter.this.rowsetRequeried(null);
		}

		@Override
		public void rowsetFiltered(RowsetFilterEvent event) {
			setValueToControl();
		}

		@Override
		public void rowsetRequeried(RowsetRequeryEvent event) {
			setValueToControl();
		}

		@Override
		public void rowsetRolledback(RowsetRollbackEvent event) {
			setValueToControl();
		}

		@Override
		public void rowChanged(RowChangeEvent event) {
			if (event.getFieldIndex() == fieldRef.getColIndex())
				setValueToControl();
		}

		@Override
		public void rowDeleted(RowsetDeleteEvent event) {
			setValueToControl();
		}

		@Override
		public void rowInserted(RowsetInsertEvent event) {
			setValueToControl();
		}

	}

	protected RowRowValueProvider valueProvider;

	public RowRowLazyControlBounder(Element aTag, Model aModel, ModelElementRef aLookupValueRef, ModelElementRef aDisplayValueRef) throws Exception {
		super(aTag, aModel, null);
		valueProvider = new RowRowValueProvider(entity, this, aLookupValueRef, aDisplayValueRef);
		if (aLookupValueRef != null && aLookupValueRef.entity != null) {
			if (aLookupValueRef.entity.getRowset() != null) {
				aLookupValueRef.entity.getRowset().addRowsetListener(new RowsetsChangeAlerter(aLookupValueRef));
			} else
				aLookupValueRef.entity.getChangeSupport().addPropertyChangeListener(new RowsetsChangeAlerter(aLookupValueRef));
		}
		if (aDisplayValueRef != null && aDisplayValueRef.entity != null) {
			if (aDisplayValueRef.entity.getRowset() != null) {
				aDisplayValueRef.entity.getRowset().addRowsetListener(new RowsetsChangeAlerter(aDisplayValueRef));
			} else
				aDisplayValueRef.entity.getChangeSupport().addPropertyChangeListener(new RowsetsChangeAlerter(aDisplayValueRef));
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<Row> event) {
		if (entity.getRowset() != null) {
			try {
				if (!entity.getRowset().isBeforeFirst() && !entity.getRowset().isAfterLast())
					valueProvider.setValue(entity.getRowset().getCurrentRow(), event.getValue());
			} catch (Exception ex) {
				Logger.getLogger(LazyControlBounder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}

	@Override
	protected void setValueToControl() {
		if (entity.getRowset() != null && valueProvider != null) {
			try {
				if (!entity.getRowset().isBeforeFirst() && !entity.getRowset().isAfterLast()) {
					Row value = valueProvider.getValue(entity.getRowset().getCurrentRow());
					cellComponent.setValue(value, false, true);
				}
			} catch (Exception ex) {
				Logger.getLogger(LazyControlBounder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}
}
