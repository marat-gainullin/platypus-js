package com.eas.client.gxtcontrols.wrappers.handled;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.gxtcontrols.published.PublishedCell;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusDateField;
import com.google.gwt.core.client.JavaScriptObject;
import com.sencha.gxt.cell.core.client.form.DateCell;

public class PlatypusDateHandledField extends PlatypusDateField {

	protected DateCell dateCell;
	protected ModelElementRef modelElement;
	protected JavaScriptObject cellFunction;

	public PlatypusDateHandledField(DateCell aCell) {
		super(aCell);
		dateCell = aCell;
	}

	public JavaScriptObject getCellFunction() {
		return cellFunction;
	}

	public void setCellFunction(JavaScriptObject aValue) {
		if (aValue != cellFunction) {
			cellFunction = aValue;
			redraw();
		}
	}

	public ModelElementRef getModelElement() {
		return modelElement;
	}

	public void setModelElement(ModelElementRef aValue) {
		modelElement = aValue;
	}

	@Override
	public void setValue(Date value, boolean fireEvents, boolean redraw) {
		super.setValue(value, fireEvents, redraw);
		if (!redraw && cellFunction != null)
			redraw();
	}

	@Override
	protected void onRedraw() {
		super.onRedraw();
		try {
			JavaScriptObject eventThis = modelElement != null ? modelElement.entity.getModel().getModule() : null;
			if (getParent() != null && getParent().getParent() instanceof PlatypusAdapterStandaloneField<?>) {
				PlatypusAdapterField<?> adapter = (PlatypusAdapterStandaloneField<?>) getParent().getParent();
				eventThis = adapter.getPublishedField();
			}
			PublishedCell cellToRender = modelElement != null && cellFunction != null ? ControlsUtils.calcStandalonePublishedCell(eventThis, cellFunction,
			        modelElement.entity.getRowset().getCurrentRow(), null, modelElement) : null;
			if (cellToRender != null) {
				cellToRender.styleToElement(getInputEl());
			}
		} catch (Exception ex) {
			Logger.getLogger(PlatypusDateHandledField.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
}
