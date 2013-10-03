package com.eas.client.gxtcontrols.wrappers.handled;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.gxtcontrols.published.PublishedCell;
import com.eas.client.gxtcontrols.wrappers.component.FormattedTextInputCell;
import com.eas.client.gxtcontrols.wrappers.component.FormattedTextPropertyEditor;
import com.eas.client.gxtcontrols.wrappers.component.ObjectFormat;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class PlatypusFormattedTextHandledInputCell extends FormattedTextInputCell{

	protected PlatypusFormattedTextHandledField container;
	protected ModelElementRef modelElement;
	protected JavaScriptObject cellFunction;
	protected PublishedCell cellToRender;

	public PlatypusFormattedTextHandledInputCell(ObjectFormat aFormat) {
		this(aFormat, null, null);
	}

	public PlatypusFormattedTextHandledInputCell(ObjectFormat aFormat, ModelElementRef aModelElement, JavaScriptObject aCellFunction) {
		super(new FormattedTextPropertyEditor(aFormat));
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

	public PublishedCell getPublishedCell() {
		return cellToRender;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb) {
		try {
			JavaScriptObject eventThis = modelElement != null && modelElement.entity != null && modelElement.entity.getModel() != null ? modelElement.entity.getModel().getModule() : null;
			if (container != null && container.getParent() != null && container.getParent().getParent() instanceof PlatypusAdapterStandaloneField<?>) {
				PlatypusAdapterField<?> adapter = (PlatypusAdapterStandaloneField<?>) container.getParent().getParent();
				eventThis = adapter.getPublishedField();
			}
			if(cellFunction != null && modelElement != null && modelElement.entity != null && modelElement.entity.getRowset() != null){
				cellToRender = ControlsUtils.calcStandalonePublishedCell(eventThis, cellFunction,
				        modelElement.entity.getRowset().getCurrentRow(), getPropertyEditor().render(value), modelElement, cellToRender);
			}
			if (cellToRender != null) {
				if(cellToRender.getDisplayCallback() == null){
					cellToRender.setDisplayCallback(new Runnable(){
						@Override
						public void run() {
							JavaScriptObject oldCellFunction = cellFunction;
							cellFunction = null;
							try{
								container.redraw();
							}finally{
								cellFunction = oldCellFunction;
							}
						}
					});
				}
				String toRender = getPropertyEditor().render(value);
				if (cellToRender.getDisplay() != null)
					toRender = cellToRender.getDisplay();
				super.render(context, toRender, sb);
			} else
				super.render(context, value, sb);
		} catch (Exception ex) {
			Logger.getLogger(PlatypusFormattedTextHandledInputCell.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public void setContainer(PlatypusFormattedTextHandledField aHandledField) {
		container = aHandledField;
	}
}
