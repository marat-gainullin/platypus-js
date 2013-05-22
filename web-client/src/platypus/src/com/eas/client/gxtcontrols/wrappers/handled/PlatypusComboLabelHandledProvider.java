package com.eas.client.gxtcontrols.wrappers.handled;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.combo.ComboLabelProvider;
import com.eas.client.gxtcontrols.published.PublishedCell;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.google.gwt.core.client.JavaScriptObject;

public class PlatypusComboLabelHandledProvider extends ComboLabelProvider {

	protected PlatypusComboBoxHandledField container;
	protected JavaScriptObject cellFunction;

	public PlatypusComboLabelHandledProvider() {
		super();
	}

	public JavaScriptObject getCellFunction() {
		return cellFunction;
	}

	public void setCellFunction(JavaScriptObject aValue) {
		cellFunction = aValue;
	}

	@Override
	public String getLabel(Object aValue) {
		String label = super.getLabel(aValue);
		if (cellFunction != null && lookupValueRef != null && lookupValueRef.entity.getRowset() != null) {
			try {
				Row found = lookupValueRef.entity.find(lookupValueRef.getColIndex(), aValue);
				if(found != null){
					JavaScriptObject eventThis = lookupValueRef.entity.getModel().getModule();
					if (container != null && container.getParent() != null && container.getParent().getParent() instanceof PlatypusAdapterStandaloneField<?>) {
						PlatypusAdapterField<?> adapter = (PlatypusAdapterStandaloneField<?>) container.getParent().getParent();
						eventThis = adapter.getPublishedField();
					}
					PublishedCell cellToRender = ControlsUtils.calcStandalonePublishedCell(eventThis, cellFunction, found, label, lookupValueRef);
					if (cellToRender != null) {
						label = cellToRender.getDisplay();
					}
				}
			} catch (Exception ex) {
				Logger.getLogger(PlatypusComboLabelHandledProvider.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
		return label;
	}

	public void setContainer(PlatypusComboBoxHandledField aHandledField) {
		container = aHandledField;
	}

}
