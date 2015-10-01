package com.eas.form.grid.columns.header;

import com.eas.form.grid.DraggableHeader;
import com.eas.form.grid.columns.UsualServiceColumn;
import com.eas.form.published.widgets.model.ModelDecoratorBox;
import com.google.gwt.core.client.JavaScriptObject;

public class ServiceHeaderNode extends ModelHeaderNode {

	public ServiceHeaderNode() {
		super();
		column = new UsualServiceColumn();
		header = new DraggableHeader<JavaScriptObject>("\\", null, column, this);
		setResizable(false);
	}
	
	@Override
	public ServiceHeaderNode lightCopy(){
		ServiceHeaderNode copied = new ServiceHeaderNode();
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
