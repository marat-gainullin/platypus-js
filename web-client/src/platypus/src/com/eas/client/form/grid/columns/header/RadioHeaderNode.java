package com.eas.client.form.grid.columns.header;

import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.eas.client.form.grid.columns.RadioServiceColumn;
import com.google.gwt.core.client.JavaScriptObject;

public class RadioHeaderNode extends HeaderNode<JavaScriptObject, Object> {

	public RadioHeaderNode() {
		super();
		column = new RadioServiceColumn();
	}
}
