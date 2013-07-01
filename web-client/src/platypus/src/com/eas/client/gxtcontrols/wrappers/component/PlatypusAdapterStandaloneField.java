package com.eas.client.gxtcontrols.wrappers.component;

import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.gxtcontrols.converters.RowValueConverter;
import com.eas.client.gxtcontrols.model.LazyControlBounder;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.core.client.JavaScriptObject;
import com.sencha.gxt.widget.core.client.form.Field;

public abstract class PlatypusAdapterStandaloneField<T> extends PlatypusAdapterField<T> {

	public PlatypusAdapterStandaloneField(Field<T> aTarget) {
		super(aTarget);
	}

	public abstract ModelElementRef getModelElement();

	public abstract void setModelElement(ModelElementRef aValue);

	public abstract void setOnRender(JavaScriptObject aValue);

	@Override
	protected JavaScriptObject getEventsThis() {
		return getPublishedField();
	}

	public com.bearsoft.rowset.metadata.Field getField() throws Exception {
		ModelElementRef el = getModelElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public void setField(com.bearsoft.rowset.metadata.Field aField, RowValueConverter<T> aConverter) throws Exception {
		ModelElementRef el = getModelElement();
		if (el instanceof LazyControlBounder<?>) {
			((LazyControlBounder<?>) el).setCellComponent(null);
			((LazyControlBounder<?>) el).unregisterFromRowsetEvents();
		}
		setModelElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			LazyControlBounder<T> newBound = new LazyControlBounder<T>(newModel, newEntity.getEntityId(), aField.getName(), newEntity instanceof ParametersEntity || !(aField instanceof Parameter),
			        aConverter);
			newBound.setCellComponent(target);
			setModelElement(newBound);
		}
		target.redraw();
	}

}
