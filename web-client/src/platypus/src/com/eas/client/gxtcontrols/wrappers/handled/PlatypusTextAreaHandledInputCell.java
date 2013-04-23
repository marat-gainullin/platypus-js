package com.eas.client.gxtcontrols.wrappers.handled;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.gxtcontrols.published.PublishedCell;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.form.TextAreaInputCell;

public class PlatypusTextAreaHandledInputCell extends TextAreaInputCell {

	protected PlatypusTextHandledArea container;
	protected ModelElementRef modelElement;
	protected JavaScriptObject cellFunction;
	protected PublishedCell cellToRender;

	public PlatypusTextAreaHandledInputCell() {
		this(null, null);
	}

	public PlatypusTextAreaHandledInputCell(ModelElementRef aModelElement, JavaScriptObject aCellFunction) {
		super();
		modelElement = aModelElement;
		cellFunction = aCellFunction;
	}

	public JavaScriptObject getCellFunction() {
		return cellFunction;
	}

	public void setCellFunction(JavaScriptObject aValue) {
		cellFunction = aValue;
	}

	public ModelElementRef getModelElement() {
		return modelElement;
	}

	public void setModelElement(ModelElementRef aValue) {
		modelElement = aValue;
	}

	public PublishedCell consumePublishedCell() {
		PublishedCell consumed = cellToRender;
		cellToRender = null;
		return consumed;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		try {
			JavaScriptObject eventThis = modelElement != null ? modelElement.entity.getModel().getModule() : null;
			if (container != null && container.getParent() != null && container.getParent().getParent() instanceof PlatypusAdapterStandaloneField<?>) {
				PlatypusAdapterField<?> adapter = (PlatypusAdapterStandaloneField<?>) container.getParent().getParent();
				eventThis = adapter.getPublishedField();
			}
			cellToRender = modelElement != null && modelElement.entity.getRowset() != null ? ControlsUtils.calcStandalonePublishedCell(eventThis, cellFunction,
			        modelElement.entity.getRowset().getCurrentRow(), value, modelElement) : null;
			if (cellToRender != null) {
				String toRender = value;
				if (cellToRender.getDisplay() != null)
					toRender = cellToRender.getDisplay();
				super.render(context, toRender, sb);
			} else
				super.render(context, value, sb);
		} catch (Exception ex) {
			Logger.getLogger(PlatypusTextAreaHandledInputCell.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public void setContainer(PlatypusTextHandledArea aHandledField) {
		container = aHandledField;
	}
}
