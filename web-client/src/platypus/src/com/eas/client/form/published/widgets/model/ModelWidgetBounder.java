package com.eas.client.form.published.widgets.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.events.RowsetNetErrorEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.eas.client.converters.RowValueConverter;
import com.eas.client.model.Model;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.xml.client.Element;

public class ModelWidgetBounder<T> extends ModelElementRef implements ValueChangeHandler<T>, RowsetListener {

	protected HandlerRegistration valueChangeHandlerRegistration;
	protected HasValue<T> widget;
	protected RowValueConverter<T> converter;

	public ModelWidgetBounder(Element aTag, final Model aModel, RowValueConverter<T> aConverter) throws Exception {
		super(aTag, aModel);
		converter = aConverter;
	}

	public ModelWidgetBounder(final Model aModel, String aEntityId, String aFieldName, boolean aIsField, RowValueConverter<T> aConverter) throws Exception {
		super(aModel, aEntityId, aFieldName, aIsField);
		converter = aConverter;
	}

	protected void registerOnRowsetEvents() {
		assert entity != null : "Entity " + entityId + " missing. " + (isField ? "Field" : "Parameter") + " name: " + fieldName;
		assert entity.getRowset() != null : "Entity data array is missing";
		entity.getRowset().addRowsetListener(this);
		rowsetRequeried(null);
	}

	public void unregisterFromRowsetEvents() {
		assert entity.getRowset() != null;
		entity.getRowset().removeRowsetListener(this);
	}

	@Override
	public void resolveField() throws Exception {
		super.resolveField();
		registerOnRowsetEvents();
	}

	public HasValue<T> getWidget() {
		return widget;
	}

	public void setWidget(HasValue<T> aWidget) {
		if (widget != aWidget) {
			if (valueChangeHandlerRegistration != null) {
				valueChangeHandlerRegistration.removeHandler();
				valueChangeHandlerRegistration = null;
			}
			widget = aWidget;
			if (widget != null) {
				valueChangeHandlerRegistration = widget.addValueChangeHandler(this);
			}
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<T> event) {
		if (entity.getRowset() != null) {
			try {
				if (!entity.getRowset().isBeforeFirst() && !entity.getRowset().isAfterLast()) {
					Object prevValue = entity.getRowset().getObject(getColIndex());
					entity.getRowset().updateObject(getColIndex(), event.getValue());
					Object afterValue = entity.getRowset().getObject(getColIndex());
					if (prevValue == null ? afterValue == null : prevValue.equals(afterValue)) {
						setValueToControl();
					}
				}
			} catch (Exception ex) {
				Logger.getLogger(ModelWidgetBounder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}

	protected void setValueToControl() {
		if (widget != null) {
			try {
				Object value = null;
				Rowset eRowset = entity.getRowset();
				if (eRowset != null && !eRowset.isBeforeFirst() && !eRowset.isAfterLast()) {
					value = eRowset.getObject(getColIndex());
				}
				if (widget instanceof ModelCombo) {
					((ModelCombo) widget).setJsValue(value, false);
				} else {
					widget.setValue(converter.convert(value), false);
				}
			} catch (Exception ex) {
				Logger.getLogger(ModelWidgetBounder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}

	@Override
	public boolean willScroll(RowsetScrollEvent event) {
		return true;
	}

	@Override
	public boolean willFilter(RowsetFilterEvent event) {
		return true;
	}

	@Override
	public boolean willRequery(RowsetRequeryEvent event) {
		return true;
	}

	@Override
	public void beforeRequery(RowsetRequeryEvent event) {
	}

	@Override
	public boolean willInsertRow(RowsetInsertEvent event) {
		return true;
	}

	@Override
	public boolean willChangeRow(RowChangeEvent event) {
		return true;
	}

	@Override
	public boolean willDeleteRow(RowsetDeleteEvent event) {
		return true;
	}

	@Override
	public boolean willSort(RowsetSortEvent event) {
		return true;
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
	public void rowsetNetError(RowsetNetErrorEvent event) {
	}

	@Override
	public void rowsetSaved(RowsetSaveEvent event) {
		setValueToControl();
	}

	@Override
	public void rowsetRolledback(RowsetRollbackEvent event) {
		setValueToControl();
	}

	@Override
	public void rowsetScrolled(RowsetScrollEvent event) {
		setValueToControl();
	}

	@Override
	public void rowsetSorted(RowsetSortEvent event) {
		setValueToControl();
	}

	@Override
	public void rowInserted(RowsetInsertEvent event) {
		setValueToControl();
	}

	@Override
	public void rowChanged(RowChangeEvent event) {
		if (event.getFieldIndex() == getColIndex())
			setValueToControl();
	}

	@Override
	public void rowDeleted(RowsetDeleteEvent event) {
		setValueToControl();
	}
}
