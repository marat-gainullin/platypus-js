package com.eas.client.form.grid.columns.header;

import com.bearsoft.gwt.ui.widgets.grid.DraggableHeader;
import com.eas.client.form.grid.columns.UsualServiceColumn;
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
	
}
