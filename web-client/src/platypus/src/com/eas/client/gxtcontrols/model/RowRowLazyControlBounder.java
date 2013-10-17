package com.eas.client.gxtcontrols.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.eas.client.gxtcontrols.grid.valueproviders.RowRowValueProvider;
import com.eas.client.model.Model;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.xml.client.Element;

public class RowRowLazyControlBounder extends LazyControlBounder<Row> {

	protected class RowsetsChangeAlerter extends RowsetAdapter {

		protected ModelElementRef fieldRef;

		public RowsetsChangeAlerter(ModelElementRef aFieldRef) {
			super();
			fieldRef = aFieldRef;
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
		boolean relevant = false;
		if (aLookupValueRef != null && aLookupValueRef.entity != null) {
			aLookupValueRef.entity.getRowset().addRowsetListener(new RowsetsChangeAlerter(aLookupValueRef));
			relevant = true;
		}
		if (aDisplayValueRef != null && aDisplayValueRef.entity != null) {
			aDisplayValueRef.entity.getRowset().addRowsetListener(new RowsetsChangeAlerter(aDisplayValueRef));
			relevant = true;
		}
		if (relevant)
			rowsetRequeried(null);
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
