package com.eas.client.form.grid.columns.header;

import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.eas.client.form.grid.columns.UsualServiceColumn;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.cellview.client.Header;

public class ServiceHeaderNode extends HeaderNode<JavaScriptObject, Object> {

	public ServiceHeaderNode() {
		super();
		column = new UsualServiceColumn();
	}
}
