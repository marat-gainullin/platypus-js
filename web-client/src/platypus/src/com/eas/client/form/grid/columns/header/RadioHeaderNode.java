package com.eas.client.form.grid.columns.header;

import com.bearsoft.gwt.ui.widgets.grid.DraggableHeader;
import com.eas.client.form.grid.columns.RadioServiceColumn;
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
	
}
