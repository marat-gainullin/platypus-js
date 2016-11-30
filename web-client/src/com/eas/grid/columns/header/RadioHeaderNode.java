package com.eas.grid.columns.header;

import com.eas.bound.ModelDecoratorBox;
import com.eas.grid.DraggableHeader;
import com.eas.grid.columns.RadioServiceColumn;
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
