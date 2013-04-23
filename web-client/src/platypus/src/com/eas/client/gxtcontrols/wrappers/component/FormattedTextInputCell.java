package com.eas.client.gxtcontrols.wrappers.component;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.cell.core.client.form.TwinTriggerFieldCell;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;

public class FormattedTextInputCell extends TwinTriggerFieldCell<Object> {

	public FormattedTextInputCell(FormattedTextPropertyEditor propertyEditor) {
		this(propertyEditor, GWT.<TwinTriggerFieldAppearance> create(TwinTriggerFieldAppearance.class));
	}

	public FormattedTextInputCell(FormattedTextPropertyEditor propertyEditor, TwinTriggerFieldAppearance appearance) {
		super(appearance);
		setPropertyEditor(propertyEditor);
	}

	@Override
	public FormattedTextPropertyEditor getPropertyEditor() {
		return (FormattedTextPropertyEditor) super.getPropertyEditor();
	}

	@Override
	public void setPropertyEditor(PropertyEditor<Object> propertyEditor) {
		assert propertyEditor instanceof FormattedTextPropertyEditor;
		super.setPropertyEditor(propertyEditor);
	}
}