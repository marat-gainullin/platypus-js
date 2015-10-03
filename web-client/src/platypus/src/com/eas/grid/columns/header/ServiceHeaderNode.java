package com.eas.grid.columns.header;

import com.eas.bound.ModelDecoratorBox;
import com.eas.grid.DraggableHeader;
import com.eas.grid.columns.UsualServiceColumn;
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
