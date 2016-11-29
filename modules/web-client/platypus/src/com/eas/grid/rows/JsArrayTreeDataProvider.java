package com.eas.grid.rows;

import com.eas.grid.processing.TreeDataProvider;
import com.google.gwt.core.client.JavaScriptObject;

public class JsArrayTreeDataProvider extends TreeDataProvider<JavaScriptObject> implements JsDataContainer {

	private final JsTree jsTree;
	
	public JsArrayTreeDataProvider(String aParentField, String aChildrenField, Runnable aOnResize) {
		super(new JsTree(aParentField, aChildrenField), aOnResize);
		jsTree = (JsTree)tree;
	}

	@Override
	public JavaScriptObject getData() {
		return jsTree.getData();
	}

	@Override
	public void setData(JavaScriptObject aValue) {
		jsTree.setData(aValue);
	}

	@Override
	public void changedItems(JavaScriptObject anArray) {
		jsTree.changedItems(anArray);
	}

	@Override
	public void addedItems(JavaScriptObject anArray) {
		jsTree.addedItems(anArray);
	}

	@Override
	public void removedItems(JavaScriptObject anArray) {
		jsTree.removedItems(anArray);
	}
}
