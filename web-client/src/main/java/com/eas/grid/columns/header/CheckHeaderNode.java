package com.eas.grid.columns.header;

import com.eas.bound.ModelDecoratorBox;
import com.eas.grid.DraggableHeader;
import com.eas.grid.columns.CheckServiceColumn;
import com.google.gwt.core.client.JavaScriptObject;

public class CheckHeaderNode extends ModelHeaderNode {

	public CheckHeaderNode() {
		super();
		column = new CheckServiceColumn();
		header = new DraggableHeader<JavaScriptObject>("\\", null, column, this);
		setResizable(false);
	}

	@Override
	public CheckHeaderNode lightCopy() {
		CheckHeaderNode copied = new CheckHeaderNode();
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
