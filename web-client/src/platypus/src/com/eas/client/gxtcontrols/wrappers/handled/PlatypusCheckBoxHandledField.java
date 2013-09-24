package com.eas.client.gxtcontrols.wrappers.handled;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.gxtcontrols.published.PublishedCell;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.sencha.gxt.cell.core.client.form.CheckBoxCell;

public class PlatypusCheckBoxHandledField extends PlatypusCheckBox {

	protected ModelElementRef modelElement;
	protected JavaScriptObject cellFunction;

	public PlatypusCheckBoxHandledField(CheckBoxCell aCell) {
		super(aCell);
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
	public void setValue(Boolean value, boolean fireEvents, boolean redraw) {
		super.setValue(value, fireEvents, redraw);
		if (!redraw && cellFunction != null)
			redraw();
	}

	@Override
	protected void onRedraw() {
		super.onRedraw();
		try {
			JavaScriptObject eventThis = modelElement != null ? modelElement.entity.getModel().getModule() : null;
			// TODO: refactor to onTargetRedraw event
			if (getParent() != null && getParent().getParent() instanceof PlatypusAdapterStandaloneField<?>) {
				PlatypusAdapterStandaloneField<?> adapter = (PlatypusAdapterStandaloneField<?>) getParent().getParent();
				eventThis = adapter.getPublishedField();
				PublishedCell cellToRender = modelElement != null && cellFunction != null ? ControlsUtils.calcStandalonePublishedCell(eventThis, cellFunction, modelElement.entity.getRowset()
				        .getCurrentRow(), null, modelElement) : null;
				if (cellToRender != null) {
					cellToRender.styleToElement(getElement());
				} else {
					ControlsUtils.reapplyStyle(adapter);
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(PlatypusCheckBoxHandledField.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
}
