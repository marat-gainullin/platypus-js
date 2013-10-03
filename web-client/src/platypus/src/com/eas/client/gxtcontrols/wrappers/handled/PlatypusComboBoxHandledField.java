package com.eas.client.gxtcontrols.wrappers.handled;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.ObjectKeyProvider;
import com.eas.client.gxtcontrols.model.ListStorePkFiller;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.gxtcontrols.published.PublishedCell;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusComboBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.ListStore;

public class PlatypusComboBoxHandledField extends PlatypusComboBox {

	protected ModelElementRef modelElement;
	protected ModelElementRef valueRef;
	protected ModelElementRef displayRef;
	protected JavaScriptObject cellFunction;

	protected PlatypusComboLabelHandledProvider labelProvider;
	protected ListStorePkFiller filler;

	public PlatypusComboBoxHandledField() {
		super(new ComboBoxCell<Object>(new ListStore<Object>(new ObjectKeyProvider()), new PlatypusComboLabelHandledProvider()));
		labelProvider = (PlatypusComboLabelHandledProvider) getCell().getLabelProvider();
		labelProvider.setContainer(this);
		setEditable(false);
		setTypeAhead(true);
		setTriggerAction(TriggerAction.ALL);
		filler = new ListStorePkFiller(getCell().getStore());
	}

	public ModelElementRef getModelElement() {
		return modelElement;
	}

	public void setModelElement(ModelElementRef aValue) {
		modelElement = aValue;
		labelProvider.setTargetValueRef(modelElement);
	}

	public ModelElementRef getValueRef() {
		return valueRef;
	}

	public void setValueRef(ModelElementRef aValue) {
		valueRef = aValue;
		labelProvider.setLookupValueRef(aValue);
		filler.setValuesRowsetHost(valueRef != null ? valueRef.entity : null);
	}

	public ModelElementRef getDisplayRef() {
		return displayRef;
	}

	public void setDisplayRef(ModelElementRef aValue) {
		displayRef = aValue;
		labelProvider.setDisplayValueRef(aValue);
		if (displayRef != null)
			filler.ensureRowset(displayRef.entity);
	}

	public JavaScriptObject getCellFunction() {
		return cellFunction;
	}

	public void setCellFunction(JavaScriptObject aValue) {
		if (aValue != cellFunction) {
			cellFunction = aValue;
			labelProvider.setCellFunction(cellFunction);
			redraw();
		}
	}

	@Override
	public void setValue(Object value, boolean fireEvents, boolean redraw) {
		super.setValue(value, fireEvents, redraw);
		if (!redraw && cellFunction != null)
			redraw();
	}

	protected PublishedCell cellToRender;

	@Override
	protected void onRedraw() {
		super.onRedraw();
		try {
			JavaScriptObject eventThis = modelElement != null && modelElement.entity != null && modelElement.entity.getModel() != null ? modelElement.entity.getModel().getModule() : null;
			// TODO: refactor to onTargetRedraw event
			if (getParent() != null && getParent().getParent() instanceof PlatypusAdapterStandaloneField<?>) {
				PlatypusAdapterField<?> adapter = (PlatypusAdapterStandaloneField<?>) getParent().getParent();
				ControlsUtils.reapplyStyle(adapter);
				eventThis = adapter.getPublishedField();

				if (cellFunction != null && modelElement != null && modelElement.entity != null && modelElement.entity.getRowset() != null) {
					Row currentRow = modelElement.entity.getRowset().getCurrentRow();
					Object currentRowValue = currentRow.getColumnObject(modelElement.getColIndex());
					cellToRender = ControlsUtils.calcStandalonePublishedCell(eventThis, cellFunction, currentRow, labelProvider.getLabel(currentRowValue), modelElement, cellToRender);
				}
				if (cellToRender != null) {
					if (cellToRender.getDisplayCallback() == null) {
						cellToRender.setDisplayCallback(new Runnable() {
							@Override
							public void run() {
								JavaScriptObject oldCellFunction = cellFunction;
								cellFunction = null;
								try {
									redraw(true);
								} finally {
									cellFunction = oldCellFunction;
								}
							}
						});
					}
					cellToRender.styleToElement(getInputEl());
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(PlatypusComboBoxHandledField.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

}
