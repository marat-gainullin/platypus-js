package com.eas.client.form.grid.columns.header;

import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.eas.client.form.grid.columns.CheckServiceColumn;
import com.google.gwt.core.client.JavaScriptObject;

public class CheckHeaderNode extends HeaderNode<JavaScriptObject, Object>{

	public CheckHeaderNode() {
		super();
		column = new CheckServiceColumn();
	}

}
