package com.eas.client.form.grid.rows;

import com.bearsoft.gwt.ui.widgets.grid.processing.ChildrenFetcher;
import com.bearsoft.gwt.ui.widgets.grid.processing.TreeDataProvider;
import com.google.gwt.core.client.JavaScriptObject;

public class JsArrayTreeDataProvider extends TreeDataProvider<JavaScriptObject> implements JsDataContainer {

	public JsArrayTreeDataProvider(String aParentField, String aChildrenField, Runnable aOnResize, ChildrenFetcher<JavaScriptObject> aChildrenFetcher) {
		super(new JsTree(aParentField, aChildrenField, aOnResize, null), aOnResize, aChildrenFetcher);
	}

	@Override
	public JavaScriptObject getData() {
		return ((JsTree)tree).getData();
	}

	@Override
	public void setData(JavaScriptObject aValue) {
		((JsTree)tree).setData(aValue);
	}
}
