package com.eas.form.grid.columns.header;

import com.eas.form.grid.DraggableHeader;
import com.eas.form.grid.columns.RadioServiceColumn;
import com.eas.form.published.widgets.model.ModelDecoratorBox;
import com.google.gwt.core.client.JavaScriptObject;

public class RadioHeaderNode extends ModelHeaderNode {

	public RadioHeaderNode() {
		super();
		column = new RadioServiceColumn();
		header = new DraggableHeader<JavaScriptObject>("\\", null, column, this);
		setResizable(false);
	}
	
	@Override
	public RadioHeaderNode lightCopy(){
		RadioHeaderNode copied = new RadioHeaderNode();
		copied.setColumn(column);
		copied.setHeader(header);
		return copied;
	}
	

	@Override
	public JavaScriptObject getJsEditor() {
		return null;
	}

	@Override
	public void setEditor(ModelDecoratorBox<?> aEditor) {
		// no op since node type
	}
}
